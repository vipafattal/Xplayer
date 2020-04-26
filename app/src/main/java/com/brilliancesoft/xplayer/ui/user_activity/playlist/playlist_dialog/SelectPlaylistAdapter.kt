package com.brilliancesoft.xplayer.ui.user_activity.playlist.playlist_dialog;

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_dialog_playlist_name.view.*

/**
 * Created by  on
 */
class SelectPlaylistAdapter(
    private val dataList: List<Playlist>,
    private val onPlaylistSelected: RecyclerPressListener<Playlist>
) :
    RecyclerView.Adapter<RecyclerViewHolder<Playlist>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder<Playlist> =
        RecyclerViewHolder(
            parent,
            R.layout.item_dialog_playlist_name
        )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder<Playlist>, position: Int) {
        val playlist = dataList[position]
        val mContext = holder.itemView.context

        holder.itemView.apply {
            playListDialogName.text = playlist.name

            onClick {
                onPlaylistSelected.onItemClick(playlist,-1)
            }
        }
    }
}