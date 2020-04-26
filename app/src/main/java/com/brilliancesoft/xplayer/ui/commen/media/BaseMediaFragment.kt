package com.brilliancesoft.xplayer.ui.commen.media

import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.media.recycler.DefaultMediaAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.user_activity.downloaded.DownloadedViewModel
import com.brilliancesoft.xplayer.ui.user_activity.playlist.playlist_dialog.SelectPlaylistDialog
import com.brilliancesoft.xplayer.utils.observer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseMediaFragment(showPlayingDate: Boolean = false) : BaseFragment() {

    abstract val playlist: Playlist

    val mediaAdapter = DefaultMediaAdapter(showPlayingDate, getMediaClickListener())
    val downloadedMediaViewModel: DownloadedViewModel by viewModel()

    private val firebaseRepository: FirebaseRepository by inject()


    protected fun getMediaClickListener(): RecyclerPressListener<Media> =
        object : RecyclerPressListener<Media> {
            override fun onItemClick(data: Media, clickedViewId: Int) {
                when (clickedViewId) {
                    R.id.remove_download -> downloadedMediaViewModel.deleteMedia(data)

                    R.id.media_add_to_playlist -> SelectPlaylistDialog.show(
                        data,
                        parentFragmentManager
                    )

                    R.id.remove_from_playlist -> {
                        if (playlist.list.contains(data))
                            removeFromPlaylist(data, mediaAdapter)
                    }

                    R.id.media_play -> {
                        val mediaList = mediaAdapter.getList() as List<Media>
                        val playIndex = mediaList.indexOf(data)
                        if (playIndex == -1)
                            (context as MainActivity).play(data)
                        else
                            (context as MainActivity).playMediaList(
                                Playlist(
                                    name = playlist.name,
                                    id = playlist.id,
                                    userId = playlist.userId,
                                    list = mediaList
                                )
                            )
                    }
                }
            }
        }

    protected fun deletePlaylist() {
        playlist.Action(firebaseRepository).delete().observer(viewLifecycleOwner) {
            if (it != null)
                if (it) parentFragmentManager.beginTransaction().remove(this)
                else XplayerToast.makeShort(requireContext(), getString(R.string.failed))
        }
    }

    protected fun removeFromPlaylist(media: Media, mediaAdapter: DefaultMediaAdapter) {
        playlist.Action(firebaseRepository).removeMedia(media)
            .observer(viewLifecycleOwner) { isSuccessful ->
                if (isSuccessful != null) {
                    if (isSuccessful) {
                        val mediaList = mediaAdapter.getList().toMutableList()
                        val index = mediaList.indexOf(media)
                        if (index != -1) {
                            mediaList.removeAt(index)
                            mediaAdapter.updateList(mediaList, index)
                        }

                        val playerService = (context as MainActivity).getPlayerService()
                        if (playerService!!.getCurrentPlaylist().id == playlist.id)
                            playerService.removeFromPlayingMedia(media)

                        if (mediaList.isEmpty())
                            parentFragmentManager.beginTransaction().remove(this)
                    } else
                        XplayerToast.makeShort(requireContext(), R.string.failed_updating_playlist)
                }
            }
    }

}