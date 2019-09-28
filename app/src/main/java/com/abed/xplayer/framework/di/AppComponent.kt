package com.abed.xplayer.framework.di

import com.abed.xplayer.ui.home.HomeFragment
import com.abed.xplayer.framework.di.activityComponent.ActivityComponent
import com.abed.xplayer.framework.data.Repository
import com.abed.xplayer.ui.playList.playlistDialog.PlayListNameDialog
import com.abed.xplayer.ui.MainActivity
import com.abed.xplayer.ui.playList.MediaPlaylistFragment
import com.abed.xplayer.ui.playList.PlayListFragment
import com.abed.xplayer.ui.playList.playlistDialog.SelectPlaylistDialog
import com.abed.xplayer.ui.recitersList.TruckListFragment
import dagger.Component

/**
 * Created by ${User} on ${Date}
 */
@ApplicationScope
@Component(modules = [AppModule::class])
interface AppComponent{
    fun getActivityComponent() :ActivityComponent.Builder
    fun inject(repository: Repository)
    fun inject(mainActivity: MainActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(reciterListFragment: TruckListFragment)
    fun inject(playListNameDialog: PlayListNameDialog)
    fun inject(playListFragment: PlayListFragment)
    fun inject(selectPlayListDialog: SelectPlaylistDialog)
    fun inject(mediaPlaylistFragment: MediaPlaylistFragment)
}