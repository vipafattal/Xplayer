package com.brilliancesoft.xplayer.model

/**
 * Created by Abed on 2019-9-17
 */
data class Playlist(
    val name: String = "",
    val userId: String = "",
    val id:String = "",
    val list: List<Media> = listOf()
) {
    companion object {
        const val PLAYLIST_DOWNLOADED_ID = "playlist downloaded"
    }
}

