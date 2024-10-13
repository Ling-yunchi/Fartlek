package com.lingyunchi.fartlek.utils

fun Long.secondToMinuteSecond(): String {
    // milliseconds to "m分s秒"
    val minutes = this / 60
    val seconds = this % 60
    val minText = if (minutes != 0L) "${minutes}分" else ""
    val secText = if (seconds != 0L) "${seconds}秒" else ""
    return minText + secText
}

fun Long.milliToMinuteSecond(): String {
    // milliseconds to "m分s秒"
    val minutes = this / 60000
    val seconds = this % 60000 / 1000
    val minText = if (minutes != 0L) "${minutes}分" else ""
    val secText = if (seconds != 0L) "${seconds}秒" else ""
    return minText + secText
}