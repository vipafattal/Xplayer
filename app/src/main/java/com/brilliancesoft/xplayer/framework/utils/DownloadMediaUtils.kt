package com.brilliancesoft.xplayer.framework.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity.Companion.STORAGE_PERMISSION
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity.Companion.STORAGE_REQUEST_CODE
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast

/**
 * Created by Abed on 2019/9/24
 */

object DownloadMediaUtils {
    /**
     * Returns a true if the given folder name [Media.title] and the file name [Media.subtitle] is existed.
     */
    fun isDownloaded(media: Media): Boolean {
        val suraFile = DataDirectories.buildSurahFile(media.title, media.subtitle!!)
        return suraFile.exists()
    }

    /**
     * Returns a true if the given folder name [reciterName] and the file name [suraName] is existed.
     */
    fun isDownloaded(reciterName: String, suraName: String): Boolean {
        val suraFile = DataDirectories.buildSurahFile(reciterName, suraName)
        return suraFile.exists()
    }

    fun download(media: Media, activity: BaseActivity) {
        activity.executeWithPendingPermission(STORAGE_PERMISSION, STORAGE_REQUEST_CODE) {
            val downloadManager =
                activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri =
                Uri.parse(media.link)
            val request = DownloadManager.Request(uri)
            val downloadPath = DataDirectories.buildSurahFile(media.title, media.subtitle!!)
            request.setDestinationUri(downloadPath.toUri())
            downloadManager.enqueue(request)

            XplayerToast.makeShort(activity, "Downloading started...")
        }
    }

    fun deleteDownloadedFile(media: Media) =
        DataDirectories.buildSurahFile(media).delete()

}