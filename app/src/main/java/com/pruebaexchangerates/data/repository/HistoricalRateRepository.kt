package com.pruebaexchangerates.data.repository

import android.util.Log
import com.pruebaexchangerates.data.common.getTimestampForEndOfDay
import com.pruebaexchangerates.data.common.getTimestampForStartOfDay
import com.pruebaexchangerates.data.dao.HistoricalRateDao
import com.pruebaexchangerates.data.mapper.HistoricalRateDataMapper
import com.pruebaexchangerates.data.model.HistoricalRate
import com.pruebaexchangerates.data.provider.EuroRatesProvider
import io.reactivex.Single
import java.util.*

class HistoricalRateRepository(provider: EuroRatesProvider, dao: HistoricalRateDao) {

    private val sTAG = HistoricalRateRepository::class.java.simpleName

    private val _provider = provider
    private val _dao = dao

    fun getEuroUsdRateForSingleDay(date: Date): Single<List<HistoricalRate>> {
        val baseCurrency = "EUR"
        val otherCurrency = "USD"

        val singleFromDatabase = getSingleDayDataFromDatabase(baseCurrency, otherCurrency, date)
        val singleFromNetwork = getSingleDayDataFromNetwork(baseCurrency, otherCurrency, date)

        val databaseLaterNetworkSingle = singleFromDatabase.flatMap {
            if (it.isNotEmpty()) {
                Single.fromCallable { it }
            } else {
                singleFromNetwork
            }
        }

        return databaseLaterNetworkSingle
        /*return singleList.flatMap {
            if (1 != it.size) {
                _provider.getEuroRatesForDate("$date", listOf(otherCurrency))
                //.map { _dao.insertRate(it) }
            } else {
                Single.just(listOf())
            }
        }*/
    }

    fun getEuroUsdRateForMonthAndYear(date: Date): Single<List<HistoricalRate>> {
        val baseCurrency = "EUR"
        val otherCurrency = "USD"

        val singleFromDatabase = getSingleDayDataFromDatabase(baseCurrency, otherCurrency, date)
        val singleFromNetwork = getMonthlyDataFromNetwork(baseCurrency, otherCurrency, date)

        val databaseLaterNetworkSingle = singleFromDatabase.flatMap {
            if (it.isNotEmpty()) {
                Single.fromCallable { it }
            } else {
                singleFromNetwork
            }
        }

        return databaseLaterNetworkSingle
    }

    private fun getSingleDayDataFromNetwork(
        baseCurrency: String, otherCurrency: String, date: Date
    ): Single<List<HistoricalRate>> {
        return _provider.getEuroRatesForDate(date, listOf(otherCurrency))
    }

    private fun getMonthlyDataFromNetwork(
        baseCurrency: String, otherCurrency: String, date: Date
    ): Single<List<HistoricalRate>> {
        val listDates = getDateListForMonthYear(date)
        return _provider.getEuroRatesForDates(listDates, listOf(otherCurrency))
    }

    private fun getSingleDayDataFromDatabase(
        baseCurrency: String, otherCurrency: String, date: Date
    ): Single<List<HistoricalRate>> {
        return _dao.getRatesInTimespanForCurrencies(
            baseCurrency, otherCurrency,
            date.getTimestampForStartOfDay(), date.getTimestampForEndOfDay()
        ).map { it.map { HistoricalRateDataMapper.entityToModel(it) } }
    }

    private fun getDateListForMonthYear(date: Date): List<Date> {
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        val firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) / 2
        val dates = IntRange(firstDay, lastDay).map { it ->
            calendar.set(Calendar.DAY_OF_MONTH, it)
            calendar.time
        }
        return dates
    }
}
