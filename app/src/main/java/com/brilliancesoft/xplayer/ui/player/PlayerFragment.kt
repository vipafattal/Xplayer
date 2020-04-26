package com.brilliancesoft.xplayer.ui.player

import android.os.Bundle
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils
import com.brilliancesoft.xplayer.utils.viewExtensions.addTopInsetMargin
import com.brilliancesoft.xplayer.utils.viewExtensions.disabled
import com.brilliancesoft.xplayer.utils.viewExtensions.enabled
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : BaseFragment() {

    private var previousPlayMedia: Media? = null
    private var currentPlayMedia: Media? = null

    private var currentPlaylist = listOf<Media>()
    private val parentActivity: MainActivity
        get() = activity as MainActivity


    override val layoutId: Int = R.layout.fragment_player

    override fun onActivityCreated() {
        fragmentPlayerRoot.addTopInsetMargin()

        parentActivity.getCurrentPlayMedia()?.let {
            dispatchMediaToUI(it, currentPlaylist)
        }

        downloadMediaButton.setOnClickListener {
            if (downloadMediaButton.isEnabled)
                parentActivity.getCurrentPlayMedia()?.let {
                    DownloadMediaUtils.download(it)
                }
        }
    }

    fun dispatchMediaToUI(media: Media, playlist: List<Media>) {
        currentPlaylist = playlist
        currentMediaTitle.text = media.info
        currentMediaTitle.visible()

        if (currentPlaylistRecycler.adapter == null)
            initPlaylistRecycler()

        previousPlayMedia = currentPlayMedia
        currentPlayMedia = media

        if (playlist.size > 1) {
            downloadMediaButton.gone()
            currentPlaylistRecycler.visible()
            currentPlaylistRecycler.adapter!!.notifyDataSetChanged()
        } else {
            downloadMediaButton.visible()
            currentPlaylistRecycler.gone()
        }

        changeDownloadButtonState()
    }


    private fun initPlaylistRecycler() {
        currentPlaylistRecycler.adapter = PlayerPlaylistAdapter(currentPlaylist, currentPlayMedia)
    }

    private fun changeDownloadButtonState() {
        if (currentPlaylist.isNotEmpty())
            downloadMediaButton.gone()
        else {
            downloadMediaButton.visible()
            if (!currentPlayMedia!!.isDownloaded) {
                downloadMediaButton.text = getString(R.string.downloaded)
                downloadMediaButton.enabled()
            } else {
                downloadMediaButton.text = getString(R.string.download)
                downloadMediaButton.disabled()
            }
        }
    }

}