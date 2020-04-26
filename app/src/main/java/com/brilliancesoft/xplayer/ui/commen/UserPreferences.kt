package com.brilliancesoft.xplayer.ui.commen

import android.content.Context
import com.abed.magentaX.android.utils.AppPreferences
import com.brilliancesoft.xplayer.model.Language
import kotlinx.serialization.json.Json

object UserPreferences {

    private lateinit var appPreferences: AppPreferences

    fun init(preferences: AppPreferences) {
        appPreferences = preferences
       // val currentLanguage = preferences.getStr(SettingsPreferencesConstant.AppLanguageKey, "")
        //If this first time user launch the app, we will save system app local.
    }

    fun getAppLanguage(): Language? {
        val jsonLanguage = appPreferences.getStr(PreferencesKeys.RECITING_LANGUAGE, "")
        return if (jsonLanguage.isEmpty()) null else Json.parse(Language.serializer(), jsonLanguage)
    }

    fun saveAppLanguage(language: Language) {
        val jsonLanguage = Json.stringify(Language.serializer(), language)
        appPreferences.put(PreferencesKeys.RECITING_LANGUAGE, jsonLanguage)
    }

}