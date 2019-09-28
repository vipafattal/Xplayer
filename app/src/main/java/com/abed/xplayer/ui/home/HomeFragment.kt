package com.abed.xplayer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.abed.xplayer.R
import com.abed.xplayer.framework.data.Repository
import com.abed.xplayer.ui.sharedComponent.ViewModelFactory
import com.abed.xplayer.ui.sharedComponent.XplayerApplication
import com.abed.xplayer.ui.sharedComponent.controllers.BaseFragment
import com.abed.xplayer.utils.observer
import com.abed.xplayer.utils.viewModelOf
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.serialization.UnstableDefault
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : BaseFragment() {

    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val radiosAdapter = RadioAdapter(emptyList())
    private lateinit var homeViewModel: HomeViewModel

    override var layoutId: Int = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XplayerApplication.appComponent.inject(this)
        homeViewModel = viewModelOf(viewModelFactory)
    }

    @UnstableDefault
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recitersAdapter: RecitersAdapter? =
            fragmentManager?.let { RecitersAdapter(emptyList(), it) }

        recitersRecycler.adapter = recitersAdapter
        radioRecycler.adapter = radiosAdapter

        homeViewModel.getRadioList().observer(viewLifecycleOwner) { radio ->
            radiosAdapter.updateData(radio)
        }

        homeViewModel.getRecitersList().observer(viewLifecycleOwner) { reciters ->
            recitersAdapter?.updateData(reciters)
        }

    }
}
