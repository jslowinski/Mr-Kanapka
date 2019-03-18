package com.mrkanapka.mrkanapkakotlin.database.dao

import android.arch.persistence.room.*
import com.mrkanapka.mrkanapkakotlin.database.entity.ProductEntity
import io.reactivex.Maybe


@Dao
abstract class ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id_category = :category AND id_seller = :id_seller")
    abstract fun removeProducts(category: Int, id_seller: Int)

    @Query("SELECT * FROM products WHERE id_category = :category AND id_seller = :id_seller ORDER BY id_product ASC")
    abstract fun getProducts(category: Int, id_seller: Int): Maybe<List<ProductEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<ProductEntity>, category: Int, id_seller: Int) {
        removeProducts(category, id_seller)
        insert(entities)
    }
}