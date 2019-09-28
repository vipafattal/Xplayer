package com.abed.xplayer.ui.downloaded

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abed.xplayer.framework.utils.DataDirectories
import com.abed.xplayer.model.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

            val file = File(
                Environment.getExternalStorageDirectory(),
                DataDirectories.getRecitersFolder()
            )

            val recitersFolder = file.listFiles()
            for (reciterFolder in recitersFolder) {
                reciterFolder.listFiles().forEach {
                    //Checking that this file is not a directory (folder).
                    if (it.isFile)
                        downloadedMediaList.add(
                            Media(
                                title = reciterFolder.name,
                                subtitle = it.nameWithoutExtension
                            )
                        )
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

