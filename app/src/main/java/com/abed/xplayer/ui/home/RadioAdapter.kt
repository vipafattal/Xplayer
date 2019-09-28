package com.abed.xplayer.ui.home;

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abed.xplayer.R
import com.abed.xplayer.model.Media
import com.abed.xplayer.model.Radio
import com.abed.xplayer.ui.MainActivity
import com.abed.xplayer.ui.sharedComponent.RecyclerViewHolder
import com.codebox.lib.android.views.listeners.onClick
import com.codebox.lib.android.widgets.shortToast
import kotlinx.android.synthetic.main.item_radio.view.*

/**
 * Created by  on
 */
class RadioAdapter(private var dataList: List<Radio>) :
    RecyclerView.Adapter<RecyclerViewHolder<Radio>>() {


    fun updateData(newDataList: List<Radio>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Radio> =
        RecyclerViewHolder(parent,R.layout.item_radio)

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerViewHolder<Radio>, position: Int) {
        val data = dataList[position]

        holder.itemView.apply {
            radioName.text = data.name.replace('-',' ').trim()
            listenRadioButton.onClick {
                val activity = context as MainActivity
                activity.play(Media(data))
            }
        }


    }


}