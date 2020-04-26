package com.brilliancesoft.xplayer.utils

import com.abed.magentaX.android.utils.isRightToLeft
import java.util.*

/**
 * Created by Abed on
 */
private var numbers = arrayOf(
    '0' to '٠',
    '1' to '١',
    '2' to '٢',
    '3' to '٣',
    '4' to '٤',
    '5' to '٥',
    '6' to '٦',
    '7' to '٧',
    '8' to '٨',
    '9' to '٩'
)

fun Int.toLocalizedNumber(): String {

    var output = this.toString()
    if (Locale.getDefault().language == "ar")
        for ((englishNumber, arabicNumber) in numbers)
            output = output.replace(englishNumber, arabicNumber)

    return output
}

fun Int.getLocalizedSurah(): String {
    return if (isRightToLeft) {

        when {
            this == 1 -> "سورة"
            this == 2 -> "سورتين"
            this > 11 -> toLocalizedNumber() + " سور"
            else -> toLocalizedNumber() + " سورة"
        }
    } else when {
        this == 1 -> "Surah"
        else -> toLocalizedNumber() + " Surahs"
    }
}


fun String.toLocalizedNumber(): String {
    var output = this
    for ((englishNumber, arabicNumber) in numbers)
        output = output.replace(englishNumber, arabicNumber)

    return output
}