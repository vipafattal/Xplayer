package com.brilliancesoft.xplayer.utils.viewExtensions

import com.abed.magentaX.android.resoures.colorOf
import com.brilliancesoft.xplayer.R
import com.google.android.material.button.MaterialButton

/**
 * Created by  on
 */

fun MaterialButton.enabled() {
    isEnabled = true
    setBackgroundColor(colorOf(R.color.colorAccentLight))
    setTextColor(colorOf(R.color.white))
}

fun MaterialButton.disabled(){
    isEnabled = false
    setBackgroundColor(colorOf(R.color.colorTextSecondary))
    setTextColor(colorOf(R.color.colorPrimaryDark))
}