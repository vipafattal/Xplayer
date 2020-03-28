package com.brilliancesoft.xplayer.model

import com.brilliancesoft.xplayer.framework.utils.DownloadMediaUtils
import com.google.firebase.firestore.Exclude


/**
 * Created by  on
 */

data class Media(
    val reciterId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val link: String = "",
    @Exclude val isRadio: Boolean = false
) {
    val isDownloaded: Boolean
        get() = !isRadio && DownloadMediaUtils.isDownloaded(this)

    val isNotDownloaded: Boolean
        get() = !isDownloaded


    companion object {

        fun create(sura: Sura, reciter: Reciter): Media {
            val mediaFinalPart = when (val numberInMushaf = sura.numberInMushaf.toInt()) {
                in 1..9 -> "00$numberInMushaf"
                in 10..99 -> "0$numberInMushaf"
                else -> numberInMushaf.toString()
            } + ".mp3"

            return Media(
                reciter.id,
                reciter.name,
                sura.name,
                link = reciter.servers + '/' + mediaFinalPart
            )
        }

        fun create(suraName: String, reciter: Reciter): Media {
            return Media(reciter.id, reciter.name, suraName, link = "")
        }

        fun create(radioName: String, radio: Radio): Media {
            return Media(title = radioName, link = radio.url, isRadio = true)
        }
    }

}
