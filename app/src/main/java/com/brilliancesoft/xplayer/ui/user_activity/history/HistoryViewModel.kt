package com.brilliancesoft.xplayer.ui.user_activity.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brilliancesoft.xplayer.framework.data.LocalRepository
import com.brilliancesoft.xplayer.model.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryViewModel(private val localRepository: LocalRepository) : ViewModel() {

    private lateinit var historyMediaLiveData: MutableLiveData<List<Media>>

    fun updateMediaHistory(media: Media) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePlayingDate(media)
        }
    }

    fun getAllMediaWithHistory(): LiveData<List<Media>> {
        if (!::historyMediaLiveData.isInitialized)
            historyMediaLiveData = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getMediaWithPlayingHistory()
                .collect { historyMediaLiveData.postValue(it) }
        }
        return historyMediaLiveData
    }
    fun getMediaWithHistory(numberOfRecords:Int): LiveData<List<Media>> {
        if (!::historyMediaLiveData.isInitialized)
            historyMediaLiveData = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getMediaWithPlayingHistory(numberOfRecords)
                .collect { historyMediaLiveData.postValue(it) }
        }
        return historyMediaLiveData
    }

}