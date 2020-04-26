package com.brilliancesoft.xplayer.ui.commen.media.recycler

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.abed.magentaX.android.viewGroup.inflater
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.listeners.onClicks
import com.abed.magentaX.android.views.visible
import com.abed.magentaX.standard.date.stringDateWithMin
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.BaseModel
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.ReciterMediaIcon
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_media.*
import java.text.SimpleDateFormat
import java.util.*


private val currentDate = Date()

private fun formatDate(date: Date): String {
    stringDateWithMin()
    val pattern = "yyyy/M/dd-hh:mm"

    val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
    return formatter.format(date)
}

private fun isSameYear(playingDate: Date): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = currentDate
    cal2.time = playingDate

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}


abstract class BaseMediaViewHolder(
    parent: ViewGroup,
    layoutId: Int,
    protected val mediaClickListener: RecyclerPressListener<Media>
) :
    RecyclerView.ViewHolder(parent.inflater(layoutId)), LayoutContainer {

    open fun onBind(baseModel: BaseModel) {}

    open fun onBindMedia(media: Media, showPlayingDate: Boolean) {
        val reciterMediaIcon = ReciterMediaIcon.getIcon(media)
        media_card_avater.strokeColor = reciterMediaIcon.color
        media_avater.setColorFilter(reciterMediaIcon.color)
        media_avater.setImageResource(reciterMediaIcon.icon)

        media_reciter_name.text = media.reciterName
        media_surah_name.text = media.surahName

        onClicks(itemView,media_play) { (itemView.context as MainActivity).play(media) }

        if (media.isDownloaded)
            media_download.setImageResource(R.drawable.ic_downloaded)
        if (showPlayingDate) {
            playingMediaDataText.visible()
            val mediaPlayDate = media.getMediaPlayDate()!!
            playingMediaDataText.text = formatDate(mediaPlayDate)
        }
    }

    protected fun View.onClicked(media: Media) {
        onClick {
            mediaClickListener.onItemClick(media, id)
        }
    }

    protected fun createPopup(view: View): PopupMenu {
        val popup =
            PopupMenu(
                view.context,
                view,
                Gravity.CENTER,
                android.R.attr.popupMenuStyle,
                R.style.popupMenu
            )

        popup.inflate(R.menu.popup_download_actions)

        return popup
    }

    override val containerView: View = itemView
}