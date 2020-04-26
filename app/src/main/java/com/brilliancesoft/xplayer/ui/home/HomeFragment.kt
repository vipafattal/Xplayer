package com.brilliancesoft.xplayer.ui.home

import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.abed.magentaX.android.utils.screenHelpers.dp
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.user_activity.UserActivityFragment
import com.brilliancesoft.xplayer.ui.user_activity.history.HistoryViewModel
import com.brilliancesoft.xplayer.utils.observer
import com.brilliancesoft.xplayer.utils.viewExtensions.animateElevation
import com.brilliancesoft.xplayer.utils.viewExtensions.hideCard
import com.brilliancesoft.xplayer.utils.viewExtensions.showCard
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : BaseFragment() {

    private var historyMediaList = listOf<Media>()
    private val historyViewModel by viewModel<HistoryViewModel>()

    override var layoutId: Int = R.layout.fragment_home

    override fun onActivityCreated() {
        rootView.homeNestedScroll.setOnScrollChangeListener { v: NestedScrollView, _, scrollY: Int, _, oldScrollY: Int ->
            val dy = scrollY - oldScrollY

            if (dy < 0) rootView.homeAppBar.showCard()
            else if (dy > 0 && scrollY > dp(24)) rootView.homeAppBar.hideCard()

            if (scrollY < dp(12) || scrollY <= 0) rootView.homeAppBar.animateElevation(true)
            else if (scrollY > dp(56)) rootView.homeAppBar.animateElevation(false)
        }

        initRecentHeader()
    }


    private fun initRecentHeader() {

        historyViewModel.getMediaWithHistory(6).observer(this) {
            historyMediaList = (it)

            if (historyMediaList.isNotEmpty()) {
                recentTitle.visible()
                moreRecent.visible()
                recentRecycler.adapter = RecentAdapter(historyMediaList)
            }

            moreRecent.setOnClickListener {
                it.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToUserActivityFragment(
                        UserActivityFragment.HISTORY_TAB_NUMBER
                    )
                )
            }

        }

    }
}
