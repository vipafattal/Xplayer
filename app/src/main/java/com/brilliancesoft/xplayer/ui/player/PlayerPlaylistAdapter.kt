package com.brilliancesoft.xplayer.ui.player

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.abed.magentaX.android.resoures.colorOf
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.listeners.onClicks
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils
import kotlinx.android.synthetic.main.item_current_playlist.*

class PlayerPlaylistAdapter(
    playlist: List<Media>,
    private val currentPlayMedia: Media?
) : BaseRecyclerAdapter<Media>(playlist) {

    override val layoutItemId: Int = R.layout.item_current_playlist
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Media> {
        return ViewHolder(parent, layoutItemId, currentPlayMedia)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<Media>, position: Int) {
        holder.onBind(dataList[position])
    }

    class ViewHolder(
        parent: ViewGroup, @LayoutRes layoutId: Int,
        private val currentPlayMedia: Media?
    ) :
        RecyclerViewHolder<Media>(parent, layoutId){

        override fun onBind(media: Media) {
            mediaTitlePlayer.text = media.reciterName
            mediaSubtitlePlayer.text = media.surahName

            if (media.isDownloaded) downloadedMediaItem.setImageResource(R.drawable.ic_download_done)
            else downloadedMediaItem.setImageResource(R.drawable.ic_download)


            downloadedMediaItem.onClick {
                if (!media.isDownloaded)
                    DownloadMediaUtils.download(media)
            }

            onClicks(itemView, mediaItemButton) {
                itemView.setBackgroundColor(colorOf(R.color.colorPrimaryDark))
                (itemView.context as MainActivity).getPlayerService()!!.moveToMedia(media)
            }

            if (media == currentPlayMedia) itemView.setBackgroundColor(colorOf(R.color.colorPrimaryDark))
            else itemView.background = null
        }
    }

}