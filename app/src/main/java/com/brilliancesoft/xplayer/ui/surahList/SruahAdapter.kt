package com.brilliancesoft.xplayer.ui.surahList

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.PopupMenu
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.listeners.onClicks
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.model.Sura
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import com.brilliancesoft.xplayer.ui.player.MediaViewDownloadHelper
import kotlinx.android.synthetic.main.item_truck.*

class SruahAdapter(
    dataList: List<Sura>,
    private val reciter: Reciter,
    private val downloadedMediaList: List<Media>,
    private val recyclerPressListener: RecyclerPressListener<Media>
) : BaseRecyclerAdapter<Sura>(dataList) {

    override val layoutItemId: Int = R.layout.item_truck

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Sura> {
        return ViewHolder(parent, layoutItemId)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<Sura>, position: Int) {
        val sura = dataList[position].run { copy(name = name.replace("\r\n", "")) }
        holder.onBind(sura)
    }


    inner class ViewHolder(parent: ViewGroup, @LayoutRes layoutID: Int) :
        RecyclerViewHolder<Sura>(parent, layoutID) {

        override fun onBind(sura: Sura) {
            val media =
                downloadedMediaList.firstOrNull { it.surahName == sura.name } ?: createMedia(sura)
            surah_name.text = sura.name

            MediaViewDownloadHelper(
                media = media,
                downloadProgressBar = surah_download_loading,
                downloadImage = surah_download_image,
                recyclerPressListener = recyclerPressListener
            ).prepare()

            surah_more_option.onClick {
                showPopup()
            }
            surah_add_to_playlist.onClick {
                recyclerPressListener.onItemClick(media,R.id.surah_add_to_playlist)
            }
            onClicks(surah_play, itemView) {
                playMedia(media)
            }

        }

        /**
        Used to create [Media] if only the download media is not contained in [downloadedMediaList].
         **/
        private fun createMedia(sura: Sura): Media {
            return Media.create(sura, reciter, false)
        }

        private fun playMedia(media: Media) {
            (viewContext as MainActivity).play(media)
        }

        private fun playAllReciterList() {
            val playlist = Playlist(
                name = SurahListFragment.RECITER_SURAHS_PLAYLIST,
                list = dataList.map { surah ->
                    downloadedMediaList.firstOrNull { it.surahName == surah.name } ?: Media.create(
                        surah,
                        reciter,
                        false
                    )
                })
            (viewContext as MainActivity).playMediaList(playlist, adapterPosition)
        }


        private fun View.showPopup() {
            val popup = PopupMenu(
                context,
                this,
                Gravity.END,
                android.R.attr.popupMenuStyle,
                R.style.popupMenu
            )
            popup.inflate(R.menu.popup_surah)
            popup.show()
            popup.setPopupClickListener()
        }

        private fun PopupMenu.setPopupClickListener() {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.play_list_from_surah -> playAllReciterList()
                }
                true
            }
        }
    }


}