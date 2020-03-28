package com.brilliancesoft.xplayer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.abed.magentaX.android.fragments.transaction
import com.abed.magentaX.android.utils.AppPreferences
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.commen.PreferencesKeys
import com.brilliancesoft.xplayer.model.Radio
import com.brilliancesoft.xplayer.model.Reciter
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.ui.surahList.TruckListFragment
import com.brilliancesoft.xplayer.utils.observer

import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : BaseFragment() {


    private val homeViewModel: HomeViewModel by viewModel()

    private val recitersList = mutableListOf<Reciter>()
    private val radioList = mutableListOf<Radio>()
    private val recitersAdapter = RecitersAdapter(recitersList, reciterPressListener)
    private val radioAdapter = RadioAdapter(radioList)
    private val appPreferences: AppPreferences by inject()

    override var layoutId: Int = R.layout.fragment_home

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recitersRecycler.adapter = recitersAdapter
        radioRecycler.adapter = radioAdapter

        loadData()
    }

    private val numberOfDataLoading = MutableLiveData(0)

    override fun loadData() {
        super.loadData()

        val language = appPreferences.getStr(PreferencesKeys.RECITING_LANGUAGE)

        homeViewModel.getRadioList(language).observer(viewLifecycleOwner) { radio ->
            if (radio.isNotEmpty()) {
                numberOfDataLoading.postValue(numberOfDataLoading.value!!.inc())
                radioList.addAll(radio)
                dataLoaded()
            } else
                showErrorView()

        }

        @Suppress("EXPERIMENTAL_API_USAGE")
        homeViewModel.getRecitersList(language).observer(viewLifecycleOwner) { reciters ->
            if (reciters.isNotEmpty()) {
                numberOfDataLoading.postValue(numberOfDataLoading.value!!.inc())
                recitersList.addAll(reciters)
                dataLoaded()
            } else
                showErrorView()
        }
    }

    private fun dataLoaded() {
        if (recitersList.isNotEmpty() && radioList.isNotEmpty()) {
            loadingCompleted()

            recitersTitle.visible()
            recitersAdapter.notifyDataSetChanged()

            if (radioList.first().url != ""){
                //TODO uncomment this line after test & rest radioList visibility.
                //radiosTitle.visible()
                radioAdapter.notifyDataSetChanged()
            }

        }
    }

    private val reciterPressListener: ItemPressListener<Reciter>
        get() = object :
            ItemPressListener<Reciter> {
            override fun onItemClick(data: Reciter) {
                parentFragmentManager.transaction {
                    replace(
                        R.id.fragmentHost,
                        TruckListFragment.getInstance(data),
                        TruckListFragment.TAG
                    )
                    addToBackStack(null)
                }
            }
        }

}
