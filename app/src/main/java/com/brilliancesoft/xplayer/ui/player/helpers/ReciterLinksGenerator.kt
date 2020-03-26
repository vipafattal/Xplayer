package com.brilliancesoft.xplayer.ui.player.helpers

/**
 * Created by  on
 */
object ReciterLinksGenerator {

    fun getLink(reciterLink:String, surahNumber: Int) :String = "$reciterLink/$surahNumber.mp3"
}