package com.mozhimen.emulatork.test.shared

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.emulatork.test.R
import com.mozhimen.basick.utilk.kotlin.sequences.UtilKSequence
import dagger.android.support.AndroidSupportInjection

/**
 * @ClassName RecyclerViewFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
open class RecyclerViewFragment : Fragment() {

    protected var recyclerView: RecyclerView? = null
    protected var emptyView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_recyclerview, container, false)
        recyclerView = root.findViewById(R.id.recycler_view)
        emptyView = root.findViewById(R.id.empty_view)
        return root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    fun updateEmptyViewVisibility(loadState: CombinedLoadStates, itemCount: Int) {
        val emptyViewConditions = UtilKSequence.lazySequenceOf<Boolean>(
            { loadState.source.refresh is LoadState.NotLoading },
            { loadState.append.endOfPaginationReached },
            { itemCount == 0 }
        )

        val emptyViewVisible = emptyViewConditions.all { it }

        recyclerView?.isVisible = !emptyViewVisible
        emptyView?.isVisible = emptyViewVisible
    }
}

