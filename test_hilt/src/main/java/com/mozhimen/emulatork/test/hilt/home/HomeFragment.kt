package com.mozhimen.emulatork.test.hilt.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.Carousel
import com.mozhimen.basick.utilk.android.app.UtilKActivityStart
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.library.SettingsInteractor
import com.mozhimen.emulatork.test.hilt.R
import com.mozhimen.emulatork.ui.hilt.settings.StorageFrameworkPickerActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * @ClassName HomeFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var gameInteractor: GameInteractor

    @Inject
    lateinit var coverLoader: CoverLoader

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.provideFactory(homeViewModelFactory, requireContext())
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        handleNotificationPermissionResponse(it)
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

        homeViewModel.refreshData()

        // Disable snapping in carousel view
        Carousel.setDefaultGlobalSnapHelperFactory(null)

        val pagingController = EpoxyHomeController(gameInteractor, coverLoader)

        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recyclerview)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = pagingController.adapter

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            pagingController.getActions().collect {
                com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Received home view model action + $it")
                handleEpoxyAction(it)
            }
        }

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            homeViewModel.getViewStates().collect {
                com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"getViewStates + $it")
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
            UtilKActivityStart.startSettingApplicationDetailsSettings(requireContext())
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

//    @dagger.Module
//    class Module
}
