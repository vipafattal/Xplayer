package com.abed.xplayer.ui.playList

import com.abed.xplayer.R
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.MainActivity
import com.abed.xplayer.ui.sharedComponent.BaseRecyclerAdapter
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.RecyclerViewHolder
import com.codebox.lib.android.views.listeners.onClick
import kotlinx.android.synthetic.main.item_playlist.view.*

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
            playListName.text = data.name

            playButtonPlayList.onClick {
                val mainActivity = holder.itemView.context as MainActivity
                mainActivity.playMediaList(data.list)
            }

            onClick {
                itemPressListener.onItemClick(data)
            }

        }

    }

}