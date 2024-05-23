package com.mozhimen.emulatork.test.hilt.home

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.carousel
import com.mozhimen.basick.BuildConfig
import com.mozhimen.basick.utilk.kotlin.sequences.UtilKSequence
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.rxk.epoxy4.util.withModelsFrom
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * @ClassName EpoxyHomeController
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class EpoxyHomeController(
    private val gameInteractor: GameInteractor,
    private val coverLoader: CoverLoader
) : AsyncEpoxyController() {

    enum class HomeAction {
        CHANGE_STORAGE_FOLDER,
        ENABLE_NOTIFICATION_PERMISSION
    }

    private var currentUIState = HomeViewModel.UIState()
    private val actionsFlow = MutableSharedFlow<HomeAction>()

    fun getActions(): Flow<HomeAction> {
        return actionsFlow
    }

    fun updateState(viewState: HomeViewModel.UIState) {
        currentUIState = viewState
        requestModelBuild()
    }

    override fun buildModels() {
        if (displayEmptyView()) {
            addEmptyView()
        }

        if (displayEnableNotifications()) {
            addNotificationsView()
        }

        if (displayFavorites()) {
            addCarousel("favorites", R.string.favorites, currentUIState.favoritesGames)
        }

        if (displayRecents()) {
            addCarousel("recent", R.string.recent, currentUIState.recentGames)
        }

        if (displayDiscovery()) {
            addCarousel("discover", R.string.discover, currentUIState.discoveryGames)
        }
    }

    private fun displayDiscovery() = currentUIState.discoveryGames.isNotEmpty()

    private fun displayRecents() = currentUIState.recentGames.isNotEmpty()

    private fun displayFavorites() = currentUIState.favoritesGames.isNotEmpty()

    private fun displayEmptyView(): Boolean {
        val conditions = UtilKSequence.lazySequenceOf(
            { currentUIState.loading.not() },
            { currentUIState.recentGames.isEmpty() },
            { currentUIState.favoritesGames.isEmpty() },
            { currentUIState.discoveryGames.isEmpty() },
        )
        return conditions.all { it }
    }

    private fun displayEnableNotifications() = currentUIState.notificationsEnabled.not()

    private fun addCarousel(id: String, titleId: Int, games: List<Game>) {
        epoxyHomeSection {
            id("section_$id")
            title(titleId)
        }
        carousel {
            id("carousel_$id")
            paddingRes(R.dimen.grid_spacing)
            withModelsFrom(games) { item ->
                EpoxyGameView_()
                    .id(item.id)
                    .game(item)
                    .gameInteractor(this@EpoxyHomeController.gameInteractor)
                    .coverLoader(this@EpoxyHomeController.coverLoader)
            }
        }
    }

    private fun addNotificationsView() {
        epoxyHomeNotification {
            id("notifications")
                .title(R.string.home_notification_title)
                .message(R.string.home_notification_message)
                .action(R.string.home_notification_action)
                .actionEnabled(true)
                .onClick { this@EpoxyHomeController.launchAction(HomeAction.ENABLE_NOTIFICATION_PERMISSION) }
        }
    }

    private fun addEmptyView() {
        epoxyHomeNotification {
            id("notification_empty")
                .title(R.string.home_empty_title)
                .message(R.string.home_empty_message)
                .action(R.string.home_empty_action)
                .actionEnabled(!this@EpoxyHomeController.currentUIState.indexInProgress)
                .onClick { this@EpoxyHomeController.launchAction(HomeAction.CHANGE_STORAGE_FOLDER) }
        }
    }

    private fun launchAction(homeAction: HomeAction) {
        GlobalScope.launch {
            actionsFlow.emit(homeAction)
        }
    }

    init {
        if (BuildConfig.DEBUG) {
            isDebugLoggingEnabled = true
        }
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        throw exception
    }
}
