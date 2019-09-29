package com.abed.xplayer.framework.di

import com.abed.xplayer.BASE_URL
import com.abed.xplayer.BuildConfig
import com.abed.xplayer.framework.api.QuranMp3
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.framework.data.Repository
import com.abed.xplayer.framework.utils.CacheManager
import com.abed.xplayer.ui.downloaded.DownloadedViewModel
import com.abed.xplayer.ui.home.HomeViewModel
import com.abed.xplayer.ui.playList.PlaylistViewModel
import com.abed.xplayer.ui.recitersList.SurahViewModel
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
            Repository(get(), get())
        }

        single {
            FirebaseRepository()
        }

        factory {
            CacheManager()
        }
    }

    val QuranMp3API = module {
        single<QuranMp3> {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val timeout: Long = if (BuildConfig.DEBUG) 10 else 30
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
    }

}