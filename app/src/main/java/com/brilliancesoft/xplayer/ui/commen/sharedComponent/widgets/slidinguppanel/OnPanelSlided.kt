package com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.slidinguppanel

import android.view.View

/**
 * Created by  on
 */
interface OnPanelSlided : SlidingUpPanelLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View, slideOffset: Float) {}
    override fun onPanelAnchored(panel: View) {}
    override fun onPanelHidden(panel: View) {}

}