package com.pruebaexchangerates.data.provider

import com.pruebaexchangerates.data.model.HistoricalRateQueryResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FixerApi {

    @GET("{dateString}")
    fun getEuroToAnotherCurrenciesRateForDate(

        @Path("dateString")
        date: String,

        @Query("symbols")
        anotherCurrenciesList: String

    ): Single<HistoricalRateQueryResponse>
}
