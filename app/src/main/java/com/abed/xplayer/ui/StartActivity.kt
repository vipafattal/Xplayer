package com.abed.xplayer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abed.xplayer.R
import com.abed.xplayer.ui.sharedComponent.controllers.BaseActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }
}
