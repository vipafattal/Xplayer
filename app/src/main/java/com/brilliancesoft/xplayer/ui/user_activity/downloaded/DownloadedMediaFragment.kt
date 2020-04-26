package com.brilliancesoft.xplayer.ui.user_activity.downloaded


import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.media.BaseMediaFragment
import com.brilliancesoft.xplayer.ui.commen.media.recycler.DefaultMediaAdapter
import com.brilliancesoft.xplayer.ui.player.MediaDownloadService
import com.brilliancesoft.xplayer.ui.player.helpers.DownloadMediaUtils
import com.brilliancesoft.xplayer.utils.observer
import com.google.android.exoplayer2.offline.Download
import kotlinx.android.synthetic.main.fragment_downloaded.*

class DownloadedMediaFragment : BaseMediaFragment(false) {

    private val currentDownloadingAdapter = DefaultMediaAdapter(false, getMediaClickListener())
    override val layoutId: Int = R.layout.fragment_downloaded
    override val playlist: Playlist = Playlist(id = DOWNLOADED_MEDIA_LIST_ID)

    override fun onActivityCreated(){

        downloadedMediaRecycler.adapter = mediaAdapter
        currentDownloadingRecycler.adapter = currentDownloadingAdapter

        downloadedMediaViewModel.getDownloadedMedia().observer(viewLifecycleOwner) { mediaList ->
            mediaAdapter.updateList(mediaList.sortedBy { it.reciterId })
            showNoDataIfIsEmpty()
        }

        MediaDownloadService.getCurrentDownloads().observer(viewLifecycleOwner) { downloads ->
            currentDownloadingAdapter.updateList(downloads.filter { it.download.state != Download.STATE_COMPLETED })
            showNoDataIfIsEmpty()
        }
    }

    override fun onResume() {
        super.onResume()
        DownloadMediaUtils.resumeDownloads()
    }

    private fun showNoDataIfIsEmpty() {
        if (mediaAdapter.getList().isEmpty() && currentDownloadingAdapter.getList().isEmpty())
            showNoDataView(R.string.no_media_downloaded)
        else
            hideNoDataView()
    }

    companion object {
        private const val DOWNLOADED_MEDIA_LIST_ID = "Downloaded List"
    }
}
