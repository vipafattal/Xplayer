package com.brilliancesoft.xplayer.ui.player.helpers

import android.content.Context
import android.net.Uri
import com.brilliancesoft.xplayer.R

import com.brilliancesoft.xplayer.framework.utils.DataDirectories
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.player.MediaDownloadService
import com.google.android.exoplayer2.offline.*
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.*

/**
 * Created by Abed on 2019/9/24
 */

object DownloadMediaUtils {

    private val context: Context = XplayerApplication.app
    private val downloadManager: DownloadManager
        get() = XplayerApplication.downloadManager

    /**
     * Returns a true if the given folder name [Media] and the file name [Media.surahName] is existed and is not downloading.
     */
    fun isDownloaded(media: Media): Boolean {
        val suraFile =
            DataDirectories.buildSurahFile(
                media
            )
        return suraFile.exists() && !isDownloading(
            media
        )
    }

    /**
     * Download [media] by [Media.link] and save it to cache so to check if download not completed yet.
     * It gets removed from cache when download completed
     * @see CheckDownloadComplete
     */
    fun download(media: Media):Boolean {
        var downloadStarted = true
        if (!isDownloading(media)) {
            val uri = Uri.parse(media.link)

            val downloadHelper = DownloadHelper.forProgressive(uri)
            downloadHelper.prepare(object : DownloadHelper.Callback {
                override fun onPrepared(helper: DownloadHelper?) {}
                override fun onPrepareError(helper: DownloadHelper?, e: IOException?) {}
            })

            val downloadId = UUID.randomUUID().toString()


            val downloadRequest = DownloadRequest(
                downloadId,
                DownloadRequest.TYPE_PROGRESSIVE,
                uri,  /* streamKeys= */
                Collections.emptyList(),  /* customCacheKey= */
                null,
                Json.stringify(Media.serializer(), media).toByteArray()
            )

            DownloadService.sendAddDownload(
                context,
                MediaDownloadService::class.java,
                downloadRequest,
                true
            )

        } else
            downloadStarted = false


        return downloadStarted
    }


    fun download(request: DownloadRequest) {
        DownloadService.sendAddDownload(context, MediaDownloadService::class.java, request, true)
    }

    fun removeDownload(download: Download) {
        DownloadService.sendRemoveDownload(
            context,
            MediaDownloadService::class.java,
            download.request.id,
            true
        )
    }

    fun removeDownload(media: Media) {
        val uri = Uri.parse(media.link)
        val download =
            XplayerApplication.downloadManager.currentDownloads.first { it.request.uri == uri }

        DownloadService.sendRemoveDownload(
            context,
            MediaDownloadService::class.java,
            download.request.id,
            true
        )
    }

    fun resumeDownloads() {
        if (downloadManager.currentDownloads.isNotEmpty() && downloadManager.downloadsPaused && !downloadManager.isWaitingForRequirements)
            DownloadService.sendResumeDownloads(context, MediaDownloadService::class.java, true)
    }

    fun isDownloading(media: Media): Boolean {
        val uri = Uri.parse(media.link)

        val download =
            XplayerApplication.downloadManager.currentDownloads.firstOrNull { it.request.uri == uri }
        return download != null
    }

}