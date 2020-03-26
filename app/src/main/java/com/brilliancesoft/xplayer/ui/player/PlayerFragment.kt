package com.brilliancesoft.xplayer.ui.player

import android.os.Bundle
import com.abed.kotlin_recycler.withSimpleAdapter
import com.abed.magentaX.android.resoures.colorOf
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.utils.DownloadMediaUtils
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.observer
import com.brilliancesoft.xplayer.utils.viewExtensions.addTopInsetPadding
import com.brilliancesoft.xplayer.utils.viewExtensions.disabled
import com.brilliancesoft.xplayer.utils.viewExtensions.enabled
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.item_current_playlist.*

class PlayerFragment : BaseFragment() {

    private var currentPlayMedia: Media? = null
    private var currentPlaylist = listOf<Media>()
    private val parentActivity: MainActivity
        get() = activity as MainActivity

    override val layoutId: Int = R.layout.fragment_player

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentPlayerRoot.addTopInsetPadding()

        parentActivity.getCurrentPlayMedia()?.let {
            dispatchMediaToUI(it)
        }

        downloadMediaButton.setOnClickListener {
            if (downloadMediaButton.isEnabled)
                parentActivity.getCurrentPlayMedia()?.let {
                    DownloadMediaUtils.download(it, parentActivity)
                }
        }

        PlayerHelper.getMediaList().observer(viewLifecycleOwner) { playlist ->
            if (playlist.isNotEmpty()) {
                downloadMediaButton.gone()
                currentPlaylist = playlist
                updatePlaylistRecycler()
            } else currentPlaylistRecycler.gone()
        }
    }


    fun dispatchMediaToUI(media: Media) {
        currentPlaylistRecycler

        currentMediaTitle.text = media.title + ": " + media.subtitle
        currentMediaTitle.visible()

        if (currentPlaylist.isNotEmpty())
            updateCurrentPlayBackgroundItem(currentPlayMedia, media)

        currentPlayMedia = media

        changeDownloadButtonState()
    }


    private fun updatePlaylistRecycler() {

        val playerService = parentActivity.getPlayerService()!!

        currentPlaylistRecycler.visible()

        currentPlaylistRecycler.withSimpleAdapter(
            currentPlaylist,
            R.layout.item_current_playlist
        ) { media ->

            mediaTitlePlayer.text = media.title
            mediaSubtitlePlayer.text = media.subtitle

            itemView.onClick {
                setBackgroundColor(colorOf(R.color.colorPrimaryDark))
                playerService.moveToMedia(media)
            }
        }
    }

    private fun updateCurrentPlayBackgroundItem(oldMedia: Media?, newMedia: Media) {
        val previousPlayIndex = currentPlaylist.indexOf(oldMedia)
        val currentPlayIndex = currentPlaylist.indexOf(newMedia)

        if (previousPlayIndex != -1)
            currentPlaylistRecycler.getChildAt(previousPlayIndex).background = null
        if (currentPlayIndex != -1)
            currentPlaylistRecycler.getChildAt(currentPlayIndex)
                .setBackgroundColor(colorOf(R.color.colorPrimaryDark))

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