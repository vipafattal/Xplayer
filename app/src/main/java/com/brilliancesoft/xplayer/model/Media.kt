package com.brilliancesoft.xplayer.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.brilliancesoft.xplayer.framework.db.DOWNLOADED_MEDIA_TABLE
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Entity(tableName = DOWNLOADED_MEDIA_TABLE)
data class Media(
    @PrimaryKey
    var link: String = "",
    var reciterId: String = "",
    var reciterName: String = "",
    var surahName: String = "",
    @Exclude
    var isDownloaded: Boolean = false,
    @Exclude
    var playData: Long? = null,
    @Ignore
    @Exclude val isRadio: Boolean = false
) : BaseModel {
    //surahName is empty when playing radio.
    val info: String
        @Exclude get() = if (surahName.isNotEmpty()) "$reciterName: $surahName" else reciterName

    fun getMediaPlayDate(): Date? {
        return if (playData != null)
            Date(playData!!) else null
    }

    companion object {
        fun create(sura: Sura, reciter: Reciter, isDownloaded: Boolean): Media {
            return Media(
                link = createLink(sura, reciter.servers),
                reciterId = reciter.id,
                reciterName = reciter.name,
                surahName = sura.name,
                isDownloaded = isDownloaded, playData = null
            )
        }

        fun createLink(sura: Sura, reciterServer: String): String {
            val mediaFinalPart = when (val numberInMushaf = sura.numberInMushaf.toInt()) {
                in 1..9 -> "00$numberInMushaf"
                in 10..99 -> "0$numberInMushaf"
                else -> numberInMushaf.toString()
            } + ".mp3"

            return "$reciterServer/$mediaFinalPart"
        }


        fun create(radioName: String, radio: Radio): Media {
            return Media(
                reciterName = radioName,
                link = radio.url,
                isDownloaded = false,
                isRadio = true,
                playData = null
            )
        }
    }

}
