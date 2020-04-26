package com.brilliancesoft.xplayer.ui.user_activity

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.findNavController

import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.ui.NavigationPager
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.viewExtensions.addTopInsetPadding
import com.brilliancesoft.xplayer.utils.viewExtensions.reduceDragSensitivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_user_activity.*


class UserActivityFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_user_activity


    private val moveToTabNumber:Int by lazy {
        UserActivityFragmentArgs.fromBundle(requireArguments()).openTabNumber
    }

    override fun onActivityCreated() {
        initViewPager()
        user_activity_tabs.getTabAt(moveToTabNumber)!!.select()
        userActivityPager.setCurrentItem(moveToTabNumber,true)
    }

    private fun initViewPager() {
        userActivityPager.adapter = NavigationPager(requireActivity())
        user_activity_tabs.addTopInsetPadding()
        TabLayoutMediator(
            user_activity_tabs,
            userActivityPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text =  when (position) {
                    DOWNLOAD_TAB_NUMBER -> getString(R.string.downloaded)
                    PLAYLIST_TAB_NUMBER -> getString(R.string.playlist)
                    HISTORY_TAB_NUMBER -> getString(R.string.history)
                    else -> throw IllegalArgumentException("Unknown tab position $position")
                }
            }).attach()
    }

    companion object {
      const val DOWNLOAD_TAB_NUMBER = 0
      const val PLAYLIST_TAB_NUMBER = 1
      const val HISTORY_TAB_NUMBER = 2
    }

}
