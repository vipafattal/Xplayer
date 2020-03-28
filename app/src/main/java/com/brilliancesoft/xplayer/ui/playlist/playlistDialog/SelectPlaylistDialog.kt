package com.brilliancesoft.xplayer.ui.playlist.playlistDialog

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseBottomSheetDialog
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.android.synthetic.main.bottomsheet_select_playlist.*
import org.koin.android.ext.android.inject

/**
 * Created by  on
 */
class SelectPlaylistDialog : BaseBottomSheetDialog(),
    ItemPressListener<Playlist> {

    private val firebaseRepository: FirebaseRepository by inject()

    private lateinit var media: Media

    private var isLoadingPlaylistCompleted: LiveData<Boolean?>? = null


    companion object {

        val TAG = SelectPlaylistDialog::class.simpleName
        fun getInstance(media: Media): SelectPlaylistDialog {
            val thisDialog = SelectPlaylistDialog()
            thisDialog.media = media

            return thisDialog
        }
    }


    override val layoutId: Int = R.layout.bottomsheet_select_playlist

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(firebaseRepository.currentUser != null) {
            firebaseRepository.getUserPlayLists().observer(viewLifecycleOwner) {
                playListSelectionRecycler.adapter = SelectPlaylistAdapter(it, this)
                playListLoading.gone()
            }
        }


        newPlayListButton.onClick {
            dismiss()
            parentFragmentManager.let {
                PlaylistNameDialog.getInstance(media).show(it, PlaylistNameDialog.TAG)
            }
        }

    }

    override fun onItemClick(data: Playlist) {
        isLoadingPlaylistCompleted =
            firebaseRepository.updateMediaInPlaylist(media, data.id, true)
        isLoadingPlaylistCompleted!!.observer(viewLifecycleOwner) {
            when (it) {
                null -> playListLoading.visible()
                true -> {
                    XplayerToast.makeShort(requireContext(), R.string.saved)
                    dismiss()
                }
                else -> {
                    XplayerToast.makeShort(requireContext(), R.string.failed)
                    playListLoading.gone()
                    isLoadingPlaylistCompleted = null
                }

            }
        }
    }
}