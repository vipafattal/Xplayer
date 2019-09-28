package com.abed.xplayer.ui.playList

import androidx.lifecycle.*
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.model.Sura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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