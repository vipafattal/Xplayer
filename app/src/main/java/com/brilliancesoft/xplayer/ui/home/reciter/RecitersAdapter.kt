package com.brilliancesoft.xplayer.ui.home.reciter

import android.content.Context
import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import com.brilliancesoft.xplayer.ui.home.HomeFragmentDirections
import kotlinx.android.synthetic.main.item_reciter.view.*
import kotlinx.serialization.json.Json

/**
 * Created by ${User} on ${Date}
 */
class RecitersAdapter(
    dataList: List<Reciter>,
    private val isShowingFromHomeFragment: Boolean
) : BaseRecyclerAdapter<Reciter>(dataList) {

    override val layoutItemId: Int = R.layout.item_reciter

    override fun onBindViewHolder(holder: RecyclerViewHolder<Reciter>, position: Int) {
        val reciter = dataList[position].run { copy(name = name.replace("\r\n", "")) }

        holder.itemView.apply {
            reciterName.text = reciter.name
            reciterRewaya.text = reciter.rewaya
            onClick { context.openReciterTruckList(reciter) }
        }
    }

    private fun Context.openReciterTruckList(reciter: Reciter) {
        val serializedReciter = Json.stringify(Reciter.serializer(), reciter)
        val navController =(this as MainActivity).navController
        if (isShowingFromHomeFragment)
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToTruckListFragment(
                    serializedReciter
                )
            )
        else
            navController.navigate(
                ReciterMoreFragmentDirections.actionReciterMoreFragmentToTruckListFragment(
                    serializedReciter
                )
            )
    }

}