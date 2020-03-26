package com.brilliancesoft.xplayer.framework.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranMp3 {

    @GET("get_json.php")
    fun getSupportedLanguage(): Deferred<Response<ApiModels.SupportedLanguages>>

    @GET("{language}_sura.json")
    fun getSurasName(
        @Path("language") language: String
    ): Deferred<Response<ApiModels.SurasNames>>

    @GET("{language}.json")
    fun getReciters(
        @Path("language") language: String
    ): Deferred<Response<ApiModels.Reciters>>

    @GET("http://api.mp3quran.net/verse/reciters_verse{language}.json")
    fun getRecitersByVerse(@Path("language") language: String): Deferred<Response<ApiModels.RecitersByVerse>>

    @GET("http://api.mp3quran.net/radios/get_radios.php")
    fun getRadiosUrl(): Deferred<Response<ApiModels.SupportedRadioUrl>>

    @GET("http://api.mp3quran.net/radios/radio{language}.json")
    fun getRadiosByLanguage(@Path("language") language: String): Deferred<Response<ApiModels.RadiosList>>


}