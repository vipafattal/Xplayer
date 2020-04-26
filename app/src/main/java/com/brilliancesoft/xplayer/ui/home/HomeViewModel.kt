package com.brilliancesoft.xplayer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brilliancesoft.xplayer.framework.data.Repository
import com.brilliancesoft.xplayer.model.Radio
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.commen.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by  on
 */
class HomeViewModel(
    private val repository: Repository
) : ViewModel() {

    private lateinit var radiosLiveData: MutableLiveData<List<Radio>>
    private lateinit var recitersLiveData: MutableLiveData<List<Reciter>>
    private val language = UserPreferences.getAppLanguage()!!.language

    init {
        if (!::recitersLiveData.isInitialized) {
            recitersLiveData = MutableLiveData()
            radiosLiveData = MutableLiveData()
        }
    }

    fun getRadioList(): LiveData<List<Radio>> {
        viewModelScope.launch {
            if (radios.isEmpty())
                radios =
                    withContext(Dispatchers.IO) { repository.getRadiosByLanguage(language) }
            radiosLiveData.value = radios
        }

        return radiosLiveData
    }

    fun getRecitersList(): LiveData<List<Reciter>> {
        viewModelScope.launch {
            if (reciters.isEmpty())
                reciters = withContext(Dispatchers.IO) { repository.getReciters(language) }
            recitersLiveData.value = reciters
        }
        return recitersLiveData
    }

    companion object {
        private var reciters = listOf<Reciter>()
        private var radios = listOf<Radio>()
    }

}