package com.brilliancesoft.xplayer.ui.surahList

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import com.abed.magentaX.android.views.invisible
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.framework.utils.DownloadMediaUtils
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.model.Sura
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.ui.playlist.playlistDialog.SelectPlaylistDialog
import kotlinx.android.synthetic.main.item_truck.view.*

/**
 * Created by Abed.
 */
class TruckAdapter(
    dataList: List<Sura>,
    private val reciter: Reciter,
    private val fragmentManager: FragmentManager
) : BaseRecyclerAdapter<Sura>(dataList) {


    override val layoutItemId: Int = R.layout.item_truck

    override fun onBindViewHolder(holder: RecyclerViewHolder<Sura>, position: Int) {
        val sura = dataList[position].run { copy(name = name.replace("\r\n", "")) }

        holder.itemView.apply {
            surahName.text = sura.name

            val media = Media.create(sura, reciter)

            val isDownloaded = DownloadMediaUtils.isDownloaded(media)
            if (isDownloaded) mediaDownloadImage.visible()
            else mediaDownloadImage.invisible()

            reciterSurahButton.onClick {
                showPopup(reciterSurahButton, sura, context)
            }
            onClick { playMedia(sura, context) }
        }
    }

    private fun playMedia(sura: Sura, context: Context) {
        val media = Media.create(sura, reciter)
        (context as MainActivity).play(media)
    }


    private fun showPopup(v: View, sura: Sura, context: Context) {
        val popup =
            PopupMenu(v.context, v, Gravity.END, android.R.attr.popupMenuStyle, R.style.popupMenu)
        popup.inflate(R.menu.popup_truck)

        val media = Media.create(sura, reciter)

        val isDownloaded = DownloadMediaUtils.isDownloaded(media)
        popup.menu.findItem(R.id.download_media).isVisible = !isDownloaded

        popup.show()
        popup.setPopupClickListener(sura, context)
    }

    private fun PopupMenu.setPopupClickListener(
        sura: Sura,
        context: Context
    ) {
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.play_media -> playMedia(sura, context)
                R.id.add_to_playlist -> addToPlaylist(sura)
                R.id.download_media -> {
                    val media = Media.create(sura, reciter)
                    DownloadMediaUtils.download(media, context as BaseActivity)
                }
            }
            false
        }
    }

    private fun addToPlaylist(sura: Sura) {
        val media = Media.create(sura, reciter)
        val playlistDialog = SelectPlaylistDialog.getInstance(media)
        playlistDialog.show(fragmentManager, SelectPlaylistDialog.TAG)
    }

}