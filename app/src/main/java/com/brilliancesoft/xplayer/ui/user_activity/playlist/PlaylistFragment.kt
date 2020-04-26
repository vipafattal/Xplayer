package com.brilliancesoft.xplayer.ui.user_activity.playlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abed.magentaX.android.views.gone
import com.abed.magentaX.android.views.listeners.onClicks
import com.abed.magentaX.android.views.visible
import com.abed.magentaX.android.widgets.snackbar.material
import com.abed.magentaX.android.widgets.snackbar.snackbar
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.ui.commen.windowControllers.BaseFragment
import com.brilliancesoft.xplayer.utils.observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.layout_empty_data_text.*
import kotlinx.android.synthetic.main.layout_no_account.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class PlaylistFragment : BaseFragment() {

    private val playlistViewModel: PlaylistViewModel by viewModel()
    private val firebaseAuth: FirebaseAuth by inject()

    override val layoutId: Int = R.layout.fragment_playlist

    override fun onActivityCreated() {
        val user = firebaseAuth.currentUser

        if (user != null) loadData()
        else noAccountView.visible()

        onClicks(createAccountButton, signInButton) {
            val providers = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setLogo(R.drawable.ic_logo_24dp)
                    .setAvailableProviders(providers)
                    .setTosAndPrivacyPolicyUrls(
                        "link to app terms and service",
                        "link to app privacy policy"
                    )
                    .build(), RC_SIGN_IN
            )
        }
    }

    override fun loadData() {
        super.loadData()

        noAccountView.gone()

        playlistViewModel.getPlayLists().observer(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                loadingCompleted()
                emptyDataText.gone()
                playlistRecycler.adapter = PlaylistAdapter(it)
            } else
                showNoDataView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val parentActivity = activity as AppCompatActivity

        if (requestCode == RC_SIGN_IN) {
            /*
                this checks if the user_activity result we are getting is for the sign in
                as we can have more than user_activity to be started in our Activity.
             */
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
                parentActivity.snackbar(getString(R.string.signed_in)).material()
                loadData()
                return
            } else {
                if (response == null) {
                    //If no response from the Server
                    parentActivity.snackbar("cancelled").material()
                    return
                }
                if (response.error != null) {
                    //If there was a network problem the user'Liked_Tag phone
                    parentActivity.snackbar("unknown error").material()
                    return
                }
            }
        }
        parentActivity.snackbar("unknown sign in response").material()
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}
