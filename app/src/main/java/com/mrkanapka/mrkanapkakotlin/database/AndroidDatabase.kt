package com.mrkanapka.mrkanapkakotlin.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mrkanapka.mrkanapkakotlin.database.dao.CategoryDao
import com.mrkanapka.mrkanapkakotlin.database.dao.ProductDao
import com.mrkanapka.mrkanapkakotlin.database.dao.SellerDao
import com.mrkanapka.mrkanapkakotlin.database.dao.TokenDao
import com.mrkanapka.mrkanapkakotlin.database.entity.CategoryEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.SellerEntity
import com.mrkanapka.mrkanapkakotlin.database.entity.TokenEntity


@Database(
    version = 14,
    exportSchema = false,
    entities = [
        ProductEntity::class,
        TokenEntity::class,
        SellerEntity::class,
        CategoryEntity::class
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

    abstract fun productDao(): ProductDao

    abstract fun tokenDao(): TokenDao

    abstract fun sellerDao(): SellerDao

    abstract fun categoryDao(): CategoryDao
    //endregion
}