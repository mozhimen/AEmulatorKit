package com.mozhimen.emulatork.test.dagger.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.Carousel
import com.mozhimen.basick.utilk.android.app.UtilKActivityStart
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.dagger.R
import com.mozhimen.emulatork.ext.library.SettingsInteractor
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.ui.dagger.settings.StorageFrameworkPickerActivity
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

/**
 * @ClassName HomeFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class HomeFragment : Fragment() {

    @Inject
    lateinit var retrogradeDb: RetrogradeDatabase

    @Inject
    lateinit var gameInteractor: GameInteractor

    @Inject
    lateinit var coverLoader: CoverLoader

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    private lateinit var homeViewModel: HomeViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        handleNotificationPermissionResponse(it)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = HomeViewModel.Factory(requireContext().applicationContext, retrogradeDb)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Disable snapping in carousel view
        Carousel.setDefaultGlobalSnapHelperFactory(null)

        val pagingController = EpoxyHomeController(gameInteractor, coverLoader)

        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recyclerview)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = pagingController.adapter

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            pagingController.getActions().collect {
                Timber.d("Received home view model action + $it")
                handleEpoxyAction(it)
            }
        }

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            homeViewModel.getViewStates().collect {
                pagingController.updateState(it)
            }
        }
    }

    private fun handleEpoxyAction(homeAction: EpoxyHomeController.HomeAction) {
        when (homeAction) {
            EpoxyHomeController.HomeAction.CHANGE_STORAGE_FOLDER -> handleChangeStorageFolder()
            EpoxyHomeController.HomeAction.ENABLE_NOTIFICATION_PERMISSION -> handleNotificationPermissionRequest()
        }
    }

    private fun handleChangeStorageFolder() {
        settingsInteractor.changeLocalStorageFolder(StorageFrameworkPickerActivity::class.java)
    }

    private fun handleNotificationPermissionRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun handleNotificationPermissionResponse(isGranted: Boolean) {
        if (!isGranted) {
            UtilKActivityStart.startApplicationDetailsSettings(requireContext())
        }
    }

    private fun isNotificationsPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        val permissionResult = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        )

        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.updateNotificationPermission(isNotificationsPermissionGranted())
    }

    @dagger.Module
    class Module
}
