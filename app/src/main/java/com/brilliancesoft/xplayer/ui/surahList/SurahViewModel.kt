package com.brilliancesoft.xplayer.ui.surahList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brilliancesoft.xplayer.framework.data.Repository
import com.brilliancesoft.xplayer.model.Sura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.UnstableDefault

/**
 * Created by  on
 */
class SurahViewModel(private val repository: Repository) : ViewModel() {
    private lateinit var surahsData: MutableLiveData<List<Sura>>


    @UnstableDefault
    fun getSurahList(language: String): LiveData<List<Sura>> {
        if (!::surahsData.isInitialized)
            surahsData = MutableLiveData()

        if (surahsData.value == null) {
            viewModelScope.launch {
                val radios = withContext(Dispatchers.IO) { repository.getSurahsLanguage(language) }
                surahsData.value = radios
            }
        }

        return surahsData
    }


}