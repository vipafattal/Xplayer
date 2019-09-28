package com.abed.xplayer.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abed.xplayer.ui.downloaded.DownloadedMediaFragment
import com.abed.xplayer.ui.playList.PlayListFragment
import com.abed.xplayer.ui.home.HomeFragment

class NavigationPager(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val numberOfPages = 3
    private val fragments = mutableMapOf<Int, Fragment>()

    private fun getFragment(position: Int) = when (position) {
        0 -> HomeFragment()
        1 -> PlayListFragment()
        else -> DownloadedMediaFragment()
    }


    override fun getItemCount(): Int = numberOfPages

    override fun createFragment(position: Int): Fragment =
        fragments[position] ?: getFragment(position)


}