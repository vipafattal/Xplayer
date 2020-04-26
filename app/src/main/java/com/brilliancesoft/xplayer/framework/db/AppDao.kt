package com.brilliancesoft.xplayer.framework.db

import androidx.room.*
import com.brilliancesoft.xplayer.model.Media
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("select * from DOWNLOADED_MEDIA where link like :mediaLink limit 1")
    fun getDownloadedMedia(mediaLink: String): Media?

    @Query("select * from DOWNLOADED_MEDIA where isDownloaded like 1")
    fun getAllDownloaded(): Flow<List<Media>>

    @Query("select * from DOWNLOADED_MEDIA where playData is not null order by playData desc")
    fun getMediaByPlayingDate(): Flow<List<Media>>

    @Query("select * from DOWNLOADED_MEDIA where playData is not null order by playData desc limit :numberOfRecords")
    fun getMediaByPlayingDate(numberOfRecords:Int): Flow<List<Media>>

    @Update
    suspend fun updateMedia(media: Media)

    @Insert
    suspend fun insertMedia(media: Media): Long

    @Delete
    suspend fun removeMedia(media: Media)
}