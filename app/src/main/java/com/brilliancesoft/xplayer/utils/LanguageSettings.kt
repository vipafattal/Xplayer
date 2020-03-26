package com.brilliancesoft.xplayer.utils

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

fun Int.toArabicNumber(): String {
    var output = this.toString()
    for ((englishNumber, arabicNumber) in numbers)
        output = output.replace(englishNumber, arabicNumber)

    return output
}

fun String.toArabicNumber(): String {
    var output = this
    for ((englishNumber, arabicNumber) in numbers)
        output = output.replace(englishNumber, arabicNumber)

    return output
}