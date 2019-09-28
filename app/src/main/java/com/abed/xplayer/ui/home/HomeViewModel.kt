package com.abed.xplayer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abed.xplayer.framework.data.Repository
import com.abed.xplayer.model.Radio
import com.abed.xplayer.model.Reciter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.UnstableDefault

/**
 * Created by  on
 */
class HomeViewModel(private val repository: Repository) : ViewModel() {
    private lateinit var radiosData: MutableLiveData<List<Radio>>
    private lateinit var recitersData: MutableLiveData<List<Reciter>>

    private val language = "_arabic"

    @UnstableDefault
    fun getRadioList(): LiveData<List<Radio>> {
        if (!::radiosData.isInitialized)
            radiosData = MutableLiveData()

        if (radiosData.value == null) {
            viewModelScope.launch {
                val radios = withContext(Dispatchers.IO) { repository.getRadiosByLanguage(language) }
                radiosData.value = radios
            }
        }

        return radiosData
    }
    @UnstableDefault
    fun getRecitersList(): LiveData<List<Reciter>> {
        if (!::recitersData.isInitialized)
            recitersData = MutableLiveData()

        if (recitersData.value == null) {
            viewModelScope.launch {
                val reciters = withContext(Dispatchers.IO) { repository.getReciters(language) }
                recitersData.value = reciters
            }
        }

        return recitersData
    }

}