package com.mozhimen.emulatork.test.dagger.systems

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.system.ESystemMetaType
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.common.dagger.android.DaggerRecyclerViewFragment
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.xmlk.recyclerk.decoration.RecyclerKDecorationSpaceGrid
import com.mozhimen.xmlk.recyclerk.manager.RecyclerKDynamicGridLayoutManager
import javax.inject.Inject

/**
 * @ClassName MetaSystemsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class MetaSystemsFragment : DaggerRecyclerViewFragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase

    private var metaSystemsAdapter: MetaSystemsAdapter? = null

    private lateinit var metaSystemsViewModel: MetaSystemsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = MetaSystemsViewModel.Factory(retrogradeDb, requireContext().applicationContext)
        metaSystemsViewModel = ViewModelProvider(this, factory)[MetaSystemsViewModel::class.java]

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

    private fun navigateToGames(eSystemMetaType: ESystemMetaType) {
        val dbNames = eSystemMetaType.eSystemTypes
            .map { it.simpleName }
            .toTypedArray()

        val action = MetaSystemsFragmentDirections.actionNavigationSystemsToNavigationGames(dbNames)
        findNavController().navigate(action)
    }

    @dagger.Module
    class Module
}
