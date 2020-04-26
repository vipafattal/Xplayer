package com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView

import androidx.annotation.IdRes

/**
 * Created by Abed on
 */
interface RecyclerPressListener<T : Any> {
    fun onItemClick(data: T,@IdRes clickedViewId:Int)
    fun onItemLongClickListener(data: T?,@IdRes clickedViewId:Int){}
}