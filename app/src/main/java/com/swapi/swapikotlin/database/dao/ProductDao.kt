package com.swapi.swapikotlin.database.dao

import android.arch.persistence.room.*
import com.swapi.swapikotlin.database.entity.ProductEntity
import io.reactivex.Maybe


@Dao
abstract class ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<ProductEntity>)

    @Query("DELETE FROM products")
    abstract fun removeAll()

    @Query("SELECT * FROM products ORDER BY id_product ASC")
    abstract fun getFilms(): Maybe<List<ProductEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<ProductEntity>) {
        removeAll()
        insert(entities)
    }
}