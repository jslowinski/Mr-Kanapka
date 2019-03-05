package com.mrkanapka.mrkanapkakotlin.database.dao

import android.arch.persistence.room.*
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import io.reactivex.Maybe


@Dao
abstract class ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id_category = :category ")
    abstract fun removeProducts(category: Int)

    @Query("SELECT * FROM products WHERE id_category = :category ORDER BY id_product ASC")
    abstract fun getProducts(category: Int): Maybe<List<ProductEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<ProductEntity>, category: Int) {
        removeProducts(category)
        insert(entities)
    }
}