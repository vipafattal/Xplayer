package com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView

/**
 * Created by Abed on
 */
interface ItemPressListener<T : Any> {
    fun onItemClick(data: T)
    fun onItemLongClicklistener(data: T?){}
}