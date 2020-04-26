package com.brilliancesoft.xplayer.ui.commen.media

import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.abed.magentaX.android.views.invisible
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_surah.view.*


class OldMediaListAdapter(
    private val playlist: Playlist,
    private val recyclerPressListener: RecyclerPressListener<Media>
) : BaseRecyclerAdapter<Media>(playlist.list) {

    override val layoutItemId: Int = R.layout.item_surah

    override fun onBindViewHolder(holder: RecyclerViewHolder<Media>, position: Int) {
        val media = dataList[position]
        val mainActivity = holder.itemView.context as MainActivity

        holder.itemView.apply {
            mediaSubtitle.text = media.reciterName
            mediaTitle.text = media.surahName

            if (DownloadMediaUtils.isDownloaded(media)) mediaDownloadedImage.visible()
            else mediaDownloadedImage.invisible()

            mediaItemButton.onClick {
                showPopup(this, media, mainActivity)
            }

            onClick {
                //playListAt(media, mainActivity)
            }
        }
    }

    private fun showPopup(v: View, media: Media, mainActivity: MainActivity) {

        val popup =
            PopupMenu(v.context, v, Gravity.END, android.R.attr.popupMenuStyle, R.style.popupMenu)

        popup.inflate(R.menu.popup_playlist_remove)

       /* if (media.isDownloaded)
            popup.menu.findItem(R.id.download_media_from_playlist).title = "Delete downloaded file"
        if (media.link.isEmpty())
            popup.menu.removeItem(R.id.remove_from_playlist)
*/
        popup.show()
        popup.setPopupClickListener(media, mainActivity)
    }

    private fun PopupMenu.setPopupClickListener(
        media: Media,
        mainActivity: MainActivity
    ) {
       /* setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.play_media_from_list -> playListAt(media, mainActivity)
                R.id.remove_from_playlist -> recyclerPressListener.onItemClick(media, R.id.remove_from_playlist)
                R.id.download_media_from_playlist -> {
                    if (!media.isDownloaded)
                        DownloadMediaUtils.download(media, mainActivity)
                    else
                        recyclerPressListener.onItemClick(media, R.id.download_media_from_playlist)
                }
            }
            false
        }*/
    }

    private fun playListAt(media: Media, mainActivity: MainActivity) {
        mainActivity.playMediaList(playlist, dataList.indexOf(media))
    }

}