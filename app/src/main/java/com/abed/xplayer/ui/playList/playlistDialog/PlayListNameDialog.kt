package com.abed.xplayer.ui.playList.playlistDialog

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.abed.xplayer.R
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.model.Media
import com.abed.xplayer.model.Playlist
import com.abed.xplayer.ui.sharedComponent.XplayerApplication
import com.abed.xplayer.ui.sharedComponent.controllers.BaseDialog
import com.abed.xplayer.ui.sharedComponent.widgets.XplayerToast
import com.abed.xplayer.utils.observer
import com.abed.xplayer.utils.viewExtensions.isNotNullOrEmpty
import com.codebox.lib.android.views.listeners.onClick
import com.codebox.lib.android.views.utils.visible
import kotlinx.android.synthetic.main.dialog_playlist_name.*
import java.util.*
import javax.inject.Inject

/**
 * Created by  on
 */

class PlayListNameDialog : BaseDialog() {
    companion object {
        const val TAG = "PlayListNameDialog"
        fun getInstance(media: Media, isUpdate: Boolean = false): PlayListNameDialog {
            val playListNameDialog =
                PlayListNameDialog()
            playListNameDialog.media = media
            playListNameDialog.isUpdate = isUpdate
            return playListNameDialog
        }
    }

    @Inject
    lateinit var firebaseRepository: FirebaseRepository
    private lateinit var media: Media
    private var isSavingProgressCompleted: LiveData<Boolean?>? = null
    private var isUpdate: Boolean = false

    init {
        XplayerApplication.appComponent.inject(this)
    }

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
                "123",
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