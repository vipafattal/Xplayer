package com.brilliancesoft.xplayer.ui.user_activity.playlist

import androidx.lifecycle.*
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.model.Playlist

/**
 * Created by Abed on 2019/9/20
 */
class PlaylistViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private lateinit var playlistLiveData: LiveData<List<Playlist>>

    fun getPlayLists(): LiveData<List<Playlist>> {
        if (!::playlistLiveData.isInitialized)
            playlistLiveData = MutableLiveData()

        if (playlistLiveData.value == null) {
                val playlist = firebaseRepository.getUserPlayLists()
                playlistLiveData = playlist

        }
        return playlistLiveData
    }

}