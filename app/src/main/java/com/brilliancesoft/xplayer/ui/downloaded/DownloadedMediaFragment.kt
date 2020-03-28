package com.brilliancesoft.xplayer.ui.downloaded


import android.os.Bundle
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.utils.DownloadMediaUtils
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
import com.brilliancesoft.xplayer.ui.commen.recyclerAdapters.MediaListAdapter
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.android.synthetic.main.fragment_downloaded.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DownloadedMediaFragment : BaseFragment(),
    ItemPressListener<Media> {


    private val downloadedMediaViewModel: DownloadedViewModel by viewModel()
    private lateinit var mediaListAdapter: MediaListAdapter

    override val layoutId: Int = R.layout.fragment_downloaded

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()
    }

    override fun onResume() {
        super.onResume()
        if (::mediaListAdapter.isInitialized)
            downloadedMediaViewModel.loadData()
    }

    override fun onItemClick(data: Media) {

        (context as BaseActivity).executeWithPendingPermission(
            BaseActivity.STORAGE_PERMISSION,
            BaseActivity.STORAGE_REQUEST_CODE
        ) {
            val isRemoved = DownloadMediaUtils.deleteDownloadedFile(data)
            if (isRemoved) {
                val index = downloadedMediaViewModel.removeMediaAndGetIndex(data)
                mediaListAdapter.removeItemAtIndex(index)
            }
        }
    }

    override fun loadData() {
        downloadedMediaViewModel.getDownloadedMedia().observer(viewLifecycleOwner) {
            dispatchDataToAdapter(it)
        }
    }

    private fun dispatchDataToAdapter(mediaList: List<Media>) {
        if (mediaList.isEmpty())
            showNoDataView(R.string.no_media_downloaded)
        else {
            loadingCompleted()

            mediaListAdapter = MediaListAdapter(Playlist(list = mediaList), this)
            downloadedMediaRecycler.adapter = mediaListAdapter
        }

    }

}
