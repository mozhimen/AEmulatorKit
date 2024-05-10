package com.mozhimen.emulatork.test.feature.home

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.carousel
import com.mozhimen.basick.BuildConfig
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.feature.settings.SettingsInteractor
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.mozhimen.rxk.epoxy.utils.withModelsFrom

/**
 * @ClassName EpoxyHomeController
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class EpoxyHomeController(
    private val gameInteractor: GameInteractor,
    private val settingsInteractor: SettingsInteractor
) : AsyncEpoxyController() {

    private var recentGames = listOf<Game>()
    private var favoriteGames = listOf<Game>()
    private var discoverGames = listOf<Game>()

    private var libraryIndexingInProgress = false

    fun updateRecents(games: List<Game>) {
        recentGames = games
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    fun updateFavorites(games: List<Game>) {
        favoriteGames = games
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    fun updateDiscover(games: List<Game>) {
        discoverGames = games
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    fun updateLibraryIndexingInProgress(indexingInProgress: Boolean) {
        libraryIndexingInProgress = indexingInProgress
        requestDelayedModelBuild(UPDATE_DELAY_TIME)
    }

    override fun buildModels() {
        if (recentGames.isNotEmpty()) {
            addCarousel("recent", R.string.recent, recentGames)
        }

        if (favoriteGames.isNotEmpty()) {
            addCarousel("favorites", R.string.favorites, favoriteGames)
        }

        if (discoverGames.isNotEmpty()) {
            addCarousel("discover", R.string.discover, discoverGames)
        }

        if (recentGames.isEmpty() && favoriteGames.isEmpty() && discoverGames.isEmpty()) {
            addEmptyView()
        }
    }

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
                    .title(item.title)
                    .coverUrl(item.coverFrontUrl)
                    .favorite(item.isFavorite)
                    .onFavoriteChanged { gameInteractor.onFavoriteToggle(item, it) }
                    .onClick { gameInteractor.onGamePlay(item) }
                    .onRestart { gameInteractor.onGameRestart(item) }
            }
        }
    }

    private fun addEmptyView() {
        epoxyEmptyViewAction {
            id("empty_home")
                .title(R.string.home_empty_title)
                .message(R.string.home_empty_message)
                .action(R.string.home_empty_action)
                .actionEnabled(!libraryIndexingInProgress)
                .onClick { settingsInteractor.changeLocalStorageFolder() }
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

    companion object {
        const val UPDATE_DELAY_TIME = 160
    }
}
