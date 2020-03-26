package com.brilliancesoft.xplayer.framework.data

import com.brilliancesoft.xplayer.framework.api.QuranMp3
import com.brilliancesoft.xplayer.framework.utils.CacheManager
import com.brilliancesoft.xplayer.model.Language
import com.brilliancesoft.xplayer.model.Radio
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.model.Sura
import com.brilliancesoft.xplayer.utils.ConnectivityHelper
import com.brilliancesoft.xplayer.utils.onComplete
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Deferred
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.list
import retrofit2.Response

/**
 * Created by ${User} on ${Date}
 */
class Repository(private val quranMp3: QuranMp3, private val cacheManager: CacheManager) {
    var loadingStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
        private set


    @UnstableDefault
    suspend fun getRadiosByLanguage(language: String): List<Radio>? {
        var radios: List<Radio> = emptyList()

        if (ConnectivityHelper.isConnectedToNetwork()) {

            quranMp3.getRadiosUrl().request { supportedUrls ->

                val isRadioLanguageSupported =
                    supportedUrls.radios.any { it.url.endsWith("$language.json") }

                //supportedUrls.radios contains "language".json then this language has radio.
                if (isRadioLanguageSupported)
                    quranMp3.getRadiosByLanguage(language).request {
                        radios = it.radios

                        cacheManager.saveList(
                            cacheKey = RadiosListKey + language,
                            dataSerializer = Radio.serializer().list, data = radios
                        )
                    }
                else
                    radios = mutableListOf(Radio(url = "", name = ""))
            }
        }
        if (radios.isEmpty())
            radios = cacheManager.getSavedList(RadiosListKey + language, Radio.serializer().list)

        return radios
    }

    @UnstableDefault
    suspend fun getReciters(language: String): List<Reciter> {
        var reciters: List<Reciter> = emptyList()

        if (ConnectivityHelper.isConnectedToNetwork()) {
            quranMp3.getReciters(language).request {
                reciters = it.reciters.filter { it.numberOfAvailableSuras == 114 }
                cacheManager.saveList(
                    cacheKey = RecitersListKey + language,
                    dataSerializer = Reciter.serializer().list,
                    data = reciters
                )
            }
        }
        if (reciters.isEmpty())
            reciters =
                cacheManager.getSavedList(RecitersListKey + language, Reciter.serializer().list)

        return reciters
    }


    @UnstableDefault
    suspend fun getSupportedLanguages(): List<Language> {
        var languages: List<Language> = emptyList()
        if (ConnectivityHelper.isConnectedToNetwork())
            quranMp3.getSupportedLanguage().request {
                languages = it.languages
            }

        return languages
    }

    @UnstableDefault
    suspend fun getSurahsLanguage(language: String): List<Sura> {
        var surahs: List<Sura> =
            cacheManager.getSavedList(SurasNameKey + language, Sura.serializer().list)

        if (surahs.isEmpty() && ConnectivityHelper.isConnectedToNetwork())
            quranMp3.getSurasName(language).request {
                surahs = it.suras
                cacheManager.saveList(
                    cacheKey = SurasNameKey + language,
                    dataSerializer = Sura.serializer().list,
                    data = surahs
                )
            }
        return surahs
    }

    private suspend inline fun <reified T> Deferred<Response<T>>.request(onSuccess: (T) -> Unit) {
        onComplete({
            onSuccess(it)
        }, onError = {
            errorStream.onNext(it)
        })
    }


    companion object {
        private const val RecitersListKey = "RecitersKey"
        private const val RadiosListKey = "RadiosKey"
        private const val SurasNameKey = "SurasNameKey"

        val errorStream: BehaviorSubject<String> = BehaviorSubject.create()

    }

}