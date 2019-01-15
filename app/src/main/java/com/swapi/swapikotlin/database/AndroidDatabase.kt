package com.swapi.swapikotlin.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.swapi.swapikotlin.database.dao.FilmDao
import com.swapi.swapikotlin.database.entity.FilmEntity


@Database(
    version = 1,
    exportSchema = false,
    entities = [
        FilmEntity::class
    ]
)
abstract class AndroidDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "swapi_db"

        lateinit var database: AndroidDatabase
            private set

        fun initialize(context: Context) {
            database = Room
                .databaseBuilder(context, AndroidDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

    }

    //region Dao

    abstract fun filmDao(): FilmDao

    //endregion
}