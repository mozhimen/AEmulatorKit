package com.mozhimen.emulatork.test.dagger.home

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.test.dagger.R
import com.mozhimen.emulatork.test.dagger.games.GameContextMenuListener
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.basic.game.GameUtil

/**
 * @ClassName EpoxyGameView
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@EpoxyModelClass(layout = R.layout.layout_game_recent)
abstract class EpoxyGameView : EpoxyModelWithHolder<EpoxyGameView.Holder>() {

    @EpoxyAttribute
    lateinit var game: Game

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var gameInteractor: GameInteractor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var coverLoader: CoverLoader

    override fun bind(holder: Holder) {
        holder.titleView?.text = game.title
        holder.subtitleView?.let { it.text = GameUtil.getGameSubtitle(it.context, game) }

        coverLoader.loadCover(game, holder.coverView)

        holder.itemView?.setOnClickListener { gameInteractor.onGamePlay(game) }
        holder.itemView?.setOnCreateContextMenuListener(
            GameContextMenuListener(gameInteractor, game)
        )
    }

    override fun unbind(holder: Holder) {
        holder.itemView?.setOnClickListener(null)
        holder.coverView?.apply {
            coverLoader.cancelRequest(this)
        }
        holder.itemView?.setOnCreateContextMenuListener(null)
    }

    class Holder : EpoxyHolder() {
        var itemView: View? = null
        var titleView: TextView? = null
        var subtitleView: TextView? = null
        var coverView: ImageView? = null

        override fun bindView(itemView: View) {
            this.itemView = itemView
            this.titleView = itemView.findViewById(R.id.text)
            this.subtitleView = itemView.findViewById(R.id.subtext)
            this.coverView = itemView.findViewById(R.id.image)
        }
    }
}
