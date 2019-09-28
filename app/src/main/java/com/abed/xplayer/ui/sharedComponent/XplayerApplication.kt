package com.abed.xplayer.ui.sharedComponent

import androidx.multidex.MultiDexApplication
import com.abed.xplayer.R
import com.abed.xplayer.framework.di.AppComponent
import com.abed.xplayer.framework.di.DaggerAppComponent
import com.codebox.lib.android.os.MagentaX
import com.codebox.lib.standard.delegation.DelegatesExt
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.util.Util
import kotlin.properties.Delegates

/**
 * Created by ${User} on ${Date}
 */
class XplayerApplication : MultiDexApplication() {

    companion object {
        var appComponent: AppComponent by DelegatesExt.notNullSingleValue()
            private set

        var xplayer: XplayerApplication by DelegatesExt.notNullSingleValue()
            private set

        var userAgent:String by  DelegatesExt.notNullSingleValue()
            private set
    }


    override fun onCreate() {
        super.onCreate()
        xplayer = this
        userAgent = Util.getUserAgent(this,getString(R.string.app_name))
        MagentaX.init(this)
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().build()
    }

    fun getActivityComponent() = appComponent.getActivityComponent().build()

}