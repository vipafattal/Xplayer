package com.abed.xplayer.ui.sharedComponent

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.codebox.lib.android.viewGroup.inflater

/**
 * Created by ${User} on ${Date}
 */
 open class RecyclerViewHolder<in T>(parent: ViewGroup, @LayoutRes layoutId: Int) : RecyclerView.ViewHolder(parent.inflater(layoutId)) {
    open fun bindData(data: T){}
    open fun bindHeader(data: T) {}
    open fun bindWithHeader(data: T) {}
}