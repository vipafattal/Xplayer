package com.brilliancesoft.xplayer.ui.language


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.abed.magentaX.android.context.launchActivity
import com.abed.magentaX.android.utils.AppPreferences
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Language
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.commen.UserPreferences
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.RecyclerPressListener
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.observer
import com.brilliancesoft.xplayer.utils.viewExtensions.disabled
import com.brilliancesoft.xplayer.utils.viewExtensions.enabled
import kotlinx.android.synthetic.main.fragment_languages.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class LanguagesFragment : BaseFragment(),
    RecyclerPressListener<Language> {

    private val languagesViewModel: LanguagesViewModel by inject()
    private var selectedLanguage: Language? = null


    override val layoutId: Int = R.layout.fragment_languages

    override fun onActivityCreated() {
        loadData()

        if (selectedLanguage == null)
            languageNextButton.disabled()

        languageNextButton.setOnClickListener {
            UserPreferences.saveAppLanguage(selectedLanguage!!)
            context?.launchActivity<MainActivity>()
            activity?.finish()
        }
    }

    override fun loadData() {
        super.loadData()

        languagesViewModel.getLanguages().observer(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showErrorView()
                welcomeFragmentRootView.gone()
            }

            else {
                welcomeFragmentRootView.visible()
                languagesRecycler.adapter = LanguagesAdapter(it, this)
                loadingCompleted()
            }
        }
    }


    override fun onItemClick(data: Language, clickedViewId: Int) {
        languageNextButton.enabled()
        selectedLanguage = data
    }


}
