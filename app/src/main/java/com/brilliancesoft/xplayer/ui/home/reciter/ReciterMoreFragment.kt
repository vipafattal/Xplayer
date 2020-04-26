package com.brilliancesoft.xplayer.ui.home.reciter

import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import com.abed.magentaX.android.utils.screenHelpers.dp
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.visible
import com.abed.magentaX.android.widgets.recyclerView.onScroll
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.home.HomeFragmentDirections
import com.brilliancesoft.xplayer.ui.home.HomeViewModel
import com.brilliancesoft.xplayer.utils.observer
import com.brilliancesoft.xplayer.utils.viewExtensions.animateElevation
import kotlinx.android.synthetic.main.fragment_more_reciter.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ReciterMoreFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_more_reciter

    private val homeViewModel: HomeViewModel by viewModel()
    private var recitersList = listOf<Reciter>()

    private val numberOfReciters: Int by lazy {
        if (arguments != null)
            ReciterMoreFragmentArgs.fromBundle(requireArguments()).recitersNumber
        else
            NUMBER_OF_RECITERS_IN_HOME
    }

    override fun onActivityCreated() {
        super.onActivityCreated()

        moreReciters.setOnClickListener {
            val navController = (rootView.context as MainActivity).navController
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToReciterMoreFragment(
                    RECITERS_NUMBER_UNLIMITED
                )
            )
        }

        homeViewModel.getRecitersList().observer(viewLifecycleOwner) { reciters ->
            if (reciters.isNotEmpty()) {
                recitersList = reciters
                initAdapter()
            }
        }
    }

    private fun initAdapter() {
        moreReciters.visible()
        recitersTitle.visible()
        var isShowingFromFromHomeFragment = false
        if (numberOfReciters != RECITERS_NUMBER_UNLIMITED) {
            recitersList = recitersList.subList(0, numberOfReciters)
            isShowingFromFromHomeFragment = true
        }
        else {
            val bottom =
                requireContext().theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize)).getDimension(0,0f)

            rootView.updatePadding(bottom = bottom.roundToInt())
            moreReciters.gone()

            recitersRecycler.onScroll { dx, dy ->
                if (dy > 0)
                    titleCardReciters.animateElevation(false)
                else
                    titleCardReciters.animateElevation(true)

            }
        }
        recitersRecycler.adapter = RecitersAdapter(recitersList, isShowingFromFromHomeFragment)
    }

    companion object {
        private const val RECITERS_NUMBER_UNLIMITED = 0
        private const val NUMBER_OF_RECITERS_IN_HOME = 12
    }
}