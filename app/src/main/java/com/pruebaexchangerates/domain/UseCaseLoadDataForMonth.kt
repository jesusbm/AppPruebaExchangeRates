package com.pruebaexchangerates.domain

import android.util.Log
import com.pruebaexchangerates.data.repository.HistoricalRateRepository
import com.pruebaexchangerates.domain.mapper.RateDataMapper
import com.pruebaexchangerates.ui.model.RateModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.Date
import java.util.Calendar

class UseCaseLoadDataForMonth(val repository: HistoricalRateRepository) {

    private val sTAG = UseCaseLoadDataForMonth::class.java.simpleName

    fun loadData(month: Int, year: Int, callback: DataReceiver) {
        val dateForQuery = getDateFromMonthAndYear(month, year)
        val observable = repository.getEuroUsdRateForMonthAndYear(dateForQuery)
        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.map { RateDataMapper.fromDataToModel(it) } }
            .subscribe(getOnOkFunction(callback), getOnErrorFunction(callback))
    }

    private fun getOnOkFunction(callback: DataReceiver): Consumer<List<RateModel>> {
        return Consumer {
            callback.setData(it)
        }
    }

    private fun getOnErrorFunction(callback: DataReceiver): Consumer<Throwable> {
        return Consumer {
            callback.onError(it)
        }
    }

    private fun getDateFromMonthAndYear(month: Int, year: Int): Date {
        return Calendar.getInstance().run {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, 1)
            time
        }
    }

    interface DataReceiver {
        fun setData(data: List<RateModel>)
        fun onError(throwable: Throwable)
    }
}
