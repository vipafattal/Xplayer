package com.brilliancesoft.xplayer.framework.data

import com.brilliancesoft.xplayer.framework.db.AppDao
import com.brilliancesoft.xplayer.model.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class LocalRepository(private val appDao: AppDao) {

    private fun getDownloadMedia(mediaLink: String): Media? = appDao.getDownloadedMedia(mediaLink)

    fun getAllDownloadedMedia(): Flow<List<Media>> = appDao.getAllDownloaded().distinctUntilChanged()

    fun getMediaWithPlayingHistory(): Flow<List<Media>> = appDao.getMediaByPlayingDate().distinctUntilChanged()
    fun getMediaWithPlayingHistory(numberOfRecords:Int): Flow<List<Media>> = appDao.getMediaByPlayingDate(numberOfRecords).distinctUntilChanged()

    suspend fun updateDownloadState(media: Media) {
        insertOrUpdateMediaDownload(media, media.isDownloaded)
    }

    suspend fun updatePlayingDate(media: Media) {
        require(media.playData != null) { "playDate mustn't be null" }
        insertOrUpdateMediaDate(media, media.playData!!)
    }

    private suspend fun insertOrUpdateMediaDownload(media: Media, downloadState: Boolean) {
        val savedMedia = getDownloadMedia(media.link)
        if (savedMedia != null) {
            savedMedia.isDownloaded = downloadState
            appDao.updateMedia(savedMedia)
            return
        }
        appDao.insertMedia(media)
    }

    private suspend fun insertOrUpdateMediaDate(media: Media, date: Long) {
        val savedMedia = getDownloadMedia(media.link)
        if (savedMedia != null) {
            savedMedia.playData = date
            appDao.updateMedia(savedMedia)
            return
        }
        appDao.insertMedia(media)
    }
}