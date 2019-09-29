package com.abed.xplayer.ui.playList.playlistDialog

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.abed.xplayer.R
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.model.Media
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.XplayerApplication
import com.abed.xplayer.ui.sharedComponent.controllers.BaseBottomSheetDialog
import com.abed.xplayer.ui.sharedComponent.widgets.XplayerToast
import com.abed.xplayer.utils.observer
import com.codebox.lib.android.views.listeners.onClick
import com.codebox.lib.android.views.utils.gone
import com.codebox.lib.android.views.utils.visible
import kotlinx.android.synthetic.main.bottomsheet_select_playlist.*
import org.koin.android.ext.android.inject

/**
 * Created by  on
 */
class SelectPlaylistDialog : BaseBottomSheetDialog(), ItemPressListener<Playlist> {

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

        firebaseRepository.getUserPlayLists().observer(viewLifecycleOwner) {
            playListSelectionRecycler.adapter = SelectPlayListAdapter(it, this)
            playListLoading.gone()
        }


        newPlayListButton.onClick {
            dismiss()
            fragmentManager?.let {
                PlayListNameDialog.getInstance(media).show(it, PlayListNameDialog.TAG)
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