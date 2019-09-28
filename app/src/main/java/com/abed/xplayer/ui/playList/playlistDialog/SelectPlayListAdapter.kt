package com.abed.xplayer.ui.playList.playlistDialog;

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abed.xplayer.R
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.RecyclerViewHolder
import com.codebox.lib.android.resoures.Image
import com.codebox.lib.android.views.listeners.onClick
import kotlinx.android.synthetic.main.item_dialog_playlist_name.view.*

/**
 * Created by  on
 */
class SelectPlayListAdapter(
    private val dataList: List<Playlist>,
    private val onPlaylistSelected: ItemPressListener<Playlist>
) :
    RecyclerView.Adapter<RecyclerViewHolder<Playlist>>() {
    private val endDrawable = Image(R.drawable.ic_add)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder<Playlist> =
        RecyclerViewHolder(parent, R.layout.item_dialog_playlist_name)

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder<Playlist>, position: Int) {
        val playlist = dataList[position]
        val mContext = holder.itemView.context

        holder.itemView.apply {
            playListDialogName.text = playlist.name

            onClick {
                onPlaylistSelected.onItemClick(playlist)
            }
        }
    }
}