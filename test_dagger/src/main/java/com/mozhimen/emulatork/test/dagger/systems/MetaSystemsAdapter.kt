package com.mozhimen.emulatork.test.dagger.systems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.emulatork.basic.system.ESystemMetaType
import com.mozhimen.emulatork.basic.system.SystemMetadata
import com.mozhimen.emulatork.ui.R

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

    fun bind(systemMetadata: SystemMetadata, onSystemClick: (ESystemMetaType) -> Unit) {
        textView?.text = itemView.context.resources.getString(systemMetadata.eSystemMetaType.titleResId)
        subtextView?.text = itemView.context.getString(
            R.string.system_grid_details,
            systemMetadata.count.toString()
        )
        coverView?.setImageResource(systemMetadata.eSystemMetaType.imageResId)
        itemView.setOnClickListener { onSystemClick(systemMetadata.eSystemMetaType) }
    }
}

class MetaSystemsAdapter(
    private val onSystemClick: (ESystemMetaType) -> Unit
) : ListAdapter<SystemMetadata, MetaSystemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetaSystemViewHolder {
        return MetaSystemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_system, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MetaSystemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onSystemClick) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SystemMetadata>() {

            override fun areItemsTheSame(oldInfo: SystemMetadata, newInfo: SystemMetadata) =
                oldInfo.eSystemMetaType == newInfo.eSystemMetaType

            override fun areContentsTheSame(oldInfo: SystemMetadata, newInfo: SystemMetadata) =
                oldInfo == newInfo
        }
    }
}
