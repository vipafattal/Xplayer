package com.abed.xplayer.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.updatePadding
import com.abed.xplayer.R
import com.abed.xplayer.framework.data.FirebaseRepository
import com.abed.xplayer.framework.data.Repository
import com.abed.xplayer.framework.utils.DownloadMediaUtils
import com.abed.xplayer.model.Duration
import com.abed.xplayer.model.Media
import com.abed.xplayer.model.Reciter
import com.abed.xplayer.ui.player.MediaPlayerService
import com.abed.xplayer.ui.recitersList.TruckListFragment
import com.abed.xplayer.ui.sharedComponent.controllers.BaseActivity
import com.abed.xplayer.ui.sharedComponent.widgets.XplayerToast
import com.abed.xplayer.utils.observeOnMainThread
import com.abed.xplayer.utils.toArabicNumber
import com.abed.xplayer.utils.viewExtensions.doOnApplyWindowInsets
import com.abed.xplayer.utils.viewExtensions.reduceDragSensitivity
import com.codebox.lib.android.fragments.transaction
import com.codebox.lib.android.resoures.Colour
import com.codebox.lib.android.utils.screenHelpers.dp
import com.codebox.lib.android.views.utils.visible
import com.codebox.lib.android.widgets.snackbar.showAction
import com.codebox.lib.android.widgets.snackbar.snackbar
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.layout_player.*
import kotlinx.android.synthetic.main.layout_player_bottom_bar.*
import java.util.*


class MainActivity : BaseActivity(), OnPanelSlided, View.OnClickListener {

    private lateinit var playerService: MediaPlayerService
    private var isBuffering: Boolean = false
    private var isMusicPanelVisible = false
    private var timer: Timer? = null
    private var isCurrentMediaCompleted = false
    private val playerServiceIntent by lazy { Intent(this, MediaPlayerService::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        volumeControlStream = AudioManager.STREAM_MUSIC

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)

        sliding_layout.setPanelSlideListener(this)
        playBarPlayButton.setOnClickListener(this)
        playPauseButton.setOnClickListener(this)
        downloadMediaButton.setOnClickListener(this)

        userErrorNotifier()
        initViewPager()

        getReciterFromShortcut()?.let {
            supportFragmentManager.transaction {
                replace(
                    R.id.fragmentHost,
                    TruckListFragment.getInstance(it, "_arabic"),
                    TruckListFragment.TAG
                )
                addToBackStack(null)
            }
        }

    }


    private fun getReciterFromShortcut(): Reciter? {
        val bundle = intent.extras
        val id = bundle?.getString(RECITER_SHORTCUT_ID)
        val name = bundle?.getString(RECITER_SHORTCUT_NAME)
        val servers = bundle?.getString(RECITER_SHORTCUT_SERVER)

        return if (id != null)
            Reciter(id, name!!, servers!!)
        else null
    }

