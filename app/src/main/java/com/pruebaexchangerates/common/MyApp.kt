package com.pruebaexchangerates.common

import android.app.Application
import androidx.room.Room
import com.pruebaexchangerates.data.database.HistoricalRateExchangeRoomDatabase
import com.pruebaexchangerates.data.provider.EuroRatesProvider
import com.pruebaexchangerates.data.provider.RetrofitInstance
import com.pruebaexchangerates.data.repository.HistoricalRateRepository
import com.pruebaexchangerates.domain.UseCaseLoadDataForMonth
import com.pruebaexchangerates.ui.InitialScreenViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val moduleList = listOf<Module>(getDataModule())

        startKoin { modules(moduleList) }
    }


    private fun getDataModule(): Module {

        return module {

            //database
            single {
                Room.databaseBuilder(
                    get(),
                    HistoricalRateExchangeRoomDatabase::class.java,
                    "room_database"
                ).build()
            }

            //retrofit
            single {
                RetrofitInstance().buildInstance()
            }

            single { get<HistoricalRateExchangeRoomDatabase>().contratoRoomDao() }

            single { EuroRatesProvider(get()) }

            single { HistoricalRateRepository(get(), get()) }

            single { UseCaseLoadDataForMonth() }

            viewModel { InitialScreenViewModel() }
        }
    }
}
