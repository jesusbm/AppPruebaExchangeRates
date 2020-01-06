package com.pruebaexchangerates.data.provider

import com.pruebaexchangerates.data.mapper.HistoricalRateDataMapper
import com.pruebaexchangerates.data.model.HistoricalRate
import com.pruebaexchangerates.data.model.HistoricalRateQueryResponse
import io.reactivex.Single

class EuroRatesProvider(retrofitInstance: RetrofitInstance) {

    private val mService: FixerApi by lazy {
        buildApiService(retrofitInstance)
    }

    fun getEuroRatesForDate(date: String, currencies: List<String>): Single<List<HistoricalRate>> {
        val currenciesStr = currencies.joinToString(",")
        val singleQuery = mService.getEuroToAnotherCurrenciesRateForDate(date, currenciesStr)
        return singleQuery
            .map {
                addQueryTimestamp(it)
                HistoricalRateDataMapper.responseToModels(it)
            }
    }

    private fun addQueryTimestamp(data: HistoricalRateQueryResponse): HistoricalRateQueryResponse {
        return data.apply {
            queryTimestamp = System.currentTimeMillis() / 1000
        }
    }

    private fun buildApiService(retrofitInstance: RetrofitInstance): FixerApi {
        return retrofitInstance.instance.create(FixerApi::class.java)
    }
}
