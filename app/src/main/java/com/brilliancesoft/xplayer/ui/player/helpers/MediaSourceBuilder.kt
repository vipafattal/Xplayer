package com.brilliancesoft.xplayer.ui.player.helpers

import android.net.Uri
import androidx.core.net.toUri
import com.brilliancesoft.xplayer.framework.utils.DataDirectories
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource

object MediaSourceBuilder {


    fun create(mediaList: List<Media>, eachTruck: Int = 1, wholeSet: Int = 1): MediaSource {
        val mediaSourceArray = arrayOfNulls<MediaSource>(mediaList.size)

        for ((index, media) in mediaList.withIndex())
            if (media.isDownloaded)
                mediaSourceArray[index] = offlineSource(media, eachTruck)
            else
                mediaSourceArray[index] = onlineSource(media, eachTruck)

        return if (wholeSet > 1) LoopingMediaSource(
            ConcatenatingMediaSource(*mediaSourceArray),
            wholeSet
        ) else ConcatenatingMediaSource(*mediaSourceArray)
    }

    fun create(media: Media, eachTruck: Int = 1): MediaSource {
        return if (media.isDownloaded)
            offlineSource(media, eachTruck)
        else
            onlineSource(media, eachTruck)


    }

    fun onlineSource(
        mediaList: List<Media>,
        eachTruck: Int = 1,
        wholeSet: Int = 1
    ): MediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory(XplayerApplication.userAgent)
        val mediaSourceArray = arrayOfNulls<MediaSource>(mediaList.size)
        for (i in mediaList.indices) {
            val media = mediaList[i]
            val audioUri = Uri.parse(media.link)
            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory).setTag(media)
                .createMediaSource(audioUri)

            mediaSourceArray[i] =
                if (eachTruck > 1) LoopingMediaSource(audioSource, eachTruck) else audioSource
        }
        return if (wholeSet > 1) LoopingMediaSource(
            ConcatenatingMediaSource(*mediaSourceArray),
            wholeSet
        ) else ConcatenatingMediaSource(*mediaSourceArray)
    }

    fun onlineSource(
        media: Media,
        eachTruck: Int = 1,
        wholeSet: Int = 1
    ): MediaSource {


        val dataSourceFactory = DefaultHttpDataSourceFactory(XplayerApplication.userAgent)
        val audioUri = Uri.parse(media.link)
        val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory).setTag(media)
            .createMediaSource(audioUri)

        val mediaSource =
            if (eachTruck > 1) LoopingMediaSource(audioSource, eachTruck) else audioSource

        return if (wholeSet > 1) LoopingMediaSource(
            ConcatenatingMediaSource(mediaSource),
            wholeSet
        ) else mediaSource
    }

    fun offlineSource(
        media: Media,
        loopTimes: Int = 1
    ): MediaSource {
        val uri = DataDirectories.buildSurahFile(media).toUri()
        val dataSpec = DataSpec(uri)
        val fileDataSource = FileDataSource()
        try {
            fileDataSource.open(dataSpec)
        } catch (e: FileDataSource.FileDataSourceException) {
            e.printStackTrace()
        }
        val factoryDataSource = DataSource.Factory { fileDataSource }
        val audioSource =
            ProgressiveMediaSource.Factory(factoryDataSource).setTag(media).createMediaSource(uri)

        return if (loopTimes > 1) LoopingMediaSource(audioSource, loopTimes) else audioSource
    }

    fun offlineSource(
        mediaList: List<Media>,
        eachTruck: Int,
        wholeSet: Int
    ): MediaSource {
        val mediaSourceArray = arrayOfNulls<MediaSource>(mediaList.size)

        for ((index, media) in mediaList.withIndex()) {
            val uri = DataDirectories.buildSurahFile(media).toUri()
            val dataSpec = DataSpec(uri)
            val fileDataSource = FileDataSource()
            try {
                fileDataSource.open(dataSpec)
            } catch (e: FileDataSource.FileDataSourceException) {
                e.printStackTrace()
            }
            val factoryDataSource = DataSource.Factory { fileDataSource }
            val audioSource = ProgressiveMediaSource.Factory(factoryDataSource).setTag(media)
                .createMediaSource(uri)
            mediaSourceArray[index] =
                if (eachTruck > 1) LoopingMediaSource(audioSource, eachTruck) else audioSource
        }
        return if (wholeSet > 1) LoopingMediaSource(
            ConcatenatingMediaSource(*mediaSourceArray),
            wholeSet
        ) else ConcatenatingMediaSource(*mediaSourceArray)
    }

}