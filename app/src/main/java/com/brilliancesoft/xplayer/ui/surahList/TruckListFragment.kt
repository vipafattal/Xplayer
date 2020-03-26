package com.brilliancesoft.xplayer.ui.surahList


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import co.jp.smagroup.musahaf.model.ShortcutDetails
import com.abed.magentaX.android.context.newIntent
import com.abed.magentaX.android.utils.AppPreferences
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.commen.PreferencesKeys
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.Shortcut
import com.brilliancesoft.xplayer.utils.observer
import kotlinx.android.synthetic.main.fragment_reciter_list.*
import kotlinx.android.synthetic.main.toolbar_details.*
import kotlinx.serialization.UnstableDefault
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TruckListFragment : BaseFragment() {


    companion object {
        const val TAG = "ReciterListFragment"

        fun getInstance(reciter: Reciter, language: String): TruckListFragment {
            val reciterListFragment = TruckListFragment()

            reciterListFragment.reciter = reciter
            reciterListFragment.language = language

            return reciterListFragment
        }
    }


    private val surahViewModel: SurahViewModel by viewModel()
    private val appPreference : AppPreferences by inject()
    private lateinit var reciter: Reciter
    private lateinit var language: String
    private lateinit var surahsAdapter: TruckAdapter
    private var isScrollAnimated = false

    override val layoutId: Int = R.layout.fragment_reciter_list


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager?.let {
            surahsAdapter =
                TruckAdapter(emptyList(), reciter, it)
        }
    }

    @UnstableDefault
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeRecycler()
        reciterDetailsName.text = reciter.name
        detailsToolbar.inflateMenu(R.menu.menu_trucks)
        detailsToolbar.menu.getItem(0)
            .setOnMenuItemClickListener {
                val shortcutDetails =
                    ShortcutDetails(reciter.id, reciter.name, R.drawable.reciter_icon)
                Shortcut.create(requireContext(), shortcutDetails, getShortcutIntent())
                true
            }

        initNestScrollViewListener()

        surahViewModel.getSurahList(appPreference.getStr(PreferencesKeys.RECITING_LANGUAGE)).observer(viewLifecycleOwner) {
            surahsAdapter.updateDataList(it)
        }
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

    private fun getShortcutIntent() = context!!.newIntent<MainActivity>().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        action = "LOCATION_SHORTCUT"
        val bundle = bundleOf(
            MainActivity.RECITER_SHORTCUT_ID to reciter.id,
            MainActivity.RECITER_SHORTCUT_NAME to reciter.name,
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

}
