package com.mrkanapka.mrkanapkakotlin.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mrkanapka.mrkanapkakotlin.database.dao.JuiceDao
import com.mrkanapka.mrkanapkakotlin.database.dao.ProductDao
import com.mrkanapka.mrkanapkakotlin.database.dao.SaladDao
import com.mrkanapka.mrkanapkakotlin.database.entity.JuiceEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.SaladEntity


@Database(
    version = 6,
    exportSchema = false,
    entities = [
        ProductEntity::class,
        SaladEntity::class,
        JuiceEntity::class
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

    abstract fun sandwichDao(): ProductDao

    abstract fun saladDao(): SaladDao

    abstract fun juiceDao(): JuiceDao

    //endregion
}