package com.brilliancesoft.xplayer.framework.utils

import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import java.io.File

/**
 * Created by Abed on
 */
object DataDirectories {

    private val rootFolder = XplayerApplication.appContext.getString(R.string.app_name)
    private val recitersFolder = "reciters"

    /**
     * Creates a [File] with specific folder name [Media.title] and file name [Media.title].
     **/
    fun buildSurahFile(media: Media): File {
        return File(
            XplayerApplication.appContext.getExternalFilesDir(null),
            getReciterSurahPath(
                media.title,
                media.subtitle!!
            )
        )
    }

    fun getRecitersFolder() = File(
        XplayerApplication.appContext.getExternalFilesDir(null),
        "$rootFolder/$recitersFolder/"
    )

    private fun getReciterSurahPath(reciterName: String, fileName: String) =
        getReciterFolderPath(reciterName).getSurahFilePath(fileName)

    private fun getReciterFolderPath(reciterName: String) =
        "$rootFolder/$recitersFolder/$reciterName"

    private fun String.getSurahFilePath(fileName: String) = "$this/$fileName.mp3"

}

