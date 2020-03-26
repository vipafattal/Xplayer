package com.brilliancesoft.xplayer.ui.home

import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_reciter.view.*

/**
 * Created by ${User} on ${Date}
 */
class RecitersAdapter(
    dataList: List<Reciter>,
    private val itemPressListener: ItemPressListener<Reciter>
) : BaseRecyclerAdapter<Reciter>(dataList) {

    override val layoutItemId: Int = R.layout.item_reciter

    override fun onBindViewHolder(holder: RecyclerViewHolder<Reciter>, position: Int) {
        val data = dataList[position]

        holder.itemView.apply {
            reciterName.text = data.name
            onClick { itemPressListener.onItemClick(data) }
        }

    }

}