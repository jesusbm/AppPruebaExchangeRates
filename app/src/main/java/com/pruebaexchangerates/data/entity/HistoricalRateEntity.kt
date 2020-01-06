package com.pruebaexchangerates.data.entity

import androidx.room.Entity

@Entity(tableName = "historicalRate", primaryKeys = ["baseCurrency", "otherCurrency", "date"])
data class HistoricalRateEntity(

    val baseCurrency: String,

    val otherCurrency: String,

    val date: String,

    val rate: Double,

    val timestamp: Long,

    val queryTimestamp: Long
)
