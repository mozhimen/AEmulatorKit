package com.mozhimen.emulatork.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.mozhimen.emulatork.basic.library.db.entities.Game

/**
 * @ClassName GamesAdapter
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GamesAdapter(
    private val baseLayout: Int,
    private val gameInteractor: GameInteractor,
    private val coverLoader: com.mozhimen.emulatork.ui.covers.CoverLoader
) : PagingDataAdapter<Game, GameViewHolder>(Game.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(LayoutInflater.from(parent.context).inflate(baseLayout, parent, false))
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, gameInteractor, coverLoader) }
    }

    override fun onViewRecycled(holder: GameViewHolder) {
        holder.unbind(coverLoader)
    }
}