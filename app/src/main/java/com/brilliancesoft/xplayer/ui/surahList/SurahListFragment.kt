package com.brilliancesoft.xplayer.ui.surahList


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnticipateInterpolator
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import co.jp.smagroup.musahaf.model.ShortcutDetails
import com.abed.magentaX.android.context.newIntent
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.model.Sura
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.user_activity.downloaded.DownloadedViewModel
import com.brilliancesoft.xplayer.ui.commen.UserPreferences
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.user_activity.playlist.playlist_dialog.SelectPlaylistDialog
import com.brilliancesoft.xplayer.utils.Shortcut
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.android.synthetic.main.fragment_reciter_list.*
import kotlinx.android.synthetic.main.toolbar_details.*
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class SurahListFragment : BaseFragment(), RecyclerPressListener<Media> {

    private val downloadedViewModel: DownloadedViewModel by viewModel()
    private val surahViewModel: SurahViewModel by viewModel()

    private lateinit var surahsAdapter: SruahAdapter
    private var isScrollAnimated = false
    private lateinit var downloadedMediaList: List<Media>
    private lateinit var surahList: List<Sura>
    override val layoutId: Int = R.layout.fragment_reciter_list
    private val reciter: Reciter by lazy {
        Json.parse(
            Reciter.serializer(),
            SurahListFragmentArgs.fromBundle(requireArguments()).reciterTruckListArg
        )
    }

    override fun onItemClick(media: Media, clickedViewId: Int) {
        when (clickedViewId) {
            R.id.remove_download -> downloadedViewModel.deleteMedia(media)
            R.id.surah_add_to_playlist ->
                SelectPlaylistDialog.show(media,parentFragmentManager)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @UnstableDefault
    override fun onActivityCreated() {
        reciterDetailsName.text = reciter.name

        initNestScrollViewListener()

        val language = UserPreferences.getAppLanguage()!!.language

        surahViewModel.getSurahList(language).observer(viewLifecycleOwner) {
            surahList = it
            dataLoaded()
        }

        downloadedViewModel.getDownloadedMedia().observer(viewLifecycleOwner) {
            downloadedMediaList = it
            dataLoaded()
        }
    }

    //If downloaded is null means data is not patched from database.
    private fun dataLoaded() {
        if (::surahList.isInitialized && ::downloadedMediaList.isInitialized) {
            surahsAdapter = SruahAdapter(
                dataList = surahList,
                reciter = reciter,
                downloadedMediaList = downloadedMediaList.filter { it.reciterName == reciter.name },
                recyclerPressListener = this
            )
            initializeRecycler()
        }
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.create_reciter_icon -> {
                val shortcutDetails =
                    ShortcutDetails(reciter.id, reciter.name, R.drawable.reciter_icon)
                Shortcut.create(requireContext(), shortcutDetails, getShortcutIntent())
            }
            R.id.play_all_list_item -> {
                    val playlist = Playlist(
                        name = RECITER_SURAHS_PLAYLIST,
                        list = surahList.map { surah ->
                            downloadedMediaList.firstOrNull { it.surahName == surah.name } ?: Media.create(
                                surah,
                                reciter,
                                false
                            )
                        })
                    (context as MainActivity).playMediaList(playlist, 0)

            }
        }
        return true
    }


    private fun initNestScrollViewListener() {
        scrollViewReciterDetails.setOnScrollChangeListener { _: NestedScrollView, _, _, _, _ ->
            val scrollBounds = Rect()
            scrollViewReciterDetails.getHitRect(scrollBounds)
            if (reciterDetailsName.getLocalVisibleRect(scrollBounds)) {
                animate(true)
                detailsToolbar.title = ""
                isScrollAnimated = false

            } else {
                animate(false)
                titleDetailsToolbar.text = reciter.name
                isScrollAnimated = true

            }

        }
    }

    private fun initializeRecycler() {
        recitersDetailsRecycler.adapter = surahsAdapter

        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            RecyclerView.VERTICAL
        )

        recitersDetailsRecycler.addItemDecoration(dividerItemDecoration)
    }

    private fun getShortcutIntent() = requireContext().newIntent<MainActivity>().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        action = "LOCATION_SHORTCUT"
        val bundle = bundleOf(
            MainActivity.RECITER_SHORTCUT_ID to reciter.id,
            MainActivity.RECITER_SHORTCUT_NAME to reciter.name,
            MainActivity.REWAYA_SHORTCUT_SERVER to reciter.rewaya,
            MainActivity.RECITER_SHORTCUT_SERVER to reciter.servers
        )
        putExtras(bundle)
    }

    private var isAnimationRemoved = true
    private fun animate(removeAnimation: Boolean) {
        if (removeAnimation != isAnimationRemoved) {
            isAnimationRemoved = removeAnimation
            val animator: ValueAnimator =
                if (removeAnimation)
                    ValueAnimator.ofFloat(8f, 0f)
                else
                    ValueAnimator.ofFloat(0f, 8f)

            val interpolator = AnticipateInterpolator(2f)
            val animatorSet = AnimatorSet()

            val titleAnimator =
                ObjectAnimator.ofFloat(
                    titleDetailsToolbar,
                    "translationY",
                    if (removeAnimation) titleDetailsToolbar.height.toFloat() else 0f
                )
            animatorSet.playTogether(titleAnimator, animator)
            animatorSet.duration = 300
            animatorSet.interpolator = interpolator
            animatorSet.start()
            animator.addUpdateListener {
                detailsAppbar.elevation = it.animatedValue as Float
            }
        }
    }
    companion object {
        const val RECITER_SURAHS_PLAYLIST = "current reciter playlist"
    }
}
