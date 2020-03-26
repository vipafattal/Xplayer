package com.brilliancesoft.xplayer.ui.commen

import androidx.multidex.MultiDexApplication
import com.abed.magentaX.android.MagentaX
import com.abed.magentaX.standard.delegation.DelegatesExt
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.di.KoinComponent
import com.google.android.exoplayer2.util.Util
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by ${User} on ${Date}
 */
class XplayerApplication : MultiDexApplication() {

    companion object {
        var appContext: XplayerApplication by DelegatesExt.notNullSingleValue()
            private set

        var userAgent: String by DelegatesExt.notNullSingleValue()
            private set
    }


    override fun onCreate() {
        super.onCreate()
        appContext = this
        userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        MagentaX.init(this)
        initInjection()
    }

    private fun initInjection() {
        startKoin {
            androidLogger()
            androidContext(this@XplayerApplication)
            modules(KoinComponent.appComponent)
        }
    }

}