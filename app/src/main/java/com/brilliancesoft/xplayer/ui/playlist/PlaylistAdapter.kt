package com.brilliancesoft.xplayer.ui.playlist

import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_playlist.view.*
import com.brilliancesoft.xplayer.R

/**
 * Created by  on
 */
class PlaylistAdapter(
    dataList: List<Playlist>,
    private val itemPressListener: ItemPressListener<Playlist>
) : BaseRecyclerAdapter<Playlist>(dataList) {


    override val layoutItemId: Int = R.layout.item_playlist

    override fun onBindViewHolder(holder: RecyclerViewHolder<Playlist>, position: Int) {
        val data = dataList[position].run { copy(name = name.replace("\r\n", "")) }

        holder.itemView.apply {
            playlistName.text = data.name

            playButtonPlaylist.onClick {
                val mainActivity = holder.itemView.context as MainActivity
                mainActivity.playMediaList(data.list)
            }

            onClick {
                itemPressListener.onItemClick(data)
            }

        }

    }

}