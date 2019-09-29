package com.abed.xplayer.ui.playList


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.abed.xplayer.R
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.model.Media
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.XplayerApplication
import com.abed.xplayer.ui.sharedComponent.controllers.BaseFragment
import com.abed.xplayer.ui.sharedComponent.recyclerAdapters.MediaListAdapter
import com.abed.xplayer.ui.sharedComponent.widgets.XplayerToast
import com.abed.xplayer.utils.observer
import kotlinx.android.synthetic.main.fragment_media_playlist.*
import kotlinx.android.synthetic.main.toolbar_details.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class MediaPlaylistFragment : BaseFragment(), ItemPressListener<Media> {

    companion object {
        const val TAG: String = "MediaPlaylistFragment"

        fun getInstance(playlist: Playlist): MediaPlaylistFragment {
            val mediaPlaylistFragment = MediaPlaylistFragment()
            mediaPlaylistFragment.playlist = playlist
            mediaPlaylistFragment.mediaList.addAll(playlist.list)
            return mediaPlaylistFragment
        }
    }


    private val firebaseRepository: FirebaseRepository by inject()
    private lateinit var mediaListAdapter: MediaListAdapter
    private lateinit var playlist: Playlist

    private val mediaList = mutableListOf<Media>()


    override val layoutId: Int = R.layout.fragment_media_playlist

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        detailsToolbar.title = playlist.name
        detailsToolbar.inflateMenu(R.menu.menu_media_list)

        detailsToolbar.menu.findItem(R.id.deletePlayList).setOnMenuItemClickListener {

            firebaseRepository.deletePlaylist(playlist.id).observer(viewLifecycleOwner) {
                if (it != null)
                    if (it) fragmentManager?.beginTransaction()!!.remove(this)
                    else XplayerToast.makeShort(context!!, getString(R.string.failed))
            }
            true
        }

        fragmentManager?.let {
            mediaListAdapter =
                MediaListAdapter(
                    mediaList,
                    this
                )
            mediaPlaylistRecycler.adapter = mediaListAdapter

        }
    }

    override fun onItemClick(data: Media) {
        firebaseRepository.updateMediaInPlaylist(data, playlist.id, isAdd = false)
            .observer(viewLifecycleOwner) {
                if (it != null) {
                    val index = mediaList.indexOf(data)
                    mediaList.removeAt(index)
                    mediaListAdapter.removeItemAtIndex(index)

                    if (mediaList.isEmpty()) {
                        fragmentManager?.beginTransaction()!!.remove(this)
                    }
                }
            }
    }
}


