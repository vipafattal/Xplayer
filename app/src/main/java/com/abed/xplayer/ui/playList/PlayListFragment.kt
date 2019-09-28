package com.abed.xplayer.ui.playList


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.abed.xplayer.R
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.ViewModelFactory
import com.abed.xplayer.ui.sharedComponent.XplayerApplication
import com.abed.xplayer.ui.sharedComponent.controllers.BaseFragment
import com.abed.xplayer.utils.observer
import com.abed.xplayer.utils.viewModelOf
import com.codebox.lib.android.fragments.transaction
import com.codebox.lib.android.views.utils.visible
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.layout_empty_data_text.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class PlayListFragment : BaseFragment(), ItemPressListener<Playlist> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    init {
        XplayerApplication.appComponent.inject(this)
    }

    override val layoutId: Int = R.layout.fragment_playlist

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = viewModelOf<PlaylistViewModel>(viewModelFactory)

        viewModel.getPlayLists().observer(viewLifecycleOwner) {
            if (it.isNotEmpty())
                playlistRecycler.adapter = PlaylistAdapter(it, this)
            else {
                emptyDataText.visible()
                emptyDataText.text = getString(R.string.no_saved_playlist)
            }
        }
    }

    override fun onItemClick(data: Playlist) {
        fragmentManager?.transaction {
            val mediaPlaylistFragment = MediaPlaylistFragment.getInstance(data)
            replace(R.id.fragmentHost, mediaPlaylistFragment, MediaPlaylistFragment.TAG)
            addToBackStack(null)
        }
    }
}
