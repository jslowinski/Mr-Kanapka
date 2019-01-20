package com.swapi.swapikotlin.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.swapi.swapikotlin.database.dao.ProductDao
import com.swapi.swapikotlin.database.dao.SaladDao
import com.swapi.swapikotlin.database.entity.ProductEntity
import com.swapi.swapikotlin.database.entity.SaladEntity


@Database(
    version = 5,
    exportSchema = false,
    entities = [
        ProductEntity::class,
        SaladEntity::class
    ]
)
abstract class AndroidDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "mrkanapka_db"

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

    abstract fun filmDao(): ProductDao

    abstract fun saladDao(): SaladDao

    //endregion
}