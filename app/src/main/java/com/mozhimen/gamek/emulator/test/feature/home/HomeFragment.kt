package com.mozhimen.gamek.emulator.test.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.Carousel
import com.mozhimen.gamek.emulator.test.R

/**
 * @ClassName HomeFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()

        val homeViewModel =
            ViewModelProviders.of(this,
                HomeViewModel.Factory(context!!.applicationContext, retrogradeDb)).get(HomeViewModel::class.java)

        // Disable snapping in carousel view
        Carousel.setDefaultGlobalSnapHelperFactory(null)

        val pagingController = EpoxyHomeController(gameInteractor, settingsInteractor)

        val recyclerView = view!!.findViewById<RecyclerView>(R.id.home_recyclerview)
        val layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = pagingController.adapter

        homeViewModel.recentGames.observe(this, Observer {
            pagingController.updateRecents(it)
        })

        homeViewModel.favoriteGames.observe(this, Observer {
            pagingController.updateFavorites(it)
        })

        homeViewModel.discoverGames.observe(this, Observer {
            pagingController.updateDiscover(it)
        })

        homeViewModel.indexingInProgress.observe(this, Observer {
            pagingController.updateLibraryIndexingInProgress(it)
        })
    }
}