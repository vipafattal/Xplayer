package com.brilliancesoft.xplayer.ui.user_activity.playlist.playlist_dialog

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseDialog
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.utils.observer
import com.brilliancesoft.xplayer.utils.viewExtensions.isNotNullOrEmpty
import kotlinx.android.synthetic.main.dialog_playlist_name.*
import org.koin.android.ext.android.inject
import java.util.*

/**
 * Created by  on
 */

class PlaylistNameDialog : BaseDialog() {
    companion object {
        const val TAG = "PlayListNameDialog"
        fun getInstance(media: Media, isUpdate: Boolean = false): PlaylistNameDialog {
            val playListNameDialog =
                PlaylistNameDialog()
            playListNameDialog.media = media
            playListNameDialog.isUpdate = isUpdate
            return playListNameDialog
        }
    }


    private lateinit var media: Media
    private var isSavingProgressCompleted: LiveData<Boolean?>? = null
    private var isUpdate: Boolean = false
    private val firebaseRepository:FirebaseRepository by inject()

    override val layoutId: Int = R.layout.dialog_playlist_name

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cancelPlayListButton.onClick {
            dismiss()
        }
        savePlayListButton.setOnClickListener {
            val playListNameText = playlistNameInput.text
            if (playListNameText.isNotNullOrEmpty()) {
                createPlaylist(playListNameText!!.toString())
            }
        }
    }

    private fun createPlaylist(listName: String) {
        if (isSavingProgressCompleted == null) {
            val playlistId = UUID.randomUUID().toString()

            val playList = Playlist(
                listName,
                firebaseRepository.currentUser!!.uid,
                playlistId,
                listOf(media)
            )

            isSavingProgressCompleted = firebaseRepository.savePlayList(playList)

            isSavingProgressCompleted!!.observer(viewLifecycleOwner) {
                when (it) {
                    null -> {
                        creatingListProgress.visible()
                        savePlayListButton.text = ""
                    }
                    true -> {
                        isSavingProgressCompleted = null
                        XplayerToast.makeShort(requireContext(),getString(R.string.saved))
                        dismiss()
                    }
                    false -> {
                        isSavingProgressCompleted = null
                        XplayerToast.makeLong(requireContext(), getString(R.string.failed_creating_list))
                    }
                }

            }
        }
    }


}