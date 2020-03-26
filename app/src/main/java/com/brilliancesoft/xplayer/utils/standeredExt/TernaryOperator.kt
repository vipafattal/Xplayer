package com.brilliancesoft.xplayer.utils.standeredExt

/**
 * Created by Abed on
 */

infix fun <T> Boolean.then(param: T): T? {
    return if (this) {
        param
    } else
        null
}


