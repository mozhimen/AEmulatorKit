package com.mozhimen.emulatork.test.feature.games

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.mozhimen.emulatork.test.shared.GamesAdapter
import com.mozhimen.emulatork.test.shared.RecyclerViewFragment
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

    private val args: GamesFragmentArgs by navArgs()

    private lateinit var gamesViewModel: GamesViewModel

    private var gamesAdapter: GamesAdapter? = null

    override fun onResume() {
        super.onResume()

        gamesAdapter = GamesAdapter(R.layout.layout_game_list, gameInteractor)

        gamesViewModel = ViewModelProviders.of(this, GamesViewModel.Factory(retrogradeDb))
            .get(GamesViewModel::class.java)

        gamesViewModel.games.observe(this, Observer { pagedList ->
            gamesAdapter?.submitList(pagedList)
        })

        args.systemId?.let {
            gamesViewModel.systemId.value = it
        }

        recyclerView?.apply {
            adapter = gamesAdapter
            layoutManager = LinearLayoutManager(context)
        }
        restoreRecyclerViewState()
    }

    @dagger.Module
    class Module
}
