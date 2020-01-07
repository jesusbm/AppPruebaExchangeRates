package com.pruebaexchangerates.common

import android.app.Application
import com.pruebaexchangerates.data.database.HistoricalRateExchangeRoomDatabase
import com.pruebaexchangerates.data.provider.EuroRatesProvider
import com.pruebaexchangerates.data.provider.FixerApi
import com.pruebaexchangerates.data.provider.RetrofitInstance
import com.pruebaexchangerates.data.repository.HistoricalRateRepository
import com.pruebaexchangerates.domain.UseCaseLoadDataForMonth
import com.pruebaexchangerates.ui.InitialScreenViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val moduleUi = module {

            single { InitialScreenViewModel(androidApplication(), get()) }

            single { UseCaseLoadDataForMonth(get()) }

            single { HistoricalRateRepository(get(), get()) }

            single { EuroRatesProvider(get()) }

            single {
                val retrofit: Retrofit = get()
                retrofit.create(FixerApi::class.java)
            }

            single { RetrofitInstance().instance }

            single {
                val database: HistoricalRateExchangeRoomDatabase = get()
                database.contratoRoomDao()
            }
            //single { get<HistoricalRateExchangeRoomDatabase>().contratoRoomDao() }

            single {
                HistoricalRateExchangeRoomDatabase.getInstance(androidContext())
            }
            /*single {
                Room.databaseBuilder(
                    androidApplication(),
                    HistoricalRateExchangeRoomDatabase::class.java,
                    "room_database"
                ).build()
            }*/
        }

        startKoin {

            printLogger()

            androidContext(this@MyApp)

            modules(moduleUi)

        }
    }
}
