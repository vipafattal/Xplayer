package com.abed.xplayer.player

import com.abed.xplayer.model.Media
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player


/**
 * Created by ${User} on ${Date}
 */
class ExoPlayerListener(
    private val exoPlayer: ExoPlayer,
    private val currentPlayingList:List<Media>
) : Player.EventListener {
    @Suppress("DEPRECATION")

    override fun onPositionDiscontinuity(reason: Int) {
        val p = exoPlayer.currentWindowIndex
        // val aya = currentPlayedAyat?.get(p)
        /*   if (aya != null) {
               highlightCurrentPlayedAya(aya)
           }*/
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        val exoPlayerErrorTag = "exoPlayerErrorTag"
        //  CustomToast.makeShort(readQuranActivity,R.string.playing_error)

        /* when (error.type) {
             ExoPlaybackException.TYPE_SOURCE -> Crashlytics.log(
                 Priority.Heigh,
                 "TYPE_SOURCE: $exoPlayerErrorTag",
                 error.message
             )
             ExoPlaybackException.TYPE_RENDERER -> Crashlytics.log(
                 Priority.Heigh,
                 "TYPE_RENDERER: $exoPlayerErrorTag",
                 error.message
             )
             ExoPlaybackException.TYPE_UNEXPECTED -> Crashlytics.log(
                 Priority.Heigh,
                 "TYPE_UNEXPECTED: $exoPlayerErrorTag",
                 error.message
             )
         }
 */
        // terminatePlayer()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            //The player is able to immediately play from the current position. This means the player does actually play media when playWhenReady is true. If it is false the player is paused.
            Player.STATE_READY -> {
                /* val p = exoPlayer.currentWindowIndex
                 readQuranActivity.updatePagerPadding(0)
                 val aya = currentPlayedAyat?.get(p)

                 if (aya != null) highlightCurrentPlayedAya(aya)*/
            }
            //The player has finished playing the all passed media.
            Player.STATE_ENDED -> {/*terminatePlayer()*/
            }
        }
    }


/*    private fun terminatePlayer() {
        readQuranActivity.releasePlayer()
        clearAllHighlighted()
        exoPlayer.removeListener(this)
    }*/
}