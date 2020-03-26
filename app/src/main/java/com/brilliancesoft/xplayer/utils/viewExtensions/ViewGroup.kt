
package com.brilliancesoft.xplayer.utils.viewExtensions

import android.view.View
import android.view.ViewGroup
import com.abed.magentaX.android.views.listeners.onClick

/**
 * Created by Abed on
 */

@Suppress("UNCHECKED_CAST")
val ViewGroup.children: Array<View>
    get() {
        val children = arrayOfNulls<View>(childCount)
        for (index in 0 until childCount)
            children[index] = getChildAt(index)

        return children as Array<View>
    }

fun ViewGroup.setOnChildClicks(block: (view: View) -> Unit) {
    for (view in children)
        view.onClick(block)
}