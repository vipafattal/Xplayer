package com.abed.xplayer.framework.di.activityComponent

import android.util.Log
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by ${User} on ${Date}
 */
@Module
open class ActivityModule {

    @ActivityScope
    @Provides
    fun coroutineScopeProvider(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

}