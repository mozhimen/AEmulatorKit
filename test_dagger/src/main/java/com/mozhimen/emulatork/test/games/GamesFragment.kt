package com.mozhimen.emulatork.test.games

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.game.GameInteractor
import com.mozhimen.emulatork.ui.game.GamesAdapter
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.covers.CoverLoader
import com.mozhimen.emulatork.ui.dagger.game.RecyclerViewFragment
import javax.inject.Inject

/**
 * @ClassName GamesFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GamesFragment : RecyclerViewFragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase

    @Inject
    lateinit var gameInteractor: GameInteractor

    @Inject
    lateinit var coverLoader: CoverLoader

    private val args: GamesFragmentArgs by navArgs()

    private lateinit var gamesViewModel: GamesViewModel

    private var gamesAdapter: GamesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gamesAdapter = GamesAdapter(R.layout.layout_game_list, gameInteractor, coverLoader)

        val factory = GamesViewModel.Factory(retrogradeDb)
        gamesViewModel = ViewModelProvider(this, factory)[GamesViewModel::class.java]

        recyclerView?.apply {
            adapter = gamesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            gamesViewModel.games
                .collect { gamesAdapter?.submitData(lifecycle, it) }
        }

        gamesViewModel.systemIds.value = (listOf(*args.systemIds))
    }

    @dagger.Module
    class Module
}