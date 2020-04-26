package com.brilliancesoft.xplayer.ui.player

import android.app.Notification
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.data.LocalRepository
import com.brilliancesoft.xplayer.model.DownloadMedia
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject


class MediaDownloadService : DownloadService(
    DOWNLOAD_NOTIFICATION_ID, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.exo_download_notification_channel_name, 0
) {

    private val localRepository by inject<LocalRepository>()
    private val downloadsList: MutableList<Download> = mutableListOf()
    private val downloadNotificationHelper: DownloadNotificationHelper by lazy { DownloadNotificationHelper(this, DOWNLOAD_NOTIFICATION_CHANNEL_ID) }
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun getDownloadManager(): DownloadManager {
        return XplayerApplication.downloadManager.apply {
            requirements = DownloadManager.DEFAULT_REQUIREMENTS
            maxParallelDownloads = 3
            addListener(TerminalStateNotificationHelper(this@MediaDownloadService,DOWNLOAD_NOTIFICATION_ID + 1))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        var info = ""
        for (download in downloads) info += getMediaFromBytes(download.request.data).info + "\n"
        downloadsList.clear()
        downloadsList.addAll(downloads)
        downloadsLiveData.postValue(downloadsList.map { DownloadMedia(it) })

        return downloadNotificationHelper.buildProgressNotification(
            R.drawable.ic_download,
            null,
            info,
            downloads
        )
    }

    override fun getScheduler(): Scheduler? =
        if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null

    private inner class TerminalStateNotificationHelper(
        private val context: Context,
        firstNotificationId: Int
    ) : DownloadManager.Listener {
        private var nextNotificationId = firstNotificationId

        override fun onDownloadChanged(
            manager: DownloadManager,
            download: Download
        ) {

            val media = getMediaFromBytes(download.request.data)
            val notification: Notification
            when (download.state) {
                Download.STATE_COMPLETED -> {
                    updateDownloads(download)

                    coroutineScope.launch {
                        val newMediaDownload = media.copy(isDownloaded = true)
                        localRepository.updateDownloadState(newMediaDownload)
                    }

                    notification = downloadNotificationHelper.buildDownloadCompletedNotification(
                        R.drawable.ic_download_done,  /* contentIntent= */
                        null,
                        media.info
                    )
                }
                Download.STATE_FAILED -> {
                    updateDownloads(download)

                    notification = downloadNotificationHelper.buildDownloadFailedNotification(
                        R.drawable.ic_error_outline,  /* contentIntent= */
                        null,
                        media.info
                    )
                }

                else -> {
                    updateDownloads(download)
                    return
                }
            }

            NotificationUtil.setNotification(context, nextNotificationId++, notification)
        }

        private fun updateDownloads(download: Download) {
            val updateDownloadIndex =
                downloadsList.indexOfFirst { it.request.id == download.request.id }

            if (updateDownloadIndex != -1) {
                downloadsList[updateDownloadIndex] = download
                downloadsLiveData.postValue(downloadsList.map { DownloadMedia(it) })
            }
        }
    }

    companion object {

        fun getMediaFromBytes(data: ByteArray): Media {
            val jsonMedia = Util.fromUtf8Bytes(data)
            return Json.parse(Media.serializer(), jsonMedia)
        }

        fun getCurrentDownloads():LiveData<List<DownloadMedia>> = downloadsLiveData

        private val downloadsLiveData: MutableLiveData<List<DownloadMedia>> = MutableLiveData()

        private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "xplayer_download_channel"
        private const val DOWNLOAD_NOTIFICATION_ID = 2
        private const val JOB_ID = 1
    }
}

