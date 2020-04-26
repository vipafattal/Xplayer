package com.brilliancesoft.xplayer.framework.di

import org.koin.core.module.Module

/**
 * Created by  on
 */

object KoinComponent {
    val appComponent: List<Module> =
        listOf(
            KoinModules.QuranMp3API,
            KoinModules.dataProviders,
            KoinModules.repositories,
            KoinModules.viewModelFactory,
            KoinModules.firebaseAPIs
        )
}