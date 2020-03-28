package com.brilliancesoft.xplayer.ui.language


import android.os.Bundle
import android.view.View
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import com.abed.magentaX.android.context.launchActivity
import com.abed.magentaX.android.utils.AppPreferences
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.visible
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.commen.PreferencesKeys
import com.brilliancesoft.xplayer.model.Language
import com.brilliancesoft.xplayer.ui.MainActivity
import com.brilliancesoft.xplayer.ui.WelcomeActivity
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.recyclerView.ItemPressListener
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
    ItemPressListener<Language> {

    private val languagesViewModel: LanguagesViewModel by inject()
    private val appPreferences: AppPreferences by inject()
    private var selectedLanguage: Language? = null


    override val layoutId: Int = R.layout.fragment_languages

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()

        if (selectedLanguage == null)
            languageNextButton.disabled()

        languageNextButton.setOnClickListener {
            appPreferences.put(PreferencesKeys.RECITING_LANGUAGE, selectedLanguage!!.language)
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


    override fun onItemClick(data: Language) {
        languageNextButton.enabled()
        selectedLanguage = data
    }


}
