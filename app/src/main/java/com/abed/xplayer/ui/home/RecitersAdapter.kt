package com.abed.xplayer.ui.home

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.abed.xplayer.R
import com.abed.xplayer.model.Reciter
import com.abed.xplayer.ui.recitersList.TruckListFragment
import com.abed.xplayer.ui.sharedComponent.RecyclerViewHolder
import com.codebox.lib.android.fragments.transaction
import com.codebox.lib.android.views.listeners.onClick
import kotlinx.android.synthetic.main.item_reciter.view.*

/**
 * Created by ${User} on ${Date}
 */
class RecitersAdapter(
    private var dataList: List<Reciter>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<RecyclerViewHolder<Reciter>>() {

    fun updateData(newDataList: List<Reciter>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Reciter> =
        RecyclerViewHolder(parent, R.layout.item_reciter)

    override fun onBindViewHolder(holder: RecyclerViewHolder<Reciter>, position: Int) {
        val data = dataList[position]

        holder.itemView.apply {
            reciterName.text = data.name

            onClick {
                fragmentManager.transaction {
                    replace(
                        R.id.fragmentHost,
                        TruckListFragment.getInstance(data, "_arabic"),
                        TruckListFragment.TAG
                    )
                    addToBackStack(null)
                }
            }
        }

    }

}