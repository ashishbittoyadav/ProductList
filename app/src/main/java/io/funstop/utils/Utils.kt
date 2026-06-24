package io.funstop.utils

object Utils {

    fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60

        return "%02d:%02d:%02d".format(hrs, mins, secs)
    }

}