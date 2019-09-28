package com.abed.xplayer.framework.di.activityComponent

import com.abed.xplayer.ui.MainActivity
import dagger.Subcomponent

/**
 * Created by ${User} on ${Date}
 */

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun activityInject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        fun requestModule(module: ActivityModule): Builder
        fun build(): ActivityComponent
    }
}