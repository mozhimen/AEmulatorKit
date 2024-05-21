package com.mozhimen.emulatork.test.hilt.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.game.GameInteractor
import com.mozhimen.emulatork.ui.game.GamesAdapter
import com.mozhimen.emulatork.ui.covers.CoverLoader
import com.mozhimen.xmlk.recyclerk.decoration.RecyclerKDecorationSpaceGrid
import com.mozhimen.xmlk.recyclerk.manager.RecyclerKDynamicGridLayoutManager
import com.mozhimen.emulatork.ui.game.RecyclerViewFragment
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

    @Inject
    lateinit var coverLoader: CoverLoader

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = FavoritesViewModel.Factory(retrogradeDb)
        favoritesViewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]

        val gamesAdapter = GamesAdapter(com.mozhimen.emulatork.test.R.layout.layout_game_grid, gameInteractor, coverLoader)

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            favoritesViewModel.favorites
                .collect {
                    gamesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
        }

        gamesAdapter.addLoadStateListener { loadState ->
            updateEmptyViewVisibility(loadState, gamesAdapter.itemCount)
        }

        recyclerView?.apply {
            this.adapter = gamesAdapter
            this.layoutManager = RecyclerKDynamicGridLayoutManager(context)

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
            RecyclerKDecorationSpaceGrid.setSingleGridSpaceDecoration(this, spacingInPixels)
        }
    }

    @dagger.Module
    class Module
}