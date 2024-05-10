package com.mozhimen.emulatork.test.feature.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mozhimen.basick.utilk.android.view.applyVisibleIfElseGone
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.mozhimen.emulatork.test.shared.GamesAdapter
import com.mozhimen.emulatork.test.shared.RecyclerViewFragment
import com.mozhimen.xmlk.recyclerk.decoration.RecyclerKDecorationSpaceGrid
import com.mozhimen.xmlk.recyclerk.manager.RecyclerKDynamicGridLayoutManager
import javax.inject.Inject

/**
 * @ClassName FavoritesFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class FavoritesFragment : RecyclerViewFragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase
    @Inject
    lateinit var gameInteractor: GameInteractor

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesViewModel = ViewModelProviders.of(this, FavoritesViewModel.Factory(retrogradeDb))
            .get(FavoritesViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        val gamesAdapter = GamesAdapter(R.layout.layout_game_grid, gameInteractor)
        favoritesViewModel.favorites.observe(this, Observer {
            gamesAdapter.submitList(it)
            emptyView?.applyVisibleIfElseGone(it.isEmpty())
        })

        recyclerView?.apply {
            this.adapter = gamesAdapter
            this.layoutManager = RecyclerKDynamicGridLayoutManager(context)

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
            RecyclerKDecorationSpaceGrid.setSingleGridSpaceDecoration(this, spacingInPixels)
        }
        restoreRecyclerViewState()
    }

    @dagger.Module
    class Module
}
