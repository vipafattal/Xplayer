package com.brilliancesoft.xplayer.utils

import android.content.Context
import android.net.ConnectivityManager
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication.Companion.appContext

object ConnectivityHelper {
    fun isConnectedToNetwork(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isConnected: Boolean
        val activeNetwork = connectivityManager.activeNetworkInfo
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        return isConnected
    }
}