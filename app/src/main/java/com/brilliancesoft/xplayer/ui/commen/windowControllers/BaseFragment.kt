package com.brilliancesoft.xplayer.ui.commen.windowControllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClick
import com.abed.magentaX.android.views.visible
import com.abed.magentaX.android.widgets.recyclerView.onScroll
import com.brilliancesoft.xplayer.R
import kotlinx.android.synthetic.main.layout_connection.*
import kotlinx.android.synthetic.main.layout_connection.view.*
import kotlinx.android.synthetic.main.layout_empty_data_text.*
import kotlinx.android.synthetic.main.layout_loading.loadingView

abstract class BaseFragment : Fragment() {


    protected abstract val layoutId: Int



    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(layoutId, container, false)
        view.retryButton?.onClick { loadData() }
        return view
    }


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


    protected fun RecyclerView.scrollRegister() {
        onScroll { _, dy ->
            if (dy > 0) doOnScroll(isScrollingDown = true)
            if (dy < 0) doOnScroll(isScrollingDown = false)
        }
    }

    protected open fun doOnScroll(isScrollingDown: Boolean) {}

}
