package com.abed.xplayer.ui.sharedComponent

import androidx.multidex.MultiDexApplication
import com.abed.xplayer.R
import com.abed.xplayer.framework.di.KoinComponent
import com.codebox.lib.android.os.MagentaX
import com.codebox.lib.standard.delegation.DelegatesExt
import com.google.android.exoplayer2.util.Util
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by ${User} on ${Date}
 */
class XplayerApplication : MultiDexApplication() {

    companion object {
        var xplayer: XplayerApplication by DelegatesExt.notNullSingleValue()
            private set

        var userAgent: String by DelegatesExt.notNullSingleValue()
            private set
    }


    override fun onCreate() {
        super.onCreate()
        xplayer = this
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