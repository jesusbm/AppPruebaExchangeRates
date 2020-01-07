package com.pruebaexchangerates.common

fun Int.toHourString(): String {
    val prefix = if (10 > this) "0" else ""
    return "$prefix$this"
}