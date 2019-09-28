package com.abed.xplayer.framework.di

import com.abed.xplayer.BASE_URL
import com.abed.xplayer.BuildConfig
import com.abed.xplayer.framework.api.QuranMp3
import com.abed.xplayer.framework.utils.CacheManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.UnstableDefault
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by ${User} on ${Date}
 */

@Module
open class AppModule {

    @ApplicationScope
    @Provides
    fun quranCloudAPI(): QuranMp3 {
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

        return retrofit.create(QuranMp3::class.java)
    }

    @UnstableDefault
    @ApplicationScope
    @Provides
    fun cacheManagerProvider(): CacheManager =
        CacheManager()

}