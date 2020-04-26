package com.brilliancesoft.xplayer.ui.user_activity.playlist

import android.content.Context
import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import com.brilliancesoft.xplayer.ui.user_activity.UserActivityFragmentDirections
import com.brilliancesoft.xplayer.utils.getLocalizedSurah
import kotlinx.android.synthetic.main.item_playlist.view.*
import kotlinx.serialization.json.Json

/**
 * Created by  on
 */
class PlaylistAdapter(dataList: List<Playlist>) : BaseRecyclerAdapter<Playlist>(dataList) {

    override val layoutItemId: Int = R.layout.item_playlist

    override fun onBindViewHolder(holder: RecyclerViewHolder<Playlist>, position: Int) {
        val playlist = dataList[position].run { copy(name = name.replace("\r\n", "")) }

        holder.itemView.apply {
            playlist_name.text = playlist.name
            var info = ""
            val mediaList = playlist.list
            for (index in mediaList.indices) {
                val media = mediaList[index]
                info += media.surahName + if (mediaList.lastIndex != index) ", " else ""
            }
            number_of_media_playlist.text = mediaList.size.getLocalizedSurah()
            playlist_content.text = info
            playlist_play.onClick {
                val mainActivity = holder.itemView.context as MainActivity
                mainActivity.playMediaList(playlist)
            }

            onClick {
                context.openMediaList(playlist)
            }
        }
    }

    private fun Context.openMediaList(playlist: Playlist){
        val jsonPlaylist = Json.stringify(Playlist.serializer(), playlist)
        (this as MainActivity).navController.navigate(
            UserActivityFragmentDirections.actionUserActivityFragmentToMediaPlaylistFragment(jsonPlaylist))

    }

}