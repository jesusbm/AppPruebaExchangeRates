package com.pruebaexchangerates.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pruebaexchangerates.data.entity.HistoricalRateEntity
import io.reactivex.Completable
import io.reactivex.Single
import java.sql.Timestamp

@Dao
interface HistoricalRateDao {

    @Query("SELECT * FROM historicalRate WHERE baseCurrency = :baseCurrency ORDER BY timestamp ASC")
    fun getAllRatesForBaseCurrency(baseCurrency: String): Single<List<HistoricalRateEntity>>

    @Query("SELECT * FROM historicalRate WHERE baseCurrency = :baseCurrency AND otherCurrency = :otherCurrency AND timestamp >= :startTimestamp AND timestamp <= :endTimestamp ORDER BY timestamp ASC")
    fun getRatesInTimespanForCurrencies(
        baseCurrency: String,
        otherCurrency: String,
        startTimestamp: Long,
        endTimestamp: Long
    ): Single<List<HistoricalRateEntity>>

    @Insert
    fun insertRate(entity: HistoricalRateEntity): Completable
}
