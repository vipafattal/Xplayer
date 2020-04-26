package com.brilliancesoft.xplayer.ui.user_activity.downloaded.recycler

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.brilliancesoft.xplayer.model.DownloadMedia

class DownloadDiffCallback(
    private val oldList: List<DownloadMedia>,
    private val newList: List<DownloadMedia>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].download.request.id == newList[newItemPosition].download.request.id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].download
        val newItem = newList[newItemPosition].download
        return oldItem.state == newItem.state && oldItem.percentDownloaded.toInt() == newItem.percentDownloaded.toInt()
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}