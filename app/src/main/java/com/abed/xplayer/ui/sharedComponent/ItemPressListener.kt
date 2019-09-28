package com.abed.xplayer.ui.sharedComponent

/**
 * Created by Abed on
 */
interface ItemPressListener<T : Any> {
    fun onItemClick(data: T)
    fun onItemLongClicklistener(data: T?){}
}