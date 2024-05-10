package com.mozhimen.emulatork.test.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mozhimen.emulatork.basic.library.db.mos.Game

/**
 * @ClassName GamesAdapter
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GamesAdapter(
    private val baseLayout: Int,
    private val gameInteractor: GameInteractor
) : PagedListAdapter<Game, GameViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(LayoutInflater.from(parent.context).inflate(baseLayout, parent, false))
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, gameInteractor) }
    }

    override fun onViewRecycled(holder: GameViewHolder) {
        holder.unbind()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldConcert: Game, newConcert: Game) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: Game, newConcert: Game) = oldConcert == newConcert
        }
    }
}