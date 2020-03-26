package com.brilliancesoft.xplayer.model

import com.brilliancesoft.xplayer.framework.utils.DownloadMediaUtils
import com.google.firebase.firestore.Exclude


/**
 * Created by  on
 */

data class Media(
    val title: String = "",
    val subtitle: String? = null,
    val link: String = "",
    @Exclude val isRadio: Boolean = false
) {
    constructor(radio: Radio) : this(
        radio.name.replace('-', ' ').trim(),
        link = radio.url,
        isRadio = true
    )

    val isDownloaded: Boolean
        get() = !isRadio && DownloadMediaUtils.isDownloaded(this)


}
