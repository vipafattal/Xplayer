package com.abed.xplayer.utils.viewExtensions

import android.animation.Animator
import android.animation.AnimatorInflater
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import com.abed.xplayer.R

/**
 * Created by  on
 */

fun RecyclerView.animateRecyclerView(onFinished: (Animator) -> Unit ) {
    AnimatorInflater.loadAnimator(context, R.animator.list_animator).apply {
        setTarget(this)
        doOnEnd(onFinished)
        start()
    }
}