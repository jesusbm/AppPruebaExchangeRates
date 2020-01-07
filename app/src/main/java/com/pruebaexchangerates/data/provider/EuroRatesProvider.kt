package com.pruebaexchangerates.data.provider

import com.pruebaexchangerates.data.model.HistoricalRateQueryResponse
import com.pruebaexchangerates.data.mapper.HistoricalRateDataMapper
import com.pruebaexchangerates.data.model.HistoricalRate
import com.pruebaexchangerates.common.toHourString
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import java.util.Date
import java.util.Calendar

class EuroRatesProvider(retrofitInstance: Retrofit) {

    private val mService: FixerApi by lazy {
        buildApiService(retrofitInstance)
    }

    fun getEuroRatesForDates(
        dates: List<Date>,
        currencies: List<String>
    ): Single<List<HistoricalRate>> {
        val allNetworkCalls =
            Observable
                .fromIterable(dates)
                .flatMapSingle { getEuroRatesForDate(it, currencies) }
                .flatMap { Observable.fromIterable(it) }
                .toList()
        return allNetworkCalls
    }

    fun getEuroRatesForDate(date: Date, currencies: List<String>): Single<List<HistoricalRate>> {
        return getEuroRatesForDate(getDateStringForService(date), currencies)
    }

    fun getEuroRatesForDate(date: String, currencies: List<String>): Single<List<HistoricalRate>> {
        val currenciesStr = currencies.joinToString(",")
        val singleQuery = mService.getEuroToAnotherCurrenciesRateForDate(date, currenciesStr)
        return singleQuery
            .map {
                addQueryTimestamp(it)
                HistoricalRateDataMapper.responseToModels(it)
            }
            .onErrorReturn {
                listOf()
            }
    }

    private fun addQueryTimestamp(data: HistoricalRateQueryResponse): HistoricalRateQueryResponse {
        return data.apply {
            queryTimestamp = System.currentTimeMillis() / 1000
        }
    }

    private fun buildApiService(retrofitInstance: Retrofit): FixerApi {
        return retrofitInstance.create(FixerApi::class.java)
    }

    private fun getDateStringForService(date: Date): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val year = calendar.get(Calendar.YEAR).toString()
        val month = (1 + calendar.get(Calendar.MONTH)).toHourString()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toHourString()
        return "$year-$month-$day"
    }
}
