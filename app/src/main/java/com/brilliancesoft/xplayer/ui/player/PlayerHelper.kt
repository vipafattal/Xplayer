package com.brilliancesoft.xplayer.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brilliancesoft.xplayer.model.Media

object PlayerHelper {

    private var currentPlaylist: MutableLiveData<List<Media>> = MutableLiveData()

    fun getMediaList():LiveData<List<Media>> {
        return currentPlaylist
    }

    fun updateMediaList(mediaList:List<Media>) {
        currentPlaylist.value = mediaList
    }

}