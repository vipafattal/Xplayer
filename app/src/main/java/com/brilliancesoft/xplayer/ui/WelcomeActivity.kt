package com.brilliancesoft.xplayer.ui

import android.os.Build
import android.os.Bundle
import androidx.core.view.updatePadding
import com.abed.magentaX.android.utils.screenHelpers.dp
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseActivity
import com.brilliancesoft.xplayer.utils.viewExtensions.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_welecome.*
import kotlinx.android.synthetic.main.content_main.*

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideTitleBar()

        setContentView(R.layout.activity_welecome)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            welcomeRootView.doOnApplyWindowInsets { insets, originalPadding ->
                if (insets.systemWindowInsetTop > 0)
                    welcomeRootView.updatePadding(top = originalPadding.top + insets.systemWindowInsetTop)
            }
        } else
            welcomeRootView.updatePadding(top = tabsMain.paddingTop + dp(24))

        userErrorNotifier()
    }
}
