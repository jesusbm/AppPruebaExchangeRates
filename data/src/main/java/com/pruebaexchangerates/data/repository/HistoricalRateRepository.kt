package com.pruebaexchangerates.data.repository

import com.pruebaexchangerates.data.common.getTimestampForEndOfDay
import com.pruebaexchangerates.data.common.getTimestampForStartOfDay
import com.pruebaexchangerates.data.dao.HistoricalRateDao
import com.pruebaexchangerates.data.model.HistoricalRate
import com.pruebaexchangerates.data.provider.EuroRatesProvider
import io.reactivex.Single
import java.util.*

class HistoricalRateRepository(provider: EuroRatesProvider, dao: HistoricalRateDao) {

    private val _provider = provider
    private val _dao = dao

    fun getEuroUsdRateForSingleDay(date: Date): Single<List<HistoricalRate>> {
        val baseCurrency = "EUR"
        val otherCurrency = "USD"
        val singleList =
            _dao.getRatesInTimespanForCurrencies(
                baseCurrency, otherCurrency,
                date.getTimestampForStartOfDay(), date.getTimestampForEndOfDay()
            )
        singleList.flatMap {
            if (0 == it.size) {
                
            }
        }
    }

    fun getEuroUsdRateForPeriod(startDate: Date, endDate: Date): Single<List<HistoricalRate>> {
        val baseCurrency = "EUR"
        val otherCurrency = "USD"
        val singleList = _dao.getRatesInTimespanForCurrencies(
            baseCurrency, otherCurrency,
            startDate.time, endDate.time
        )
        singleList.onerr

    }
}