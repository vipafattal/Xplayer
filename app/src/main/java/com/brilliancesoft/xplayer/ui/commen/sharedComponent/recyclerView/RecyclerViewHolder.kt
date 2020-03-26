package com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.abed.magentaX.android.viewGroup.inflater

/**
 * Created by ${User} on ${Date}
 */
 open class RecyclerViewHolder<in T>(parent: ViewGroup, @LayoutRes layoutId: Int) : RecyclerView.ViewHolder(parent.inflater(layoutId)) {
    open fun bindData(data: T){}
    open fun bindHeader(data: T) {}
    open fun bindWithHeader(data: T) {}
}