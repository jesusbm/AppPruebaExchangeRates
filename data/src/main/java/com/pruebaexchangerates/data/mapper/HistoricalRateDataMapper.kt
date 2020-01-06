package com.pruebaexchangerates.data.mapper

import com.pruebaexchangerates.data.model.HistoricalRate
import com.pruebaexchangerates.data.model.HistoricalRateQueryResponse

class HistoricalRateDataMapper {

    companion object {

        fun responseToModels(response: HistoricalRateQueryResponse): List<HistoricalRate> {
            return with(response) {
                rates.entries.map { rate ->
                    HistoricalRate(
                        baseCurrency = base,
                        otherCurrency = rate.key,
                        rate = rate.value,
                        date = date,
                        timestamp = timestamp,
                        queryTimestamp = queryTimestamp
                    )
                }
            }
        }
    }
}
