package com.brilliancesoft.xplayer.ui.home.radio

import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.visible
import com.abed.magentaX.android.widgets.recyclerView.onScroll
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Radio
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.home.HomeFragmentDirections
import com.brilliancesoft.xplayer.ui.home.HomeViewModel
import com.brilliancesoft.xplayer.utils.observer
import com.brilliancesoft.xplayer.utils.viewExtensions.animateElevation
import kotlinx.android.synthetic.main.fragment_more_radio.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class RadioMoreFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_more_radio
    private val homeViewModel: HomeViewModel by viewModel()
    private var radioList = listOf<Radio>()

    private val numberOfRadios: Int by lazy {
        if (arguments != null)
            RadioMoreFragmentArgs.fromBundle(requireArguments()).radiosNumber
        else
            RADIO_NUMBER_IN_HOME
    }

    override fun onActivityCreated() {
        super.onActivityCreated()

        homeViewModel.getRadioList().observer(viewLifecycleOwner) { radio ->
            if (radio.isNotEmpty()) {
                radioList = radio
                initRecycler()
            }
        }

        moreRadios.setOnClickListener {
            val navController = (rootView.context as MainActivity).navController
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToRadioMoreFragment(
                    RADIO_NUMBER_UNLIMITED
                )
            )
        }
    }

    private fun initRecycler() {
        var isShowingFromFromHomeFragment = false
        radiosTitle.visible()
        if (numberOfRadios != RADIO_NUMBER_UNLIMITED) {
            radioList = radioList.subList(0, numberOfRadios)
            isShowingFromFromHomeFragment = true
        } else
            viewingFromSelf()


        radioRecycler.adapter = RadioAdapter(radioList, isShowingFromFromHomeFragment)
    }

    private fun viewingFromSelf(){
        val bottom =
            requireContext().theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                .getDimension(0, 0f)

        rootView.updatePadding(bottom = bottom.roundToInt())

        radioRecycler.onScroll { dx, dy ->
            if (dy > 0) titleCardRadio.animateElevation(false)
            else titleCardRadio.animateElevation(true)
        }
        moreRadios.gone()
        radioRecycler.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
    }

    companion object {
        private const val RADIO_NUMBER_UNLIMITED = 0
        private const val RADIO_NUMBER_IN_HOME = 7
    }

}