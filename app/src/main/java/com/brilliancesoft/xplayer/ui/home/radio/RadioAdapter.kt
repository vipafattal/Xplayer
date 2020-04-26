package com.brilliancesoft.xplayer.ui.home.radio;

import android.widget.TextView
import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Radio
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import com.google.android.material.textview.MaterialTextView

/**
 * Created by  on
 */
class RadioAdapter(dataList: List<Radio>,  isShowingFromFromHomeFragment:Boolean) : BaseRecyclerAdapter<Radio>(dataList) {

    override val layoutItemId: Int =if (isShowingFromFromHomeFragment) R.layout.item_radio_home else R.layout.item_radio

    override fun onBindViewHolder(holder: RecyclerViewHolder<Radio>, position: Int) {
        val radio = dataList[position]
        val radioNameTextView =   holder.itemView.findViewById<TextView>(R.id.radioNameTextView)
        val listenRadioButton =   holder.itemView.findViewById<MaterialTextView>(R.id.listenRadioButton)
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