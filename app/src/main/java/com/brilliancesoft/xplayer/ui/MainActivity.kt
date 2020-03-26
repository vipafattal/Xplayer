package com.brilliancesoft.xplayer.ui

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
import com.abed.magentaX.android.context.launchActivity
import com.abed.magentaX.android.fragments.transaction
import com.abed.magentaX.android.utils.AppPreferences
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.commen.PreferencesKeys
import com.brilliancesoft.xplayer.model.Duration
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.slidinguppanel.OnPanelSlided
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.ui.player.MediaPlayerService
import com.brilliancesoft.xplayer.ui.player.PlayerFragment
import com.brilliancesoft.xplayer.ui.surahList.TruckListFragment
import com.brilliancesoft.xplayer.utils.toArabicNumber
import com.brilliancesoft.xplayer.utils.viewExtensions.addTopInsetPadding
import com.brilliancesoft.xplayer.utils.viewExtensions.reduceDragSensitivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.layout_player_bottom_bar.*
import org.koin.android.ext.android.inject
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var playerService: MediaPlayerService
    private var isBuffering: Boolean = false
    private var isMusicPanelVisible = false
    private var timer: Timer? = null
    private var isCurrentMediaCompleted = false
    private val playerServiceIntent by lazy { Intent(this, MediaPlayerService::class.java) }
    private val appPreferences: AppPreferences by inject()
    private lateinit var playerFragment: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (appPreferences.getStr(PreferencesKeys.RECITING_LANGUAGE, "") == "") {
            launchActivity<WelcomeActivity>()
            finish()
            return
        }

        volumeControlStream = AudioManager.STREAM_MUSIC
        setContentView(R.layout.activity_main)

        sliding_layout.setPanelSlideListener(onPanelSlided)

        playBarPlayButton.setOnClickListener(this)
        playPauseButton.setOnClickListener(this)

        userErrorNotifier()
        initViewPager()

        val shortcut = getReciterFromShortcut()
        //Check if coming from shortcut.
        if (shortcut != null) {
            supportFragmentManager.transaction {
                replace(
                    R.id.fragmentHost,
                    TruckListFragment.getInstance(
                        shortcut,
                        appPreferences.getStr(PreferencesKeys.RECITING_LANGUAGE)
                    ),
                    TruckListFragment.TAG
                )
                addToBackStack(null)
            }
        }

        playerFragment =
            supportFragmentManager.findFragmentById(R.id.playerFragment) as PlayerFragment
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

    private val onPanelSlided = object : OnPanelSlided {
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
        try {
            unbindService(connection)
            if (!playerService.isPlaying)
                playerService.stopService(playerServiceIntent)
        } catch (ex: IllegalArgumentException) {
            print(ex.stackTrace)
        }
    }


    private fun initViewPager() {
        mainNavigationPager.adapter = NavigationPager(this)
        mainNavigationPager.reduceDragSensitivity()

        tabsMain.addTopInsetPadding()

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

    fun getCurrentPlayMedia(): Media? =
        if (::playerService.isInitialized) playerService.getCurrentPlayMedia() else null

    fun getPlayerService(): MediaPlayerService? =
        if (::playerService.isInitialized) playerService else null


    private fun onMediaFirstPlayed() {
        val media = playerService.getCurrentPlayMedia()
        media?.let {
            val mediaInfo = media.title + ": " + media.subtitle
            playBarTitle.text = mediaInfo

            playerFragment.dispatchMediaToUI(media)
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
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            if (service is MediaPlayerService.ServiceBinder) {
                playerService = service.getService()

                playerService.setPlayerListener(PlayerListener())
                playerService.initializePlayerView(playerFragment.audioPlayerView)

                if (playerService.isPlaying) {
                    playBarTitle.text = playerService.getCurrentPlayMedia()!!.title
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
