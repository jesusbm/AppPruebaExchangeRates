package com.pruebaexchangerates.data.database

import android.util.Log
import androidx.room.Room
import androidx.room.Database
import android.content.Context
import androidx.room.RoomDatabase
import com.pruebaexchangerates.data.dao.HistoricalRateDao
import com.pruebaexchangerates.data.entity.HistoricalRateEntity

@Database(
    entities = [
        HistoricalRateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HistoricalRateExchangeRoomDatabase : RoomDatabase() {

    abstract fun contratoRoomDao(): HistoricalRateDao

    companion object {

        private val sTAG = HistoricalRateExchangeRoomDatabase::class.java.simpleName

        private val DB_NAME = "HistoricalRateExchangeDB"
        private val DB_CLASS = HistoricalRateExchangeRoomDatabase::class.java

        @Volatile
        private var INSTANCE: HistoricalRateExchangeRoomDatabase? = null

        fun getInstance(context: Context): HistoricalRateExchangeRoomDatabase {

            val path = context.getDatabasePath(DB_NAME)
            Log.e(sTAG, "PATH: $path")

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): HistoricalRateExchangeRoomDatabase {
            val databaseBuilder =
                Room.databaseBuilder(context.applicationContext, DB_CLASS, DB_NAME)
            return databaseBuilder.run {
                build()
            }
        }
    }
}


/*
* https://github.com/commonsguy/cwac-saferoom
* https://commonsware.com/AndroidArch/
* */
