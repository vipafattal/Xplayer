package com.brilliancesoft.xplayer.ui.user_activity.downloaded

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brilliancesoft.xplayer.framework.data.LocalRepository
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by  on
 */
class DownloadedViewModel(private val localRepository: LocalRepository) : ViewModel() {

    private lateinit var downloadedMedia : MutableLiveData<List<Media>>


    fun getDownloadedMedia(): LiveData<List<Media>> {
        if (!::downloadedMedia.isInitialized)
            downloadedMedia = MutableLiveData()

        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getAllDownloadedMedia().collect { downloadedMedia.postValue(it) }
        }
        return downloadedMedia
    }

    fun deleteMedia(media: Media) {
       viewModelScope.launch(Dispatchers.IO) {
           CacheUtil.remove(
               XplayerApplication.downloadCache,
               CacheUtil.generateKey(Uri.parse(media.link))
           )
           localRepository.updateDownloadState(media.copy(isDownloaded = false))
       }
    }
}

