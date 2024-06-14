package com.mozhimen.emulatork.test.hilt.games

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.common.system.SystemProvider
/**
 * @ClassName GameViewHolder
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GameViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
    private var titleView: TextView? = null
    private var subtitleView: TextView? = null
    private var coverView: ImageView? = null
    private var favoriteToggle: ToggleButton? = null

    init {
        titleView = itemView.findViewById(R.id.text)
        subtitleView = itemView.findViewById(R.id.subtext)
        coverView = itemView.findViewById(R.id.image)
        favoriteToggle = itemView.findViewById(R.id.favorite_toggle)
    }

    fun bind(game: Game, gameInteractor: GameInteractor, coverLoader: CoverLoader) {
        titleView?.text = game.title
        subtitleView?.text = SystemProvider.getGameSubtitle(itemView.context, game)
        favoriteToggle?.isChecked = game.isFavorite

        coverLoader.loadCover(game, coverView)

        itemView.setOnClickListener { gameInteractor.onGamePlay(game) }
        itemView.setOnCreateContextMenuListener(GameContextMenuListener(gameInteractor, game))

        favoriteToggle?.setOnCheckedChangeListener { _, isChecked ->
            gameInteractor.onFavoriteToggle(game, isChecked)
        }
    }

    fun unbind(coverLoader: CoverLoader) {
        coverView?.apply {
            coverLoader.cancelRequest(this)
            this.setImageDrawable(null)
        }
        itemView.setOnClickListener(null)
        favoriteToggle?.setOnCheckedChangeListener(null)
        itemView.setOnCreateContextMenuListener(null)
    }
}