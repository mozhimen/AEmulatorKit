package com.mozhimen.emulatork.test.feature.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.mozhimen.basick.utilk.android.view.applyVisibleIfElseGone
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.mozhimen.emulatork.test.shared.GamesAdapter
import com.mozhimen.emulatork.test.shared.RecyclerViewFragment
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
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

    private lateinit var searchViewModel: SearchViewModel

    private var searchSubject: PublishSubject<String> = PublishSubject.create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        setupSearchMenuItem(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel = ViewModelProviders.of(this, SearchViewModel.Factory(retrogradeDb))
            .get(SearchViewModel::class.java)
    }

    @SuppressLint("AutoDispose")
    private fun setupSearchMenuItem(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.expandActionView()
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                activity?.onBackPressed()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem) = true
        })

        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setQuery(searchViewModel.queryString.value, false)
        searchView.queryTextChanges()
            .debounce(1, TimeUnit.SECONDS)
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(searchSubject)
    }

    override fun onResume() {
        super.onResume()

        val gamesAdapter = GamesAdapter(R.layout.layout_game_list, gameInteractor)
        searchViewModel.searchResults.observe(this, Observer {
            gamesAdapter.submitList(it)
        })

        searchViewModel.emptyViewVisible.observe(this, Observer {
            emptyView?.applyVisibleIfElseGone(it)
        })

        searchSubject
            .distinctUntilChanged()
            .autoDispose(scope())
            .subscribe { searchViewModel.queryString.postValue(it) }

        recyclerView?.apply {
            adapter = gamesAdapter
            layoutManager = LinearLayoutManager(context)
        }
        restoreRecyclerViewState()
    }

    @dagger.Module
    class Module
}
