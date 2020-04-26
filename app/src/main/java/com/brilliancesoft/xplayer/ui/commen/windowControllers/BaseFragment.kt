package com.brilliancesoft.xplayer.ui.commen.windowControllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import kotlinx.android.synthetic.main.layout_connection.*
import kotlinx.android.synthetic.main.layout_connection.view.*
import kotlinx.android.synthetic.main.layout_empty_data_text.*
import kotlinx.android.synthetic.main.layout_loading.loadingView


abstract class BaseFragment : Fragment() {

    lateinit var rootView: View
        private set
    protected abstract val layoutId: Int

    //If the saveFragmentRootViewTag is empty we won't save the root view.
    protected open val saveFragmentRootViewTag = ""
    private var hasInitializedRootView: Boolean = false

    companion object {
        private val savedViews = mutableMapOf<String, View>()
    }

    private fun getPersistentView(inflater: LayoutInflater, container: ViewGroup?): View {
        if (saveFragmentRootViewTag.isEmpty() || savedViews[saveFragmentRootViewTag] == null) {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(layoutId, container, false)
            if (saveFragmentRootViewTag.isNotEmpty())
                savedViews.put(saveFragmentRootViewTag, rootView)
            hasInitializedRootView = false
        } else {
            rootView = savedViews.getValue(saveFragmentRootViewTag)
            hasInitializedRootView = true
            (rootView.parent as? ViewGroup)?.removeView(rootView)
        }

        return rootView
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = getPersistentView(inflater, container)
        rootView.retryButton?.onClick { loadData() }
        return rootView
    }

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!hasInitializedRootView) onActivityCreated()
    }

    open fun onActivityCreated() {}


    open fun loadData() {
        showLoading()
        connectionErrorView.gone()
    }

    fun loadingCompleted(withError: Boolean = false) {
        hideLoading()

        if (withError)
            connectionErrorView.visible()
    }


    private fun showLoading() = loadingView!!.visible()

    private fun hideLoading() = loadingView!!.gone()


    fun showErrorView() {
        hideLoading()
        connectionErrorView.visible()
    }

    fun showNoDataView(@StringRes customMessage: Int = R.string.no_data) {
        emptyDataText.text = getString(customMessage)
        emptyDataText.visible()
        hideLoading()
    }

    fun hideNoDataView() {
        emptyDataText.gone()
    }


/*    protected fun RecyclerView.scrollRegister() {
        onScroll { _, dy ->
            if (dy > 0) doOnScroll(isScrollingDown = true)
            if (dy < 0) doOnScroll(isScrollingDown = false)
        }
    }*/

    // protected open fun doOnScroll(isScrollingDown: Boolean) {}

}
