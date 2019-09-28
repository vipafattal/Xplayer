package com.abed.xplayer.ui.sharedComponent.recyclerAdapters

import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.abed.xplayer.R
import com.abed.xplayer.framework.utils.DownloadMediaUtils
import com.abed.xplayer.model.Media
import com.abed.xplayer.ui.MainActivity
import com.abed.xplayer.ui.sharedComponent.BaseRecyclerAdapter
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.RecyclerViewHolder
import com.codebox.lib.android.views.listeners.onClick
import com.codebox.lib.android.views.utils.invisible
import com.codebox.lib.android.views.utils.visible
import kotlinx.android.synthetic.main.item_media.view.*

/**
 * Created by  on
 */
class MediaListAdapter(
    data: List<Media>,
    private val itemPressListener: ItemPressListener<Media>
) : BaseRecyclerAdapter<Media>(data) {

    override val layoutItemId: Int = R.layout.item_media

    override fun onBindViewHolder(holder: RecyclerViewHolder<Media>, position: Int) {
        val media = dataList[position]
        val mainActivity = holder.itemView.context as MainActivity

        holder.itemView.apply {
            mediaTitle.text = media.title
            mediaSubtitle.text = media.subtitle

            if (DownloadMediaUtils.isDownloaded(media)) mediaDownloadedImage.visible()
            else mediaDownloadedImage.invisible()

            mediaItemButton.onClick {
                showPopup(this, media, mainActivity)
            }

            onClick {
                playListAt(media, mainActivity)
            }
        }
    }

    private fun showPopup(v: View, media: Media, mainActivity: MainActivity) {
        val popup =
            PopupMenu(v.context, v, Gravity.END, android.R.attr.popupMenuStyle, R.style.popupMenu)
        popup.inflate(R.menu.popup_media_list)

        if (media.isDownloaded)
            popup.menu.findItem(R.id.download_media_from_playlist).title = "Delete downloaded file"
        if(media.link.isEmpty())
            popup.menu.removeItem(R.id.remove_from_playlist)
        popup.show()
        popup.setPopupClickListener(media, mainActivity)
    }

    private fun PopupMenu.setPopupClickListener(
        media: Media,
        mainActivity: MainActivity
    ) {
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.play_media_from_list -> playListAt(media, mainActivity)
                R.id.remove_from_playlist -> removeFromPlayList(media)
                R.id.download_media_from_playlist -> {
                    if (!media.isDownloaded)
                        DownloadMediaUtils.download(media, mainActivity)
                    else
                        itemPressListener.onItemClick(media)
                }
            }
            false
        }
    }

    private fun playListAt(media: Media, mainActivity: MainActivity) {
        mainActivity.playMediaList(
            dataList,
            dataList.indexOf(media)
        )
    }

    private fun removeFromPlayList(media: Media) {
        itemPressListener.onItemClick(media)
    }

}