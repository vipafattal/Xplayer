package com.abed.xplayer.framework.utils

import android.os.Environment
import com.abed.xplayer.R
import com.abed.xplayer.model.Media
import com.abed.xplayer.ui.sharedComponent.XplayerApplication
import java.io.File

/**
 * Created by Abed on
 */
object DataDirectories {

    private val rootFolder = XplayerApplication.xplayer.getString(R.string.app_name)
    private val recitersFolder = "reciters"

    /**
     * Creates a [File] with specific folder name [Media.title] and file name [Media.title].
     **/
    fun buildSurahFile(media: Media): File {
        return File(
            Environment.getExternalStorageDirectory(),
            getReciterSurahPath(
                media.title,
                media.subtitle!!
            )
        )
    }

    fun buildSurahFile(reciterName: String, fileName: String): File {
        return File(
            Environment.getExternalStorageDirectory(),
            getReciterSurahPath(
                reciterName,
                fileName
            )
        )
    }

    fun getRecitersFolder() = "$rootFolder/$recitersFolder/"

    private fun getReciterSurahPath(reciterName: String, fileName: String) =
        getReciterFolderPath(reciterName).getSurahFilePath(fileName)

    private fun getReciterFolderPath(reciterName: String) =
        "$rootFolder/$recitersFolder/$reciterName"

    private fun String.getSurahFilePath(fileName: String) = "$this/$fileName.mp3"

}

