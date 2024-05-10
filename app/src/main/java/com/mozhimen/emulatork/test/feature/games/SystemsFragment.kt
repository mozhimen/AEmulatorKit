package com.mozhimen.emulatork.test.feature.games

import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mozhimen.basick.utilk.android.view.applyVisibleIfElseGone
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.RecyclerViewFragment
import com.mozhimen.taskk.autodispose.utils.subscribeBy
import com.mozhimen.xmlk.recyclerk.decoration.RecyclerKDecorationSpaceGrid
import com.mozhimen.xmlk.recyclerk.manager.RecyclerKDynamicGridLayoutManager
import com.swordfish.lemuroid.app.feature.games.SystemsFragmentDirections
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @ClassName SystemsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SystemsFragment : RecyclerViewFragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase

    private var systemsAdapter: SystemsAdapter? = null

    private lateinit var systemsViewModel: SystemsViewModel

    override fun onResume() {
        super.onResume()

        systemsViewModel = ViewModelProviders.of(this, SystemsViewModel.Factory(retrogradeDb))
            .get(SystemsViewModel::class.java)

        systemsAdapter = SystemsAdapter { navigateToGames(it) }
        systemsViewModel.availableSystems
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(scope())
            .subscribeBy {
                systemsAdapter?.submitList(it)
                emptyView?.applyVisibleIfElseGone(it.isEmpty())
            }

        recyclerView?.apply {
            this.adapter = systemsAdapter
            this.layoutManager = RecyclerKDynamicGridLayoutManager(context, 2)

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
            RecyclerKDecorationSpaceGrid.setSingleGridSpaceDecoration(this, spacingInPixels)
        }
        restoreRecyclerViewState()
    }

    private fun navigateToGames(system: GameSystem) {
        val action = SystemsFragmentDirections.actionNavigationSystemsToNavigationGames(system.id)
        findNavController().navigate(action)
    }

    @dagger.Module
    class Module
}