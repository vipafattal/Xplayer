package com.brilliancesoft.xplayer.ui.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brilliancesoft.xplayer.framework.data.Repository
import com.brilliancesoft.xplayer.model.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by  on
 */
class LanguagesViewModel(private val repository: Repository) : ViewModel() {

    private lateinit var _languagesData: MutableLiveData<List<Language>>

    fun getLanguages(): LiveData<List<Language>> {

        if (!::_languagesData.isInitialized)
            _languagesData = MutableLiveData()

        if (_languagesData.value == null || _languagesData.value!!.isEmpty()) {
            viewModelScope.launch {
                val languages = withContext(Dispatchers.IO) { repository.getSupportedLanguages() }
                _languagesData.value = languages
            }
        }

        return _languagesData
    }

}