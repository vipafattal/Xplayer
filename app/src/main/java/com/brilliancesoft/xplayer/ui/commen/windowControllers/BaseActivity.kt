package com.brilliancesoft.xplayer.ui.commen.windowControllers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.abed.magentaX.android.widgets.snackbar.showAction
import com.abed.magentaX.android.widgets.snackbar.snackbar
import com.abed.magentaX.standard.lambda.unitFun
import com.brilliancesoft.xplayer.framework.data.FirebaseRepository
import com.brilliancesoft.xplayer.framework.data.Repository
import com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets.XplayerToast
import com.brilliancesoft.xplayer.utils.observeOnMainThread


/**
 * Created by ${User} on ${Date}
 */
abstract class BaseActivity : AppCompatActivity() {

    private var onPermissionGiven: unitFun? = null
    private var permissionCode: Int = 0

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }


    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideTitleBar()
    }

    protected fun hideTitleBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
       /* this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
       */
    }

    private fun requestForSpecificPermission(requestCode: Int, vararg permissions: String) {
        permissionCode = requestCode
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    fun executeWithPendingPermission(permission: String, requestCode: Int, block: unitFun) {
        onPermissionGiven = block
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result == PackageManager.PERMISSION_GRANTED)
                block.invoke()
            else
                requestForSpecificPermission(
                    requestCode
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        } else {
            block.invoke()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onPermissionGiven?.invoke()
            else XplayerToast.makeShort(
                this,
                "Cannot save audio files without the requested permission"
            )
        }
    }


    protected fun userErrorNotifier() {
        FirebaseRepository.errorStream.observeOnMainThread {
            snackbar(msg = it).showAction()
        }

        Repository.errorStream.observeOnMainThread {
            XplayerToast.makeShort(this, message = it)
        }
    }

    companion object {
        const val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val STORAGE_REQUEST_CODE = 101
    }
}