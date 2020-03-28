package com.brilliancesoft.xplayer.framework.utils

import com.abed.magentaX.android.utils.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * Created by ${User} on ${Date}
 */
object CacheHelper {

    private const val cacheVersion = 1
    private const val cacheVersionKey = "Cache-Version-Key"
    private const val cachedDataFileName = "Cached-Data"
    private val preferences = AppPreferences.getInstance(cachedDataFileName)

    init {
        prepareCache()
    }

    private fun prepareCache() {
        val currentSavedCacheVersion = preferences.getInt(cacheVersionKey)
        if (currentSavedCacheVersion != cacheVersion) {
            preferences.clear()
            preferences.put(
                cacheVersionKey,
                cacheVersion
            )
        }
    }

    @UnstableDefault
    fun <T> getSavedObject(
        cacheKey: String,
        deserializationStrategy: DeserializationStrategy<T>
    ): T? {
        val data = preferences.getStr(cacheKey, "")
        return if (data.isNotEmpty())
            Json.parse(deserializationStrategy, data)
        else null
    }

    @UnstableDefault
    suspend fun <T> getSavedList(cacheKey: String, dataSerializer: KSerializer<List<T>>): List<T> {
        val data = preferences.getStr(cacheKey, "")
        return if (data.isNotEmpty())
            withContext(Dispatchers.IO) { Json.parse(dataSerializer, data) }
        else emptyList()
    }

    @UnstableDefault
    fun <T> saveObject(cacheKey: String, serializationStrategy: SerializationStrategy<T>, data: T) {
        val serializedObject = Json.stringify(serializationStrategy, data)
        preferences.put(cacheKey, serializedObject)
    }

    @UnstableDefault
    suspend fun <T> saveList(
        cacheKey: String,
        dataSerializer: KSerializer<List<T>>,
        data: List<T>
    ) {
        val serializedData =
            withContext(Dispatchers.IO) { Json.stringify(dataSerializer, data) }
        preferences.put(cacheKey, serializedData)
    }

    fun saveString(cacheKey: String, data: String) {
        preferences.put(cacheKey, data)
    }

    fun getString(cacheKey: String, defaultValue: String = "") =
        preferences.getStr(cacheKey, defaultValue)

    fun remove(cacheKey: String) =
        preferences.remove(cacheKey)

}