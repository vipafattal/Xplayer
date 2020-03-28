package com.brilliancesoft.xplayer.framework.utils

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.abed.magentaX.android.resoures.stringify
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity.Companion.STORAGE_PERMISSION
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity.Companion.STORAGE_REQUEST_CODE

/**
 * Created by Abed on 2019/9/24
 */

object DownloadMediaUtils {


    /**
     * Returns a true if the given folder name [Media] and the file name [Media.subtitle] is existed and is not downloading.
     */
    fun isDownloaded(media: Media): Boolean {
        val suraFile = DataDirectories.buildSurahFile(media)
        return suraFile.exists() && !isDownloading(media)
    }

    /**
     * Download [media] by [Media.link] and save it to cache so to check if download not completed yet.
     * It gets removed from cache when download completed
     * @see CheckDownloadComplete
     */

    fun download(media: Media, activity: BaseActivity):Long {
        var downloadId: Long = -1L

        if (isDownloading(media)) {
            XplayerToast.makeShort(activity, stringify(R.string.exo_download_downloading, activity))
            downloadId = 0L
        }
        else {
            activity.executeWithPendingPermission(STORAGE_PERMISSION, STORAGE_REQUEST_CODE) {
                val downloadManager =
                    XplayerApplication.appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val uri = Uri.parse(media.link)

                val request = DownloadManager.Request(uri)
                val downloadPath = DataDirectories.buildSurahFile(media)

                request.setDestinationUri(downloadPath.toUri())

                XplayerToast.makeShort(activity, stringify(R.string.download_started, activity))
                downloadId = downloadManager.enqueue(request)

                CacheHelper.saveString(media.subtitle, downloadId.toString())
                CacheHelper.saveString(downloadId.toString(), media.subtitle)
            }
        }

        if (downloadId == -1L)
            XplayerToast.makeShort(activity, stringify(R.string.exo_download_failed, activity))

        return downloadId
    }

    private fun isDownloading(media: Media): Boolean {
        val downloadId = CacheHelper.getString(media.subtitle)

        return if (downloadId.isEmpty()) false
        else getDownloadStatus(downloadId.toLong()) == DownloadManager.STATUS_RUNNING
    }

    private fun getDownloadStatus( downloadId: Long): Int {

        val downloadManager = XplayerApplication.appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(downloadId)

        val c: Cursor = downloadManager.query(query)
        if (c.moveToFirst()) {
            val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            c.close()
            return status
        }
        return -1
    }

    fun deleteDownloadedFile(media: Media) =
        DataDirectories.buildSurahFile(media).delete()

}