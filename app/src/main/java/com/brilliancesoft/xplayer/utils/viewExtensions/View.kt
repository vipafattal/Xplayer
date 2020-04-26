package com.brilliancesoft.xplayer.utils.viewExtensions

import android.animation.ValueAnimator
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
import com.brilliancesoft.xplayer.R
import com.google.android.material.card.MaterialCardView

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

fun MaterialCardView.animateElevation(animateOut: Boolean) {
    val elevation = resources.getDimension(R.dimen.toolbar_elevation)
    var endElevation = elevation
    var startElevation = 0f

    if (animateOut) {
        startElevation = elevation
        endElevation = 0f
    }
    if (cardElevation != endElevation)
        ValueAnimator().apply {
            duration = 250
            setFloatValues(startElevation, endElevation)
            setTarget(this)
            start()
        }.addUpdateListener {
            cardElevation = it.animatedValue as Float
        }
}

 fun MaterialCardView.hideCard() {
        animate()
        .setDuration(250)
        .translationY(-height.toFloat() + cardElevation)
        .start()
}

 fun MaterialCardView.showCard() {
         animate()
        .setDuration(400)
        .translationY(0f)
        .start()
}

fun TextView.setDrawable(
    end: Drawable? = null,
    start: Drawable? = null,
    top: Drawable? = null,
    bottom: Drawable? = null
) {
    setCompoundDrawablesRelative(start, top, end, bottom)
}