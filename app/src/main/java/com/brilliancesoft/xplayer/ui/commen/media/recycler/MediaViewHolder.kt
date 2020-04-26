package com.brilliancesoft.xplayer.ui.commen.media.recycler

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.BaseModel
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.player.MediaViewDownloadHelper
import kotlinx.android.synthetic.main.item_media.*


class MediaViewHolder(
    parent: ViewGroup,
    layoutId: Int,
    private val showPlayingDate: Boolean,
    private val isAddedToPlaylist: Boolean,
    mediaClickListener: RecyclerPressListener<Media>
) : BaseMediaViewHolder(parent, layoutId, mediaClickListener) {


    override fun onBind(baseModel: BaseModel) {
        super.onBind(baseModel)
        val media = (baseModel as Media)

        onBindMedia(media, showPlayingDate)
        media_play.onClicked(media)

        if (!isAddedToPlaylist) {
            media_add_to_playlist.onClicked(media)
        } else {
            media_add_to_playlist.onClick { media_add_to_playlist.showRemoveFormList(media) }
            media_add_to_playlist.setImageResource(R.drawable.ic_playlist_added)
        }

        MediaViewDownloadHelper(
            media,
            media_download_loading,
            media_download,
            mediaClickListener
        ).prepare()

    }


    private fun View.showRemoveFormList(media: Media) {
        val popup =
            PopupMenu(
                context,
                this,
                Gravity.CENTER,
                android.R.attr.popupMenuStyle,
                R.style.popupMenu
            )

        popup.inflate(R.menu.popup_playlist_remove)
        popup.menu.findItem(R.id.remove_from_playlist).setOnMenuItemClickListener {
            mediaClickListener.onItemClick(media, R.id.remove_from_playlist)
            true
        }

        popup.show()
    }

}