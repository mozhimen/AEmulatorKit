package com.mozhimen.emulatork.test.systems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.emulatork.basic.library.MetaSystemID
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.ui.systems.MetaSystemInfo

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

    fun bind(metaSystemInfo: com.mozhimen.emulatork.ui.systems.MetaSystemInfo, onSystemClick: (MetaSystemID) -> Unit) {
        textView?.text = itemView.context.resources.getString(metaSystemInfo.metaSystem.titleResId)
        subtextView?.text = itemView.context.getString(
            R.string.system_grid_details,
            metaSystemInfo.count.toString()
        )
        coverView?.setImageResource(metaSystemInfo.metaSystem.imageResId)
        itemView.setOnClickListener { onSystemClick(metaSystemInfo.metaSystem) }
    }
}

class MetaSystemsAdapter(
    private val onSystemClick: (MetaSystemID) -> Unit
) : ListAdapter<com.mozhimen.emulatork.ui.systems.MetaSystemInfo, MetaSystemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetaSystemViewHolder {
        return MetaSystemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_system, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MetaSystemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onSystemClick) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<com.mozhimen.emulatork.ui.systems.MetaSystemInfo>() {

            override fun areItemsTheSame(oldInfo: com.mozhimen.emulatork.ui.systems.MetaSystemInfo, newInfo: com.mozhimen.emulatork.ui.systems.MetaSystemInfo) =
                oldInfo.metaSystem == newInfo.metaSystem

            override fun areContentsTheSame(oldInfo: com.mozhimen.emulatork.ui.systems.MetaSystemInfo, newInfo: com.mozhimen.emulatork.ui.systems.MetaSystemInfo) =
                oldInfo == newInfo
        }
    }
}
