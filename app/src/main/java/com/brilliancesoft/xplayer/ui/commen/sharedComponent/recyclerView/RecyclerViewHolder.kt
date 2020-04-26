package com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.abed.magentaX.android.viewGroup.inflater
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by ${User} on ${Date}
 */
 open class RecyclerViewHolder<in T>(parent: ViewGroup, @LayoutRes layoutId: Int) : RecyclerView.ViewHolder(parent.inflater(layoutId)) ,LayoutContainer{

    override val containerView: View = itemView
    protected val viewContext = itemView.context

    open fun onBind(data: T){}
    open fun bindHeader(data: T) {}
    open fun bindWithHeader(data: T) {}
}