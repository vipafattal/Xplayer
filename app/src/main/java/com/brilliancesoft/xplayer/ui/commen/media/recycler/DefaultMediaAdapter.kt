package com.brilliancesoft.xplayer.ui.commen.media.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.BaseModel
import com.brilliancesoft.xplayer.model.DownloadMedia
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.user_activity.downloaded.recycler.DownloadDiffCallback
import com.brilliancesoft.xplayer.ui.user_activity.downloaded.recycler.DownloadViewHolder

open class DefaultMediaAdapter(
    private val showPlayingDate: Boolean,
    private val mediaClickListener: RecyclerPressListener<Media>
) :
    RecyclerView.Adapter<BaseMediaViewHolder>() {

    private val currentList = mutableListOf<BaseModel>()
    var isAddedToPlaylist = false

    fun getList(): List<BaseModel> = currentList
    override fun getItemCount() = currentList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseMediaViewHolder {
        return if (currentList[0] is Media) MediaViewHolder(
            parent,
            R.layout.item_media,
            showPlayingDate,
            isAddedToPlaylist,
            mediaClickListener
        )
        else DownloadViewHolder(
            parent,
            R.layout.item_media,
            showPlayingDate,
            mediaClickListener
        )
    }

    override fun onBindViewHolder(holderDownloading: BaseMediaViewHolder, position: Int) {
        val dataItem = currentList[position]
        holderDownloading.onBind(dataItem)
    }

    @Suppress("UNCHECKED_CAST")
    fun updateList(newList: List<BaseModel>, changedIndex:Int = -1) {
        if (newList.isNotEmpty() && newList[0] is DownloadMedia) {
            val diffCallback =
                DownloadDiffCallback(
                    currentList.toList() as List<DownloadMedia>,
                    newList as List<DownloadMedia>
                )
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            currentList.clear()
            currentList.addAll(newList)
            diffResult.dispatchUpdatesTo(this)
        } else {
            if (changedIndex==-1) {
                currentList.clear()
                currentList.addAll(newList)
                notifyDataSetChanged()
            } else{
                currentList.clear()
                currentList.addAll(newList)
                notifyItemRemoved(changedIndex)
                notifyItemRangeRemoved(changedIndex, currentList.size)
            }
        }
    }

}