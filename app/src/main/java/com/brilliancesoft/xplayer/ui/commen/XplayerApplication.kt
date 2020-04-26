package com.brilliancesoft.xplayer.ui.commen

import androidx.multidex.MultiDexApplication
import com.abed.magentaX.android.MagentaX
import com.abed.magentaX.android.utils.AppPreferences
import com.abed.magentaX.standard.delegation.DelegatesExt
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.di.KoinComponent
import com.brilliancesoft.xplayer.ui.player.MediaDownloadService
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.File

/**
 * Created by ${User} on ${Date}
 */
class XplayerApplication : MultiDexApplication() {

    companion object {
        var app: XplayerApplication by DelegatesExt.notNullSingleValue()
            private set

        val appName: String by lazy { app.getString(R.string.app_name) }

        val downloadCache: SimpleCache by lazy {
            SimpleCache(
                app.getDownloadDirectory(),
                NoOpCacheEvictor(),
                databaseProvider
            )
        }

        var userAgent: String by DelegatesExt.notNullSingleValue()
            private set

        private lateinit var databaseProvider: ExoDatabaseProvider

        lateinit var downloadManager: DownloadManager
            private set


        private const val DOWNLOAD_AUDIO_DIRECTORY = "downloads"
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        databaseProvider = ExoDatabaseProvider(this)

        MagentaX.init(this)
        UserPreferences.init(AppPreferences())

        initInjection()
        initDownloadManager()

    }

    private fun initInjection() {
        startKoin {
            androidLogger()
            androidContext(this@XplayerApplication)
            modules(KoinComponent.appComponent)
        }
    }

    private fun initDownloadManager() {
        downloadManager = DownloadManager(
            app,
            databaseProvider,
            downloadCache,
            DefaultHttpDataSourceFactory(userAgent)
        )

        DownloadMediaUtils.resumeDownloads()
    }

    private fun getDownloadDirectory(): File =
        File(getExternalFilesDir(null), DOWNLOAD_AUDIO_DIRECTORY)

}