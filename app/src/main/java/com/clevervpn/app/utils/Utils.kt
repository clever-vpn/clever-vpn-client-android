package com.clevervpn.app.utils

import android.annotation.SuppressLint

fun Long.msTimerString(): String {
    val totalSeconds = this / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60
    val hours = totalSeconds / 3600
    val days = totalSeconds / (3600 * 24)
    return if (days == 0L) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
    }
}

@SuppressLint("DefaultLocale")
fun prettyBytes(bytes: ULong): String {
    return when (bytes) {
        0UL -> "-"
        in 0UL until 1024UL -> "$bytes B"
        in 1024UL until (1024UL * 1024UL) ->
            String.format("%.2f KiB", bytes.toDouble() / 1024)

        in (1024UL * 1024UL) until (1024UL * 1024UL * 1024UL) ->
            String.format("%.2f MiB", bytes.toDouble() / (1024 * 1024))

        in (1024UL * 1024UL * 1024UL) until (1024UL * 1024UL * 1024UL * 1024UL) ->
            String.format("%.2f GiB", bytes.toDouble() / (1024 * 1024 * 1024))

        else -> String.format("%.2f TiB", bytes.toDouble() / (1024 * 1024 * 1024 * 1024f))
    }
}
