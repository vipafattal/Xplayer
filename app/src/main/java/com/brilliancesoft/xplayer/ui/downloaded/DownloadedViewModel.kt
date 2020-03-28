package com.brilliancesoft.xplayer.ui.downloaded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brilliancesoft.xplayer.framework.utils.DataDirectories
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Reciter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by  on
 */
class DownloadedViewModel : ViewModel() {

    private val downloadedMediaList = mutableListOf<Media>()
    private var _liveData = MutableLiveData<List<Media>>()

    fun getDownloadedMedia(): LiveData<List<Media>> {
        if (downloadedMediaList.isEmpty()) loadData()
        return _liveData
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadedMediaList.clear()

            val file = DataDirectories.getRecitersFolder()

            val recitersFolder = file.listFiles()
            if (recitersFolder != null)
                for (reciterFolder in recitersFolder) {
                    reciterFolder.listFiles().forEach {
                        //Checking that this file is not a directory (folder).
                        if (it.isFile) {

                            val surahName = it.nameWithoutExtension
                            val reciter = Reciter("", reciterFolder.name, "")
                            val media = Media.create(surahName, reciter)

                            if (media.isDownloaded)
                                downloadedMediaList.add(media)
                        }
                    }
                }

            withContext(Dispatchers.Main) { _liveData.value = downloadedMediaList }
        }
    }

    fun removeMediaAndGetIndex(media: Media): Int {
        val index = downloadedMediaList.indexOf(media)
        downloadedMediaList.removeAt(index)
        return index
    }
}

