package com.brilliancesoft.xplayer.ui.player.helpers

import android.net.Uri
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil

object MediaSourceBuilder {


    fun create(
        mediaList: List<Media>,
        eachTruck: Int = 1,
        wholeSet: Int = 1
    ): ConcatenatingMediaSource {
        val mediaSourceArray = arrayOfNulls<MediaSource>(mediaList.size)

        for ((index, media) in mediaList.withIndex())
            if (media.isDownloaded)
                mediaSourceArray[index] = offlineSource(media, eachTruck)
            else
                mediaSourceArray[index] = onlineSource(media, eachTruck)

        return ConcatenatingMediaSource(*mediaSourceArray)
    }

    fun create(media: Media, eachTruck: Int = 1): MediaSource {
        return if (media.isDownloaded)
            offlineSource(media, eachTruck)
        else
            onlineSource(media, eachTruck)
    }

    private fun onlineSource(
        media: Media,
        eachTruck: Int = 1,
        wholeSet: Int = 1
    ): ConcatenatingMediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory(XplayerApplication.userAgent)
        val audioUri = Uri.parse(media.link)

        val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory).setTag(media)
            .createMediaSource(audioUri)
        return ConcatenatingMediaSource(audioSource)
    }

    private fun offlineSource(
        media: Media,
        loopTimes: Int = 1
    ): ConcatenatingMediaSource {


        val dataSource = CacheDataSourceFactory(
            XplayerApplication.downloadCache,
            DataSource.Factory { FileDataSource() },
            DataSource.Factory { FileDataSource() },
            null,
            0,
            null
        )

        val mediaSource = ProgressiveMediaSource.Factory(dataSource).setTag(media)
            .createMediaSource(Uri.parse(media.link))

        return ConcatenatingMediaSource(mediaSource)
    }

/*    private  fun offlineSource(
        mediaList: List<Media>,
        eachTruck: Int,
        wholeSet: Int
    ): ConcatenatingMediaSource {
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
        return ConcatenatingMediaSource(*mediaSourceArray)
    }*/

/*    fun onlineSource(
        mediaList: List<Media>,
        eachTruck: Int = 1,
        wholeSet: Int = 1
    ): ConcatenatingMediaSource {
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
        return ConcatenatingMediaSource(*mediaSourceArray)
    }*/


}