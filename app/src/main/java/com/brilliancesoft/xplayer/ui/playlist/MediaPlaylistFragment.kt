package com.brilliancesoft.xplayer.ui.playlist


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.recyclerAdapters.MediaListAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.android.synthetic.main.fragment_media_playlist.*
import kotlinx.android.synthetic.main.toolbar_details.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class MediaPlaylistFragment : BaseFragment(),
    ItemPressListener<Media> {

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
                    if (it) parentFragmentManager.beginTransaction().remove(this)
                    else XplayerToast.makeShort(context!!, getString(R.string.failed))
            }
            true
        }

        mediaListAdapter =
            MediaListAdapter(
                playlist,
                this
            )
        mediaPlaylistRecycler.adapter = mediaListAdapter


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


