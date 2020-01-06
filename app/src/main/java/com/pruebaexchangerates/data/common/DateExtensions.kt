package com.pruebaexchangerates.data.common

import java.util.*

fun Date.getTimespanForDay(): Pair<Long, Long> {
    val startTimestamp = this.getTimestampForStartOfDay()
    val endTimestamp = this.getTimestampForEndOfDay()
    return Pair(startTimestamp, endTimestamp)
}

fun Date.getTimestampForStartOfDay(): Long {
    val calendar = Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

fun Date.getTimestampForEndOfDay(): Long {
    val calendar = Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 23)
        it.set(Calendar.MINUTE, 59)
        it.set(Calendar.SECOND, 59)
        it.set(Calendar.MILLISECOND, 999)
    }
    return calendar.timeInMillis
}