package com.mrkanapka.mrkanapkakotlin.database.dao

import android.arch.persistence.room.*
import com.mrkanapka.mrkanapkakotlin.database.entity.CategoryEntity
import io.reactivex.Maybe


@Dao
abstract class CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<CategoryEntity>)

    @Query("DELETE FROM category WHERE id_seller = :seller ")
    abstract fun removeCategory(seller: Int)

    @Query("SELECT * FROM category WHERE id_seller = :seller ORDER BY id_category ASC")
    abstract fun getCategory(seller: Int): Maybe<List<CategoryEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<CategoryEntity>, seller: Int) {
        removeCategory(seller)
        insert(entities)
    }
}