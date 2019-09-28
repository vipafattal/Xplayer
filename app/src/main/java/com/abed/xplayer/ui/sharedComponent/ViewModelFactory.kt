package com.abed.xplayer.ui.sharedComponent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.framework.di.ApplicationScope
import com.abed.xplayer.framework.data.Repository
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.home.HomeViewModel
import com.abed.xplayer.ui.playList.PlaylistViewModel
import com.abed.xplayer.ui.recitersList.SurahViewModel
import javax.inject.Inject

@ApplicationScope
class ViewModelFactory @Inject constructor(var repository: Repository,var firebaseRepository: FirebaseRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel: ViewModel =  when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(
                repository
            )
            modelClass.isAssignableFrom(SurahViewModel::class.java) -> SurahViewModel(
                repository
            )
            modelClass.isAssignableFrom(PlaylistViewModel::class.java) -> PlaylistViewModel(
                firebaseRepository
            )
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
        return viewModel as T
    }

}