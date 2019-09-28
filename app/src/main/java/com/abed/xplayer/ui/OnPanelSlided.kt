package com.abed.xplayer.ui

import android.view.View
import com.abed.xplayer.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by  on
 */
interface OnPanelSlided : SlidingUpPanelLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View, slideOffset: Float) {}
    override fun onPanelAnchored(panel: View) {}
    override fun onPanelHidden(panel: View) {}

}