package com.abed.xplayer.ui.sharedComponent.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.abed.xplayer.ui.MainActivity
import com.codebox.lib.android.widgets.recyclerView.onScroll

abstract class BaseFragment : Fragment() {


    protected abstract val layoutId: Int
    protected lateinit var parentActivity: MainActivity
        private set

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity

    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutId, container, false)


    protected fun RecyclerView.scrollRegister() {
        onScroll { _, dy ->
            if (dy > 0) doOnScroll(isScrollingDown = true)
            if (dy < 0) doOnScroll(isScrollingDown = false)
        }
    }

    protected open fun doOnScroll(isScrollingDown: Boolean) {}

}
