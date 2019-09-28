package com.abed.xplayer.ui.sharedComponent.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import com.abed.xplayer.ui.MainActivity

abstract class BaseDialog:DialogFragment() {
    protected abstract val layoutId: Int
    protected lateinit var parentActivity: MainActivity
        private set


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId,container,false)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity
    }

}
