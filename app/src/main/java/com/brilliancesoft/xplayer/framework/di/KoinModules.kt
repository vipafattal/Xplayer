package com.brilliancesoft.xplayer.framework.di

import com.abed.magentaX.android.utils.AppPreferences
import com.brilliancesoft.xplayer.BASE_URL
import com.brilliancesoft.xplayer.BuildConfig
import com.brilliancesoft.xplayer.framework.api.QuranMp3
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.framework.data.Repository
import com.brilliancesoft.xplayer.ui.downloaded.DownloadedViewModel
import com.brilliancesoft.xplayer.ui.home.HomeViewModel
import com.brilliancesoft.xplayer.ui.language.LanguagesViewModel
import com.brilliancesoft.xplayer.ui.playlist.PlaylistViewModel
import com.brilliancesoft.xplayer.ui.surahList.SurahViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by ${User} on ${Date}
 */

object KoinModules {

    val dataSource = module {
        single {
            Repository(get())
        }

        single {
            FirebaseRepository(get(),get())
        }

        factory {
            AppPreferences()
        }
    }

    val QuranMp3API = module {
        single<QuranMp3> {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val timeout: Long = if (BuildConfig.DEBUG) 5000 else 30
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return@single retrofit.create(QuranMp3::class.java)
        }
    }

    val viewModelFactory = module {
        viewModel { HomeViewModel(get()) }
        viewModel { DownloadedViewModel() }
        viewModel { PlaylistViewModel(get()) }
        viewModel { SurahViewModel(get()) }
        viewModel { LanguagesViewModel(get()) }
    }

    val firebaseAPIs = module {
        factory { FirebaseFirestore.getInstance() }
        factory { FirebaseAuth.getInstance() }

    }


}