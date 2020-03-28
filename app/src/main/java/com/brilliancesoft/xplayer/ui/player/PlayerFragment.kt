package com.brilliancesoft.xplayer.ui.player

import android.os.Bundle
import com.abed.kotlin_recycler.withSimpleAdapter
import com.abed.magentaX.android.resoures.colorOf
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.listeners.onClicks
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.utils.DownloadMediaUtils
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.viewExtensions.addTopInsetMargin
import com.brilliancesoft.xplayer.utils.viewExtensions.disabled
import com.brilliancesoft.xplayer.utils.viewExtensions.enabled
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.item_current_playlist.*

class PlayerFragment : BaseFragment() {

    private var previousPlayMedia: Media? = null
    private var currentPlayMedia: Media? = null

    private var currentPlaylist = listOf<Media>()
    private val parentActivity: MainActivity
        get() = activity as MainActivity

    override val layoutId: Int = R.layout.fragment_player


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentPlayerRoot.addTopInsetMargin()

        parentActivity.getCurrentPlayMedia()?.let {
            dispatchMediaToUI(it, currentPlaylist)
        }

        downloadMediaButton.setOnClickListener {
            if (downloadMediaButton.isEnabled)
                parentActivity.getCurrentPlayMedia()?.let {
                    DownloadMediaUtils.download(it, parentActivity)
                }
        }

    }

    fun dispatchMediaToUI(media: Media, playlist: List<Media>) {

        currentPlaylist = playlist
        val mediaInfo =
            if (media.subtitle.isNotEmpty()) media.title + ": " + media.subtitle else media.title

        currentMediaTitle.text = mediaInfo
        currentMediaTitle.visible()

        if (currentPlaylistRecycler.adapter == null) initPlaylistRecycler()

        previousPlayMedia = currentPlayMedia
        currentPlayMedia = media

        if (playlist.size > 1) {
            downloadMediaButton.gone()
            currentPlaylistRecycler.visible()
            currentPlaylistRecycler.adapter!!.notifyDataSetChanged()
        } else
            currentPlaylistRecycler.gone()

        changeDownloadButtonState()
    }


    private fun initPlaylistRecycler() {
        val playerService = parentActivity.getPlayerService()
        currentPlaylistRecycler.withSimpleAdapter(
            currentPlaylist,
            R.layout.item_current_playlist
        ) { media ->

            mediaTitlePlayer.text = media.title
            mediaSubtitlePlayer.text = media.subtitle

            if (media.isDownloaded) downloadedMediaItem.setImageResource(R.drawable.ic_download_done)
            else downloadedMediaItem.setImageResource(R.drawable.ic_download)


            downloadedMediaItem.onClick {
                if (media.isNotDownloaded)
                    DownloadMediaUtils.download(media, parentActivity)
            }

            onClicks(itemView, mediaItemButton) {
                itemView.setBackgroundColor(colorOf(R.color.colorPrimaryDark))
                playerService!!.moveToMedia(media)
            }

            if (media == currentPlayMedia) itemView.setBackgroundColor(colorOf(R.color.colorPrimaryDark))
            else itemView.background = null
        }
    }

    private fun changeDownloadButtonState() {
        if (currentPlaylist.isNotEmpty())
            downloadMediaButton.gone()
        else {
            downloadMediaButton.visible()
            if (currentPlayMedia!!.isNotDownloaded) {
                downloadMediaButton.text = getString(R.string.downloaded)
                downloadMediaButton.enabled()
            } else {
                downloadMediaButton.text = getString(R.string.download)
                downloadMediaButton.disabled()
            }
        }
    }

}