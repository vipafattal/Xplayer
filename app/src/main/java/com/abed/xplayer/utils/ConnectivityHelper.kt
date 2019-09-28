package com.abed.xplayer.utils

import android.content.Context
import android.net.ConnectivityManager
import com.abed.xplayer.ui.sharedComponent.XplayerApplication.Companion.xplayer

object ConnectivityHelper {
    fun isConnectedToNetwork(): Boolean {
        val connectivityManager = xplayer.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isConnected: Boolean
        val activeNetwork = connectivityManager.activeNetworkInfo
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        return isConnected
    }
}