package com.brilliancesoft.xplayer.ui.user_activity.history

import androidx.fragment.app.Fragment
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.media.BaseMediaFragment
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : BaseMediaFragment(true){

    private val historyViewModel by viewModel<HistoryViewModel>()

    override val playlist: Playlist = Playlist(id = HISTORY_MEDIA_LIST_ID)
    override val layoutId: Int = R.layout.fragment_history

    override fun onActivityCreated(){

        mediaHistoryRecycler.adapter = mediaAdapter

        historyViewModel.getAllMediaWithHistory().observer(viewLifecycleOwner) {
            mediaAdapter.updateList(it)
        }
    }

    companion object {
        private const val HISTORY_MEDIA_LIST_ID = "history playlist"
    }
}
