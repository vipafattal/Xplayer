package com.brilliancesoft.xplayer.ui.user_activity.downloaded.recycler

import android.view.View
import android.view.ViewGroup
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.BaseModel
import com.brilliancesoft.xplayer.model.DownloadMedia
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.media.recycler.BaseMediaViewHolder
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.player.MediaDownloadService
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils
import com.google.android.exoplayer2.offline.Download
import kotlinx.android.synthetic.main.item_media.*

class DownloadViewHolder(
    parent: ViewGroup,
    layoutId: Int,
    private val showPlayingDate: Boolean,
    mediaClickListener: RecyclerPressListener<Media>
) : BaseMediaViewHolder(parent, layoutId, mediaClickListener) {

    override fun onBind(baseModel: BaseModel) {
        super.onBind(baseModel)

        val download = (baseModel as DownloadMedia).download
        val media = MediaDownloadService.getMediaFromBytes(download.request.data)

        if (download.state == Download.STATE_FAILED) {
            media_download_loading.gone()
            media_download.setImageResource(R.drawable.ic_error_outline)
        } else if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_QUEUED) {
            media_download.setImageResource(R.drawable.ic_stop)
            media_download_loading.visible()
        }
        media_download.onClick { showPopup(download) }
        onBindMedia(media, showPlayingDate)
    }

    private fun View.showPopup(download: Download) {
        val popup = createPopup(this)
        val menu = popup.menu

        if (download.state == Download.STATE_FAILED) menu.findItem(R.id.remove_download).title =
            context.getString(R.string.exo_download_removing)
        else menu.removeItem(R.id.retry_download)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.remove_download -> DownloadMediaUtils.removeDownload(download)
                R.id.retry_download -> DownloadMediaUtils.resumeDownloads()

            }
            true
        }
        popup.show()
    }
}