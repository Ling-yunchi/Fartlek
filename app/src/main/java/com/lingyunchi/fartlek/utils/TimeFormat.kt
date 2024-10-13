package com.lingyunchi.fartlek.utils

fun Long.secondToMinuteSecond(): String {
    // milliseconds to "m分s秒"
    val minutes = this / 60
    val seconds = this % 60
    if (minutes == 0L && seconds == 0L) return "0秒"
    else if (seconds == 0L) return "${minutes}分钟"
    else if (minutes == 0L) return "${seconds}秒"
    return "${minutes}分${seconds}秒"
}

fun Long.milliToMinuteSecond(): String {
    // milliseconds to "m分s秒"
    val minutes = this / 60000
    val seconds = this % 60000 / 1000
    if (minutes == 0L && seconds == 0L) return "0秒"
    else if (seconds == 0L) return "${minutes}分钟"
    else if (minutes == 0L) return "${seconds}秒"
    return "${minutes}分${seconds}秒"
}