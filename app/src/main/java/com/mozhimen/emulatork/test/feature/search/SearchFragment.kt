package com.mozhimen.emulatork.test.feature.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.mozhimen.emulatork.test.shared.GamesAdapter
import com.mozhimen.emulatork.test.shared.RecyclerViewFragment
import com.mozhimen.emulatork.test.shared.covers.CoverLoader
import com.mozhimen.emulatork.util.coroutines.launchOnState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * @ClassName SearchFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SearchFragment : RecyclerViewFragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase

    @Inject
    lateinit var gameInteractor: GameInteractor

    @Inject
    lateinit var coverLoader: CoverLoader

    private lateinit var searchViewModel: SearchViewModel

    private val searchDebounce = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = SearchViewModel.Factory(retrogradeDb)
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        initializeMenuProvider()

        val gamesAdapter = GamesAdapter(R.layout.layout_game_list, gameInteractor, coverLoader)

        launchOnState(Lifecycle.State.RESUMED) {
            searchViewModel.searchResults
                .collect { gamesAdapter.submitData(viewLifecycleOwner.lifecycle, it) }
        }

        launchOnState(Lifecycle.State.RESUMED) {
            searchDebounce.debounce(1000)
                .collect { searchViewModel.queryString.value = it }
        }

        gamesAdapter.addLoadStateListener { loadState ->
            updateEmptyViewVisibility(loadState, gamesAdapter.itemCount)
        }

        recyclerView?.apply {
            adapter = gamesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initializeMenuProvider() {
        val menuHost: MenuHost = requireActivity() as MenuHost
        val menuProvider = object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

            override fun onMenuClosed(menu: Menu) {}

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val searchItem = menu.findItem(R.id.action_search)
                setupSearchMenuItem(searchItem)
            }
        }
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearchMenuItem(searchItem: MenuItem) {
        val onExpandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                activity?.onBackPressed()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem) = true
        }
        searchItem.setOnActionExpandListener(onExpandListener)

        searchItem.expandActionView()

        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE

        val onQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchDebounce.value = newText
                return true
            }
        }
        searchView.setOnQueryTextListener(onQueryTextListener)
        searchView.setQuery(searchViewModel.queryString.value, false)
    }

    @dagger.Module
    class Module
}
