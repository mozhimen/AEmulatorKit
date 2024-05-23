package com.mozhimen.emulatork.test.hilt.systems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.emulatork.basic.game.system.GameSystemMetaID
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.basic.game.system.GameSystemMetaInfo

/**
 * @ClassName MetaSystemsAdapter
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class MetaSystemViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
    private var coverView: ImageView? = null
    private var textView: TextView? = null
    private var subtextView: TextView? = null

    init {
        coverView = itemView.findViewById(R.id.image)
        textView = itemView.findViewById(R.id.text)
        subtextView = itemView.findViewById(R.id.subtext)
    }

    fun bind(gameSystemMetaInfo: GameSystemMetaInfo, onSystemClick: (GameSystemMetaID) -> Unit) {
        textView?.text = itemView.context.resources.getString(gameSystemMetaInfo.metaSystem.titleResId)
        subtextView?.text = itemView.context.getString(
            R.string.system_grid_details,
            gameSystemMetaInfo.count.toString()
        )
        coverView?.setImageResource(gameSystemMetaInfo.metaSystem.imageResId)
        itemView.setOnClickListener { onSystemClick(gameSystemMetaInfo.metaSystem) }
    }
}

class MetaSystemsAdapter(
    private val onSystemClick: (GameSystemMetaID) -> Unit
) : ListAdapter<GameSystemMetaInfo, com.mozhimen.emulatork.test.dagger.systems.MetaSystemViewHolder>(com.mozhimen.emulatork.test.dagger.systems.MetaSystemsAdapter.Companion.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.mozhimen.emulatork.test.dagger.systems.MetaSystemViewHolder {
        return com.mozhimen.emulatork.test.dagger.systems.MetaSystemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_system, parent, false)
        )
    }

    override fun onBindViewHolder(holder: com.mozhimen.emulatork.test.dagger.systems.MetaSystemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onSystemClick) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GameSystemMetaInfo>() {

            override fun areItemsTheSame(oldInfo: GameSystemMetaInfo, newInfo: GameSystemMetaInfo) =
                oldInfo.metaSystem == newInfo.metaSystem

            override fun areContentsTheSame(oldInfo: GameSystemMetaInfo, newInfo: GameSystemMetaInfo) =
                oldInfo == newInfo
        }
    }
}
