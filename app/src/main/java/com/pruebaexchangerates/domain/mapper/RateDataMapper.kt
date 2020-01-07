package com.pruebaexchangerates.domain.mapper

import com.pruebaexchangerates.data.model.HistoricalRate
import com.pruebaexchangerates.ui.model.RateModel

class RateDataMapper {

    companion object {

        fun fromDataToModel(data: HistoricalRate): RateModel {
            return data.run {
                RateModel(
                    baseCurrency = baseCurrency,
                    otherCurrency = otherCurrency,
                    rate = rate,
                    date = date
                )
            }
        }
        
    }
}
