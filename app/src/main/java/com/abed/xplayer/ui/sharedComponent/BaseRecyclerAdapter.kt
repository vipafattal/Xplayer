package com.abed.xplayer.ui.sharedComponent

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by  on
 */
abstract class BaseRecyclerAdapter<Data : Any>(
    protected var dataList: List<Data>
) :
    RecyclerView.Adapter<RecyclerViewHolder<Data>>() {

    abstract val layoutItemId: Int

    override fun getItemCount(): Int = dataList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Data> =
        RecyclerViewHolder(parent, layoutItemId)

    fun updateDataList(newDataList: List<Data>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    fun removeItemAtIndex(index: Int) {
        notifyItemRemoved(index)
        notifyItemRangeRemoved(index, dataList.size)
    }


}