package com.brilliancesoft.xplayer.utils.viewExtensions

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.*
import com.abed.magentaX.android.utils.screenHelpers.dp

/**
 * Created by  on
 */

fun Editable?.isNotNullOrEmpty() = this != null && isNotEmpty()


@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
fun View.doOnApplyWindowInsets(insetsBlock: (insets: WindowInsets, originalPadding: Rect) -> Unit) {
    val padding =
        Rect(
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        )

    setOnApplyWindowInsetsListener { v, insets ->
        insetsBlock(insets, padding)
        insets
    }
}

fun View.addTopInsetPadding() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        val padding =
            Rect(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
        setOnApplyWindowInsetsListener { v, insets ->
            if (insets.systemWindowInsetTop > 0)
                updatePadding(top = padding.top + insets.systemWindowInsetTop)
            insets
        }
    } else
        updatePadding(top = paddingTop + dp(24))
}


fun ViewGroup.addTopInsetMargin() {
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        val margin =
            Rect(
                marginLeft,
                marginTop,
                marginRight,
                marginBottom
            )
        setOnApplyWindowInsetsListener { v, insets ->
            if (insets.systemWindowInsetTop > 0)
                marginLayoutParams.updateMargins(top = margin.top + insets.systemWindowInsetTop)
            insets
        }

    } else
        marginLayoutParams.updateMargins(top = marginTop + dp(24))
}

fun TextView.setDrawable(
    end: Drawable? = null,
    start: Drawable? = null,
    top: Drawable? = null,
    bottom: Drawable? = null
) {
    setCompoundDrawablesRelative(start, top, end, bottom)
}