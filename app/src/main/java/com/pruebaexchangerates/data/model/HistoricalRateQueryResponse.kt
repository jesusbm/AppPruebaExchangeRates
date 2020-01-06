package com.pruebaexchangerates.data.model

data class HistoricalRateQueryResponse(

    val success: Boolean,

    val historical: Boolean,

    val date: String,

    val timestamp: Long,

    val base: String,

    val rates: Map<String, Double>,

    val error: ApiQueryError? = null,

    var queryTimestamp: Long = 0
)
