package com.abed.xplayer.model

/**
 * Created by Abed on
 */
data class Duration(val minutes: Int, val seconds: Int) {

    override fun toString(): String = "$minutes:$seconds"

    companion object {
        const val oneMinute: Int = 60 * 1000
        const val oneSecond: Int = 1000

        fun toDuration(timeInMilliSeconds: Long): Duration {
            val min = timeInMilliSeconds / oneMinute
            val seconds = (timeInMilliSeconds % oneMinute) / oneSecond
            return Duration(minutes = min.toInt(), seconds = seconds.toInt())
        }
    }
}