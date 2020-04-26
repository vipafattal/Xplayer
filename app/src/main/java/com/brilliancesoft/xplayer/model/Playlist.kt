package com.brilliancesoft.xplayer.model

import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.serialization.Serializable

/**
 * Created by Abed on 2019-9-17
 */
@Serializable
data class Playlist(
    val name: String = "",
    val userId: String = "",
    val id:String = "",
    val list: List<Media> = listOf()
): BaseModel {

    inner class Action(private val firebaseRepository: FirebaseRepository) {
        fun delete() = firebaseRepository.deletePlaylist(id)
        fun removeMedia(media:Media) = firebaseRepository.updateMediaInPlaylist(media, id, isAdd = false)
    }

}

