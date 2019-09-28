package com.abed.xplayer.ui.language

import android.widget.ImageView
import com.abed.xplayer.R
import com.abed.xplayer.model.Language
import com.abed.xplayer.ui.sharedComponent.BaseRecyclerAdapter
import com.abed.xplayer.ui.sharedComponent.ItemPressListener
import com.abed.xplayer.ui.sharedComponent.RecyclerViewHolder
import com.codebox.lib.android.resoures.Colour
import com.codebox.lib.android.views.listeners.onClick
import kotlinx.android.synthetic.main.item_language.view.*

/**
 * Created by  on
 */
class LanguagesAdpater(
    dataList: List<Language>,
    private val itemPressListener: ItemPressListener<Language>
) : BaseRecyclerAdapter<Language>(dataList) {

    private var selectedViewImage: ImageView? = null
    override val layoutItemId: Int = R.layout.item_language

    override fun onBindViewHolder(holder: RecyclerViewHolder<Language>, position: Int) {
        val language = dataList[position]

        holder.itemView.apply {
            languageName.text = language.language

            onClick {
                selectedViewImage?.setColorFilter(Colour(R.color.colorGrayItem))
                selectedViewImage = selectedLanguageImage
                itemPressListener.onItemClick(language)

            }

        }
    }
}