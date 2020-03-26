package com.brilliancesoft.xplayer.ui.player

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.abed.magentaX.android.resoures.drawableOf
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class NotificationDescriptionAdapter(private val playList: List<Media>) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): String {
        val window = player.currentWindowIndex
        return playList[window].title
    }


    override fun getCurrentContentText(player: Player): String? {
        val window = player.currentWindowIndex
        return playList[window].subtitle
    }

    private val icon = drawableOf(R.drawable.ic_logo_24dp)!!
    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        /* val window = player.currentWindowIndex
         val largeIcon = getLargeIcon(window)
         if (largeIcon == null && getLargeIconUri(window) != null) {
             // load bitmap async
             loadBitmap(getLargeIconUri(window), callback)
             return getPlaceholderBitmap()
         }*/
        return icon.toBitmap()
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val notificationIntent = Intent(XplayerApplication.appContext, MainActivity::class.java)

        return PendingIntent.getActivity(XplayerApplication.appContext, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT)
    }


}