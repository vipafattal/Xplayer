package com.brilliancesoft.xplayer.framework.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CheckDownloadComplete : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {

            val downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            val cachedDownloadTitle = CacheHelper.getString(downloadID.toString())

            CacheHelper.remove(cachedDownloadTitle)
            CacheHelper.remove(downloadID.toString())
        }
    }
}