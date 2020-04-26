package com.brilliancesoft.xplayer.model

import androidx.annotation.DrawableRes
import com.abed.magentaX.android.utils.colors.A700Colors
import com.brilliancesoft.xplayer.R

data class ReciterMediaIcon(val id: String, @DrawableRes val icon: Int, val color: Int) {

    companion object {
        private val a700Colors = A700Colors()

        private val iconsAvater = intArrayOf(
            R.drawable.ic_media_avater1,
            R.drawable.ic_media_avater2,
            R.drawable.ic_media_avater3,
            R.drawable.ic_media_avater4,
            R.drawable.ic_media_avater5
        )

        private var previousReciterIcon =
            ReciterMediaIcon("", R.drawable.reciter_icon, R.color.red_A700)
        private var currentColorIndex = -1
        private var currentIconIndex = -1

        private val reciterMediaIcons = mutableListOf<ReciterMediaIcon>()
        fun getIcon(media: Media): ReciterMediaIcon {
            var reciterIcon: ReciterMediaIcon?

            if (previousReciterIcon.id == media.reciterId)
                reciterIcon = previousReciterIcon
            else
                reciterIcon = reciterMediaIcons.firstOrNull { it.id == media.reciterId }

            if (reciterIcon == null) {

                currentColorIndex++
                currentIconIndex++
                if (currentColorIndex > a700Colors.lastIndex) currentColorIndex = 0
                if (currentIconIndex > iconsAvater.lastIndex) currentIconIndex = 0

                reciterIcon = ReciterMediaIcon(
                        media.reciterId,
                        iconsAvater[currentIconIndex],
                        a700Colors[currentColorIndex]
                    )
                previousReciterIcon = reciterIcon
                reciterMediaIcons.add(reciterIcon)
            }

            return reciterIcon
        }
    }

}
