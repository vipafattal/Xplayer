package com.brilliancesoft.xplayer.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.abed.magentaX.android.context.launchActivity
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Duration
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.commen.UserPreferences
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.slidinguppanel.OnPanelSlided
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.ui.home.HomeFragmentDirections
import com.brilliancesoft.xplayer.ui.home.HomeViewModel
import com.brilliancesoft.xplayer.ui.player.MediaPlayerService
import com.brilliancesoft.xplayer.ui.player.PlayerFragment
import com.brilliancesoft.xplayer.ui.user_activity.history.HistoryViewModel
import com.brilliancesoft.xplayer.utils.toLocalizedNumber
import com.brilliancesoft.xplayer.utils.viewExtensions.addTopInsetPadding
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.layout_player_bottom_bar.*
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener   {

    private lateinit var playerService: MediaPlayerService
    private var isBuffering: Boolean = false
    private var isMusicPanelVisible = false
    private var timer: Timer? = null
    private var isCurrentMediaCompleted = false
    private val playerServiceIntent by lazy { Intent(this, MediaPlayerService::class.java) }
    private lateinit var playerFragment: PlayerFragment

    private val historyPlayingDate: HistoryViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    val navController: NavController by lazy { findNavController(R.id.mainNavHost) }

    //private val navigationViewModel: NavigationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLanguage = UserPreferences.getAppLanguage()
        if (appLanguage == null) {
            launchActivity<WelcomeActivity>()
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        sliding_layout.setPanelSlideListener(onPanelSlided)
        playBarPlayButton.setOnClickListener(this)
        playPauseButton.setOnClickListener(this)

        userErrorNotifier()

        val reciterShortcut = getReciterFromShortcut()
        //Check if coming from shortcut.
        if (reciterShortcut != null) {
            val serializedReciter = Json.stringify(Reciter.serializer(), reciterShortcut)
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToTruckListFragment(
                    serializedReciter
                )
            )
        }

        playerFragment =
            supportFragmentManager.findFragmentById(R.id.playerFragment) as PlayerFragment

        main_bottombar.setupWithNavController(navController)
        //This will forbids reCreating fragment object in Navigation Component.
        main_bottombar.setOnNavigationItemReselectedListener {/*Do Nothing*/ }

        contentMainRootView.addTopInsetPadding()

        // main_bottombar.setOnNavigationItemSelectedListener(this)

        /*  if (savedInstanceState != null) {
              val (navigationId, currentFragment) = navigationViewModel.getCurrentNavigation()

              if (navigationId == R.id.item_home) {
                  main_bottombar.selectedItemId = navigationId
                  currentNavId = navigationId
                  updateCurrentFragment(currentFragment)
              }
          } else
              main_bottombar.selectedItemId = R.id.item_home*/

    }

/*    private var currentNavId: Int = 0
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        if (currentNavId != item.itemId) {
            currentNavId = item.itemId
            when (item.itemId) {
                R.id.item_home -> fragment = HomeFragment()
                R.id.item_search -> fragment = SearchFragment()
                R.id.item_activity -> fragment = UserActivityFragment()
            }

            if (fragment != null) {
                updateCurrentFragment(fragment)
            }
        }
        return true
    }*/

    /*private fun updateCurrentFragment(fragment: Fragment) {
        navigationViewModel.updateNavigation(currentNavId, fragment)
        replaceFragment(fragment, R.id.fragment_host)
    }*/

    private fun getReciterFromShortcut(): Reciter? {
        val bundle = intent.extras
        val id = bundle?.getString(RECITER_SHORTCUT_ID)
        val name = bundle?.getString(RECITER_SHORTCUT_NAME)
        val rewaya = bundle?.getString(RECITER_SHORTCUT_SERVER)
        val servers = bundle?.getString(RECITER_SHORTCUT_SERVER)

        return if (id != null) Reciter(id, name!!, rewaya!!, servers!!)
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
            if (::playerService.isInitialized && !playerService.isPlaying)
                playerService.stopService(playerServiceIntent)
        } catch (ex: IllegalArgumentException) {
            print(ex.stackTrace)
        }
    }


    fun play(media: Media) {
        executeWithPendingPermission(STORAGE_PERMISSION, STORAGE_REQUEST_CODE) {
            playerService.playMedia(media)
        }
    }

    fun playMediaList(playlist: Playlist, startAtPosition: Int = 0) {
        executeWithPendingPermission(STORAGE_PERMISSION, STORAGE_REQUEST_CODE) {
            playerService.playMedia(playlist, startAtPosition)
        }
    }

    fun getCurrentPlayMedia(): Media? =
        if (::playerService.isInitialized) playerService.getCurrentPlayMedia() else null

    fun getPlayerService(): MediaPlayerService? =
        if (::playerService.isInitialized) playerService else null


    private fun onMediaFirstPlayed() {
        val media = playerService.getCurrentPlayMedia()
        if (media != null) {
            historyPlayingDate.updateMediaHistory(media.copy(playData = Date().time))
            playBarTitle.text = media.info
            if (playerFragment.view != null)
                playerFragment.dispatchMediaToUI(media, playerService.getCurrentPlaylist().list)
        }
    }

    private fun activeCurrentMinLeftInMedia() {
        timer?.cancel()
        timer = Timer()

        timer?.schedule(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                runOnUiThread {

                    val currentMediaPosition = audioPlayerView.player.currentPosition
                    val maxMediaPosition = audioPlayerView.player.contentDuration
                    val duration = playerService.getCurrentMediaDuration()

                    if (duration != null) {
                        currentMediaProgress.setPosition(currentMediaPosition)
                        currentMediaProgress.setDuration(maxMediaPosition)
                    }

                    if (isBuffering)
                        playBarSubtitle.text = getString(R.string.buffering)
                    else {
                        if (duration != null)
                            playBarSubtitle.text = duration.toString().toLocalizedNumber()
                        else {
                            playBarSubtitle.text = getString(R.string.radio)
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
                    onMediaFirstPlayed()
                    activeCurrentMinLeftInMedia()
                }
            }
        }
    }

    companion object {
        const val RECITER_SHORTCUT_ID = "Reciter-ID"
        const val RECITER_SHORTCUT_NAME = "Reciter-Name"
        const val REWAYA_SHORTCUT_SERVER = "Rewaya-Server"
        const val RECITER_SHORTCUT_SERVER = "Reciter-Server"
    }

}
