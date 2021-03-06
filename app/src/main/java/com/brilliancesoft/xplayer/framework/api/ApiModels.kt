package com.brilliancesoft.xplayer.framework.api

import com.brilliancesoft.xplayer.model.*
import com.google.gson.annotations.SerializedName

/**
 * Created by ${User} on ${Date}
 */
object ApiModels {
    data class SupportedLanguages(@SerializedName("language") val languages: List<Language>)
    data class SurasNames(@SerializedName("Suras_Name") val suras: List<Sura>)
    data class Reciters(val reciters: List<Reciter>)
    data class RecitersByVerse(@SerializedName("reciters_verse") val reciters: List<ReciterVerse>)

    data class RadiosList(val radios: List<Radio>)
    data class SupportedRadioUrl(@SerializedName("language") val radios: List<Radio>)

}