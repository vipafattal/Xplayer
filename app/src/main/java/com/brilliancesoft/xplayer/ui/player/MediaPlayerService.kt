package com.brilliancesoft.xplayer.ui.player


import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Duration
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.player.helpers.Constants.PLAYBACK_CHANNEL_ID
import com.brilliancesoft.xplayer.ui.player.helpers.Constants.PLAYBACK_NOTIFICATION_ID
import com.brilliancesoft.xplayer.ui.player.helpers.MediaSourceBuilder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ShuffleOrder
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter


class MediaPlayerService : Service() {

    private lateinit var exoPlayer: ExoPlayer
    private val serviceBinder = ServiceBinder()
    private var isRadio: Boolean = false
    private var currentPlayList: MutableList<Media> = mutableListOf()
    var currentPlaylistId = ""
        private set
    var exoMediaSource: ConcatenatingMediaSource? = null
        private set
    private val bandwidthMeter =
        DefaultBandwidthMeter.Builder(XplayerApplication.appContext).build()
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var playerListener: Player.EventListener? = null
    val isPlaying: Boolean
        get() = exoPlayer.playWhenReady


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindNotificationManger()
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return serviceBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        playerListener = null
        exoPlayer.removeListener(playerListener)
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        if (!this::exoPlayer.isInitialized) initializePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initializePlayer() {

        val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory()

        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            applicationContext,
            DefaultRenderersFactory(applicationContext),
            DefaultTrackSelector(adaptiveTrackSelectionFactory),
            DefaultLoadControl(),
            null,
            bandwidthMeter
        )

    }

    fun initializePlayerView(view: PlayerView) {
        view.player = exoPlayer
        view.showController()
        view.controllerShowTimeoutMs = 0
    }

    fun playMedia(media: Media) {
        exoMediaSource = ConcatenatingMediaSource(MediaSourceBuilder.create(media))

        currentPlayList.clear()
        currentPlayList.add(media)

        //resting saved position for the new media create.
        currentWindow = 0
        playbackPosition = 0
        isRadio = media.isRadio
        exoPlayer.prepare(exoMediaSource, true, true)
        exoPlayer.playWhenReady = true
    }

    fun playMedia(playlist: Playlist, startAtPosition: Int) {
        exoMediaSource = ConcatenatingMediaSource(MediaSourceBuilder.create(playlist.list))

        currentPlaylistId = playlist.id

        currentPlayList.clear()
        currentPlayList.addAll(playlist.list)
        //resting saved position for the new media create.
        currentWindow = startAtPosition
        playbackPosition = 0
        exoPlayer.prepare(exoMediaSource, false, true)
        exoPlayer.seekTo(startAtPosition, C.TIME_UNSET)
        isRadio = false
        //playerView.iconPlayPause.setImageResource(R.drawable.ic_pause)
        exoPlayer.playWhenReady = true
    }

    fun addToPlayingMedia(media: Media) {
        val newMediaDataSource = MediaSourceBuilder.create(media)
        exoMediaSource!!.addMediaSource(exoMediaSource)
        currentPlayList.add(newMediaDataSource.tag as Media)
    }

    fun resumePlayer() {
        if (exoMediaSource != null) {
            //  playerView.iconPlayPause.setImageResource(R.drawable.ic_pause)
            exoPlayer.prepare(exoMediaSource, false, false)
            exoPlayer.seekTo(currentWindow, playbackPosition)
            exoPlayer.playWhenReady = true
        } else
            XplayerToast.makeShort(message = getString(R.string.nothing_to_play))
    }


    fun pausePlayer() {
        exoPlayer.playWhenReady = false
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentWindowIndex
    }


    private fun releasePlayer() {
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
        playerNotificationManager?.setPlayer(null)
        currentWindow = 0
        playbackPosition = 0
    }

    fun setPlayerListener(exoPlayerListener: Player.EventListener) {
        playerListener = exoPlayerListener
        exoPlayer.addListener(exoPlayerListener)
    }

    fun getCurrentPlayMedia(): Media? = exoPlayer.currentTag as? Media
    fun getCurrentPlaylist(): Playlist = Playlist(id = currentPlaylistId, list = currentPlayList)

    fun moveToMedia(media: Media) {
        require(currentPlayList.contains(media)) { "media is not added to the player" }
        if (getCurrentPlayMedia() != media)
            exoPlayer.seekTo(currentPlayList.indexOf(media), C.TIME_UNSET)
    }

    fun getCurrentMediaDuration(): Duration? =
        if (isRadio) null else Duration.toDuration(exoPlayer.duration - exoPlayer.currentPosition)

    fun clearNotification() {
        playerNotificationManager?.setPlayer(null)
    }

    private fun bindNotificationManger() {
        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(XplayerApplication.appContext,
                PLAYBACK_CHANNEL_ID, R.string.playback_channel_name, R.string.xplayer_description,
                PLAYBACK_NOTIFICATION_ID, NotificationDescriptionAdapter(currentPlayList),
                object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationCancelled(
                        notificationId: Int,
                        dismissedByUser: Boolean
                    ) = stopSelf()

                    override fun onNotificationStarted(
                        notificationId: Int,
                        notification: Notification?
                    ) = startForeground(notificationId, notification)

                }
            )
        playerNotificationManager!!.apply {
            setColor(Color.GRAY)
            setColorized(true)
            setUseChronometer(false)
            setUsePlayPauseActions(true)
            setUseStopAction(true)
            setRewindIncrementMs(15000)
            setFastForwardIncrementMs(15000)

            setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            setPlayer(exoPlayer)
        }
    }


    inner class ServiceBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

}
