package com.brilliancesoft.xplayer.ui.player

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.PopupMenu
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils

class MediaViewDownloadHelper(
    val media: Media,
    val downloadProgressBar: ProgressBar,
    val downloadImage: ImageView,
    val recyclerPressListener: RecyclerPressListener<Media>?
) {
    private val context = downloadImage.context

    fun prepare() {
        when {
            media.isDownloaded -> {
                downloadImage.setImageResource(R.drawable.ic_downloaded)
                downloadImage.onClick { showPopup(media) }
            }
            DownloadMediaUtils.isDownloading(media) -> {
                showLoading()
                downloadImage.onClick {
                    cancelDownloading(media)
                }
            }
            else -> {
                downloadImage.setImageResource(R.drawable.ic_download)
                downloadImage.onClick {
                    if (DownloadMediaUtils.download(media))
                        showLoading()
                    else
                        cancelDownloading(media)
                }
            }
        }
    }

    private fun showLoading() {
        downloadImage.setImageResource(R.drawable.ic_stop)
        downloadProgressBar.visible()
    }

    private fun cancelDownloading(media: Media) {
        DownloadMediaUtils.removeDownload(media)
        downloadImage.setImageResource(R.drawable.ic_download)
    }

    private fun View.showPopup(media: Media) {
        val popup = createPopup(this)
        val menu = popup.menu

        menu.findItem(R.id.remove_download).title =
            context.getString(R.string.exo_download_removing)
        menu.removeItem(R.id.retry_download)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.remove_download -> {
                    downloadImage.setImageResource(R.drawable.ic_download)

                    recyclerPressListener?.onItemClick(
                        media,
                        R.id.remove_download
                    )
                }
                R.id.retry_download -> throw IllegalArgumentException("Mustn't showing Retry item")
            }
            true
        }
        popup.show()
    }

    private fun createPopup(view: View): PopupMenu {
        val popup =
            PopupMenu(
                view.context,
                view,
                Gravity.CENTER,
                android.R.attr.popupMenuStyle,
                R.style.popupMenu
            )

        popup.inflate(R.menu.popup_download_actions)

        return popup
    }

}