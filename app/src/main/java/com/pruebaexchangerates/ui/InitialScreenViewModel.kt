package com.pruebaexchangerates.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.pruebaexchangerates.domain.UseCaseLoadDataForMonth
import com.pruebaexchangerates.ui.common.SingleLiveEvent

class InitialScreenViewModel : ViewModel() {

    private var useCase: UseCaseLoadDataForMonth = get()

    private val _monthSelected = MutableLiveData<Int>()
    private val _yearSelected = MutableLiveData<Int>()

    private val _eventLoadDataForMonth = SingleLiveEvent<Boolean>().apply {
        observeForever {
            useCase.loadData()
        }
    }

    val monthSelected: LiveData<Int>
        get() = _monthSelected

    val yearSelected: LiveData<Int>
        get() = _yearSelected

    fun setMonthSelected(month: Int) {
        this._monthSelected.value = month
        _eventLoadDataForMonth.value = true
    }

    fun setYearSelected(year: Int) {
        this._yearSelected.value = year
        _eventLoadDataForMonth.value = true
    }

    fun loadInfoForSelectedMonth() {
        _eventLoadDataForMonth.value = true
    }

    val monthSelectedName: LiveData<String> = Transformations.map(_monthSelected) {
        when (it) {
            0 -> "Enero"
            1 -> "Febrero"
            2 -> "Marzo"
            3 -> "Abril"
            4 -> "Mayo"
            5 -> "Junio"
            6 -> "Julio"
            7 -> "Agosto"
            8 -> "Septiembre"
            9 -> "Octubre"
            10 -> "Noviembre"
            11 -> "Diciembre"
            else -> ""
        }
    }
}
