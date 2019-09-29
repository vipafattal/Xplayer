package com.abed.xplayer.ui.downloaded


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.abed.xplayer.R
import com.abed.xplayer.framework.utils.DownloadMediaUtils
import com.abed.xplayer.model.Media
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.controllers.BaseActivity
import com.abed.xplayer.ui.sharedComponent.controllers.BaseFragment
import com.abed.xplayer.ui.sharedComponent.recyclerAdapters.MediaListAdapter
import com.abed.xplayer.utils.observer
import com.codebox.lib.android.views.utils.visible
import kotlinx.android.synthetic.main.fragment_downloaded.*
import kotlinx.android.synthetic.main.layout_empty_data_text.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class DownloadedMediaFragment : BaseFragment(), ItemPressListener<Media> {


    private val downloadedMediaViewModel: DownloadedViewModel by viewModel()
    private lateinit var mediaListAdapter: MediaListAdapter

    override val layoutId: Int = R.layout.fragment_downloaded

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        downloadedMediaViewModel.getDownloadedMedia().observer(viewLifecycleOwner) {
            dispatchDataToAdapter(it)
        }
    }

    override fun onResume() {
        super.onResume()
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

    private fun dispatchDataToAdapter(dataList: List<Media>) {
        if (dataList.isEmpty())
            emptyDataText.visible()
        else {
            mediaListAdapter = MediaListAdapter(dataList, this)
            downloadedMediaRecycler.adapter = mediaListAdapter
        }

    }

}
