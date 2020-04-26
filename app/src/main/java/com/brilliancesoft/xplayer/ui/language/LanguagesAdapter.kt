package com.brilliancesoft.xplayer.ui.language

import android.view.View
import android.widget.ImageView
import com.abed.magentaX.android.views.listeners.onClick
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Language
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.BaseRecyclerAdapter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerViewHolder
import kotlinx.android.synthetic.main.item_language.view.*

/**
 * Created by  on
 */
class LanguagesAdapter(
    dataList: List<Language>,
    private val recyclerPressListener: RecyclerPressListener<Language>
) : BaseRecyclerAdapter<Language>(dataList) {

    private var selectedViewImage: ImageView? = null
    override val layoutItemId: Int = R.layout.item_language

    override fun onBindViewHolder(holder: RecyclerViewHolder<Language>, position: Int) {
        val language = dataList[position]

        holder.itemView.apply {
            languageName.text = language.language.removePrefix("_").capitalize()

            onClick {
                updatedSelectedView()
                recyclerPressListener.onItemClick(language,-1)
            }

        }
    }

    private fun View.updatedSelectedView() {
        selectedViewImage?.setImageDrawable(null)
        selectedViewImage = selectedLanguageImage
        selectedLanguageImage.setImageResource(R.drawable.ic_check_circle)
        selectedLanguageImage.setColorFilter(R.color.colorAccent)
    }

}