package com.mozhimen.emulatork.test.feature.home

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.GameContextMenuListener
import com.squareup.picasso.Picasso

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
    var title: String? = null

    @EpoxyAttribute
    var coverUrl: String? = null

    @EpoxyAttribute
    var favorite: Boolean? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClick: (() -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onRestart: (() -> Unit)? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onFavoriteChanged: ((Boolean) -> Unit)? = null

    override fun bind(holder: Holder) {
        holder.titleView?.text = title
        Picasso.get()
            .load(coverUrl)
            .placeholder(R.drawable.ic_image_paceholder)
            .into(holder.coverView)
        holder.itemView?.setOnClickListener { onClick?.invoke() }
        holder.itemView?.setOnCreateContextMenuListener(
            GameContextMenuListener(
            favorite,
            onClick,
            onRestart,
            onFavoriteChanged)
        )
    }

    override fun unbind(holder: Holder) {
        holder.itemView?.setOnClickListener(null)
        holder.coverView?.apply {
            Picasso.get().cancelRequest(this)
        }
        holder.itemView?.setOnCreateContextMenuListener(null)
    }

    class Holder : EpoxyHolder() {
        var itemView: View? = null
        var titleView: TextView? = null
        var coverView: ImageView? = null

        override fun bindView(itemView: View) {
            this.itemView = itemView
            this.titleView = itemView.findViewById(R.id.text)
            this.coverView = itemView.findViewById(R.id.image)
        }
    }
}