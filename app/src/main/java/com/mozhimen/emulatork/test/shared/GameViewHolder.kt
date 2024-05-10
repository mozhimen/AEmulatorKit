package com.mozhimen.emulatork.test.shared

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.R
import com.squareup.picasso.Picasso

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

    @SuppressLint("SetTextI18n")
    fun bind(game: Game, gameInteractor: GameInteractor) {
        val systemName = getSystemNameForGame(game)
        val developerName = if (game.developer?.isNotBlank() == true) {
            "- ${game.developer}"
        } else {
            ""
        }

        titleView?.text = game.title
        subtitleView?.text = "$systemName $developerName"
        favoriteToggle?.isChecked = game.isFavorite

        Picasso.get()
            .load(game.coverFrontUrl)
            .placeholder(R.drawable.ic_image_paceholder)
            .error(R.drawable.ic_image_paceholder)
            .into(coverView)

        itemView.setOnClickListener { gameInteractor.onGamePlay(game) }

        itemView.setOnCreateContextMenuListener(
            GameContextMenuListener(
                game.isFavorite,
                { gameInteractor.onGamePlay(game) },
                { gameInteractor.onGameRestart(game) },
                { gameInteractor.onFavoriteToggle(game, !game.isFavorite) }
            )
        )

        favoriteToggle?.setOnCheckedChangeListener { _, isChecked -> gameInteractor.onFavoriteToggle(game, isChecked) }
    }

    private fun getSystemNameForGame(game: Game): String {
        return GameSystem.findById(game.systemId)?.shortTitleResId?.let {
            itemView.context.getString(it)
        } ?: ""
    }

    fun unbind() {
        coverView?.apply {
            Picasso.get().cancelRequest(this)
            this.setImageDrawable(null)
        }
        itemView.setOnClickListener(null)
        favoriteToggle?.setOnCheckedChangeListener(null)
        itemView.setOnCreateContextMenuListener(null)
    }
}