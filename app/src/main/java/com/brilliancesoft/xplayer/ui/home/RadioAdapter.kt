package com.brilliancesoft.xplayer.ui.home;

import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Radio
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_radio.view.*

/**
 * Created by  on
 */
class RadioAdapter(dataList: List<Radio>) :
    BaseRecyclerAdapter<Radio>(dataList) {

    override val layoutItemId: Int = R.layout.item_radio

    override fun onBindViewHolder(holder: RecyclerViewHolder<Radio>, position: Int) {
        val radio = dataList[position]

        holder.itemView.apply {

            val radioName = radio.name.replace('-', ' ').trim()
            radioNameTextView.text = radioName

            listenRadioButton.onClick {
                val activity = context as MainActivity
                activity.play(Media.create(radioName, radio))
            }
        }


    }


}