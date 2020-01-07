package com.pruebaexchangerates.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.pruebaexchangerates.R
import com.pruebaexchangerates.domain.UseCaseLoadDataForMonth
import com.pruebaexchangerates.ui.common.SingleLiveEvent
import com.pruebaexchangerates.ui.model.RateModel
import java.util.*
import kotlin.Comparator

class InitialScreenViewModel(
    ctx: Application,
    private val useCase: UseCaseLoadDataForMonth
) : AndroidViewModel(ctx), UseCaseLoadDataForMonth.DataReceiver {

    private val sTAG = InitialScreenViewModel::class.java.simpleName

    private val _monthSelected = MutableLiveData<Int>()
    private val _yearSelected = MutableLiveData<Int>()

    private val _dataXY = MutableLiveData<Pair<IntArray, DoubleArray>>()

    private val _monthsArray = ctx.resources.getStringArray(R.array.month_names)

    private val _eventLoadDataForMonth = SingleLiveEvent<Boolean>().apply {
        observeForever {
            val month = monthSelected.value
            val year = yearSelected.value
            if (null != month && null != year) {
                useCase.loadData(month, year, this@InitialScreenViewModel)
            }
        }
    }


    val yearSelected: LiveData<Int>
        get() = _yearSelected

    val monthSelected: LiveData<Int>
        get() = _monthSelected

    val monthSelectedName: LiveData<String> = Transformations.map(_monthSelected) {
        if (it in IntRange(0, 11)) {
            _monthsArray[it]
        } else {
            ""
        }
    }

    val dataXY: LiveData<Pair<IntArray, DoubleArray>>
        get() = _dataXY

    val eventStartQuery = SingleLiveEvent<Boolean>()

    val eventCompletedQuery = SingleLiveEvent<Boolean>()

    val eventErrorQuery = SingleLiveEvent<String>()


    fun setMonthSelected(month: Int) {
        this._monthSelected.value = month
        _eventLoadDataForMonth.value = true
    }

    fun setYearSelected(year: Int) {
        this._yearSelected.value = year
        _eventLoadDataForMonth.value = true
    }

    fun loadInfoForSelectedMonth() {
        if (null != this._monthSelected.value && null != this._yearSelected.value) {
            eventStartQuery.value = true
            _eventLoadDataForMonth.value = true
        }
    }

    override fun setData(data: List<RateModel>) {
        Log.e(sTAG, "OnOk: $data")
        Collections.sort(data, RateModel.dateStringComparator)
        Log.e(sTAG, "OnOk sorted: $data")
        val dataX = (1..data.size).toList().toIntArray()
        val dataY = data.map { it.rate }
        _dataXY.value = Pair(dataX, dataY.toDoubleArray())
        eventCompletedQuery.value = true
    }

    override fun onError(throwable: Throwable) {
        Log.e(sTAG, "OnError: $throwable")
        eventErrorQuery.value = throwable.message
    }


}
