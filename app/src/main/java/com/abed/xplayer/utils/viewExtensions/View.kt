package com.abed.xplayer.utils.viewExtensions

import android.annotation.TargetApi
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.Insets

/**
 * Created by  on
 */

fun Editable?.isNotNullOrEmpty() = this != null && isNotEmpty()


@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
fun View.doOnApplyWindowInsets(insetsBlock: ( insets: WindowInsets, originalPadding: Rect) -> Unit) {
    val padding =
        Rect(
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        )

    setOnApplyWindowInsetsListener { v, insets ->
        insetsBlock(insets,padding)
        insets
    }
}

fun TextView.setDrawable(end:Drawable?= null,start:Drawable?= null,top:Drawable?=null,bottom:Drawable?=null){
    setCompoundDrawablesRelative(start, top, end, bottom)
}



