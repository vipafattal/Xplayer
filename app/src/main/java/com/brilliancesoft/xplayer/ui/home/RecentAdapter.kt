package com.brilliancesoft.xplayer.ui.home;

import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.ReciterMediaIcon
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_media.*
import kotlinx.android.synthetic.main.item_recent_media.view.*

/**
 * Created by  on
 */
class RecentAdapter(dataList: List<Media>) :
    BaseRecyclerAdapter<Media>(dataList) {

    override val layoutItemId: Int = R.layout.item_recent_media

    override fun onBindViewHolder(holder: RecyclerViewHolder<Media>, position: Int) {
        val media = dataList[position]

        holder.itemView.apply {
            recent_media_reciter_name.text = media.reciterName
            recent_media_surah_name.text = media.surahName

            val reciterMediaIcon = ReciterMediaIcon.getIcon(media)
            recent_media_card_avater.strokeColor = reciterMediaIcon.color
            recent_media_avater.setColorFilter(reciterMediaIcon.color)
            recent_media_avater.setImageResource(reciterMediaIcon.icon)

            onClick { (context as MainActivity).play(media) }
        }


    }


}