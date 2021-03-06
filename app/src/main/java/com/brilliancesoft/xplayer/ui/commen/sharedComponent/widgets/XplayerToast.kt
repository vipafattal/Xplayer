package com.brilliancesoft.xplayer.ui.commen.sharedComponent.widgets

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.ui.commen.XplayerApplication
import kotlinx.android.synthetic.main.layout_toast.view.*


class XplayerToast {

    @IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Duration

    companion object {
        @JvmStatic
        fun make(context: Context, message: String, @Duration showingDuration: Int) {
            val layout = View.inflate(context, R.layout.layout_toast, null)

            layout.toast_text.text = message
            val toast = Toast(context)
            with(toast) {
                view = layout
                duration = showingDuration
                show()
            }
        }

        @JvmStatic
        fun make(context: Context, @StringRes message: Int, @Duration showingDuration: Int) {
            val layout = View.inflate(context, R.layout.layout_toast, null)

            layout.toast_text.text = context.getString(message)
            val toast = Toast(context)
            with(toast) {
                view = layout
                duration = showingDuration
                show()
            }
        }

        @JvmStatic
        fun makeShort(context: Context = XplayerApplication.appContext, message: String) {
            make(context, message, Toast.LENGTH_SHORT)
        }

        @JvmStatic
        fun makeLong(context: Context = XplayerApplication.appContext, message: String) {
            make(context, message, Toast.LENGTH_LONG)
        }

        @JvmStatic
        fun makeShort(context: Context, @StringRes message: Int) {
            make(context, message, Toast.LENGTH_SHORT)
        }

        @JvmStatic
        fun makeLong(context: Context, @StringRes message: Int) {
            make(context, message, Toast.LENGTH_LONG)
        }

    }
}
