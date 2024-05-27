package com.mozhimen.emulatork.test.hilt.systems

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.basick.utilk.androidx.lifecycle.UtilKViewModel
import com.mozhimen.emulatork.basic.game.system.GameSystemMetaID
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.basic.android.RecyclerViewFragment
import com.mozhimen.xmlk.recyclerk.decoration.RecyclerKDecorationSpaceGrid
import com.mozhimen.xmlk.recyclerk.manager.RecyclerKDynamicGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName MetaSystemsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
@AndroidEntryPoint
class MetaSystemsFragment : RecyclerViewFragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase

    private var metaSystemsAdapter: MetaSystemsAdapter? = null

    @Inject
    lateinit var metaSystemsFactory: MetaSystemsViewModel.Factory

    private val metaSystemsViewModel: MetaSystemsViewModel by viewModels { MetaSystemsViewModel.provideFactory(metaSystemsFactory, requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        metaSystemsAdapter = MetaSystemsAdapter { navigateToGames(it) }

        runOnViewLifecycleState(Lifecycle.State.CREATED) {
            metaSystemsViewModel.availableMetaSystems.collect {
                metaSystemsAdapter?.submitList(it)
                emptyView?.isVisible = it.isEmpty()
            }
        }

        recyclerView?.apply {
            this.adapter = metaSystemsAdapter
            this.layoutManager = RecyclerKDynamicGridLayoutManager(context, 2)

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
            RecyclerKDecorationSpaceGrid.setSingleGridSpaceDecoration(this, spacingInPixels)
        }
    }

    private fun navigateToGames(system: GameSystemMetaID) {
        val dbNames = system.systemIDs
            .map { it.dbname }
            .toTypedArray()

        val action = MetaSystemsFragmentDirections.actionNavigationSystemsToNavigationGames(dbNames)
        findNavController().navigate(action)
    }

//    @dagger.Module
//    class Module
}