    override fun onPanelCollapsed(panel: View) {
        isMusicPanelVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = window.decorView
            view.systemUiVisibility =
                view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }

    }

    override fun onPanelExpanded(panel: View) {
        isMusicPanelVisible = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = window.decorView
            view.systemUiVisibility =
                view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

        }

    }

    override fun onBackPressed() {
        if (isMusicPanelVisible) {
            isMusicPanelVisible = false
            sliding_layout.collapsePanel()
        } else
            super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        bindService(playerServiceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        if (!playerService.isPlaying)
            playerService.stopService(playerServiceIntent)
    }

    private fun userErrorNotifier() {
        FirebaseRepository.errorStream.observeOnMainThread {
            snackbar(msg = it).showAction()
        }
        Repository.errorStream.observeOnMainThread {
            XplayerToast.makeShort(this, message = it)
        }
    }

    private fun initViewPager() {
        mainNavigationPager.adapter = NavigationPager(this)
        mainNavigationPager.reduceDragSensitivity()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            tabsMain.doOnApplyWindowInsets { insets, originalPadding ->
                if (insets.systemWindowInsetTop > 0) {
                    tabsMain.updatePadding(top = originalPadding.top + insets.systemWindowInsetTop)
                }
            }
        } else {
            tabsMain.updatePadding(top = tabsMain.paddingTop + dp(24))
        }

        TabLayoutMediator(
            tabsMain,
            mainNavigationPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.home)
                    1 -> getString(R.string.playlist)
                    else -> getString(R.string.downloaded)
                }
            }).attach()

    }

    fun play(media: Media) {
        executeWithPendingPermission(STORAGE_PERMISSION, STORAGE_REQUEST_CODE) {
            playerService.playMedia(media)
        }
    }

    fun playMediaList(mediaList: List<Media>, startAtPosition: Int = 0) {
        executeWithPendingPermission(STORAGE_PERMISSION, STORAGE_REQUEST_CODE) {
            playerService.playMedia(mediaList, startAtPosition)
        }
    }

    private fun onMediaFirstPlayed() {
        val media = playerService.getCurrentPlayMedia()
        media?.let {
            val mediaInfo = media.title + ": " + media.subtitle
            playBarTitle.text = mediaInfo
            currentMediaTitle.text = mediaInfo
            currentMediaTitle.visible()
            downloadMediaButton.visible()
            downloadMediaButton.isActivated = !media.isDownloaded
            if (!media.isDownloaded) {
                downloadMediaButton.text = getString(R.string.downloaded)
                downloadMediaButton.setBackgroundColor(Colour(R.color.colorAccentLight))
                downloadMediaButton.setTextColor(Colour(R.color.white))
            } else {
                downloadMediaButton.text = getString(R.string.download)
                downloadMediaButton.setBackgroundColor(Colour(R.color.colorGrayItem))
                downloadMediaButton.setTextColor(Colour(R.color.colorPrimaryDark))
            }

        }
    }

    private fun activeCurrentMinLeftInMedia() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {

                runOnUiThread {
                    val duration = playerService.getCurrentMediaDuration()
                    if (isBuffering)
                        playBarSubtitle.text = getString(R.string.buffering)
                    else {
                        if (duration != null)
                            playBarSubtitle.text = duration.toString().toArabicNumber()
                        else {
                            playBarSubtitle.text =
                                getString(R.string.radio)
                            timer!!.cancel()

                        }
                    }
                }
            }
        }, 0, Duration.oneSecond.toLong())
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.playPauseButton, R.id.playBarPlayButton -> playPause()
            R.id.downloadMediaButton -> {
                if (downloadMediaButton.isActivated)
                    playerService.getCurrentPlayMedia()?.let {
                        DownloadMediaUtils.download(it, this)
                    }
            }
        }
    }

    private fun playPause() {
        if (::playerService.isInitialized) {
            if (!playerService.isPlaying && playerService.exoMediaSource != null) {
                playerService.resumePlayer()
                iconPlayPause.setImageResource(R.drawable.ic_pause)
                playBarPlayButton.setImageResource(R.drawable.ic_pause)
            } else {
                playerService.pausePlayer()
                iconPlayPause.setImageResource(R.drawable.ic_play)
                playBarPlayButton.setImageResource(R.drawable.ic_play)
            }
        } else
            XplayerToast.makeShort(message = getString(R.string.nothing_to_play))
    }


    inner class PlayerListener : Player.EventListener {

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
            activeCurrentMinLeftInMedia()
            onMediaFirstPlayed()
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)

            when (playbackState) {
                Player.STATE_READY -> {
                    onMediaFirstPlayed()
                    Util.startForegroundService(this@MainActivity, playerServiceIntent)
                    isCurrentMediaCompleted = false
                    isBuffering = false
                }

                Player.STATE_BUFFERING -> {
                    onMediaFirstPlayed()
                    isBuffering = true
                    activeCurrentMinLeftInMedia()
                }

                Player.STATE_ENDED -> {
                    playerService.clearNotification()
                    isCurrentMediaCompleted = true
                    playBarPlayButton.setImageResource(R.drawable.ic_play)
                    iconPlayPause.setImageResource(R.drawable.ic_play)
                    timer?.cancel()
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            super.onPlayerError(error)
            val exoPlayerErrorTag = "exoPlayerErrorTag"

            val errorMessage = when (error!!.type) {
                ExoPlaybackException.TYPE_SOURCE -> "Resource error"
                ExoPlaybackException.TYPE_RENDERER -> "Rendering error"
                ExoPlaybackException.TYPE_UNEXPECTED -> "Unexpected error"
                ExoPlaybackException.TYPE_REMOTE -> "Remote error"
                ExoPlaybackException.TYPE_OUT_OF_MEMORY -> "Out of memory"
                else -> "Unknown error:"
            }
            XplayerToast.makeShort(this@MainActivity, errorMessage)
            Log.v(exoPlayerErrorTag, errorMessage)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            if (service is MediaPlayerService.ServiceBinder) {
                playerService = service.getService()

                playerService.setPlayerListener(PlayerListener())
                playerService.initializePlayerView(audioPlayerView)

                if (playerService.isPlaying) {
                    playBarTitle.text = playerService.getCurrentPlayMedia()?.title
                    activeCurrentMinLeftInMedia()
                }
            }

        }
    }


    companion object {
        const val RECITER_SHORTCUT_ID = "Reciter-ID"
        const val RECITER_SHORTCUT_NAME = "Reciter-Name"
        const val RECITER_SHORTCUT_SERVER = "Reciter-Server"
    }

}
