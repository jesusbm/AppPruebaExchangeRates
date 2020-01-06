package com.pruebaexchangerates.data.model

data class HistoricalRate(

    val baseCurrency: String,

    val otherCurrency: String,

    val rate: Double,

    val date: String,

    val timestamp: Long,

    val queryTimestamp: Long
)
