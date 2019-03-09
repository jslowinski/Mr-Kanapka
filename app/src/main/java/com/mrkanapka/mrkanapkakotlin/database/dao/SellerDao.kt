package com.mrkanapka.mrkanapkakotlin.database.dao

import android.arch.persistence.room.*
import com.mrkanapka.mrkanapkakotlin.database.entity.SellerEntity
import io.reactivex.Maybe


@Dao
abstract class SellerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<SellerEntity>)

    @Query("DELETE FROM sellers WHERE id_destination = :destination ")
    abstract fun removeSellers(destination: Int)

    @Query("SELECT * FROM sellers WHERE id_destination = :destination ORDER BY id_seller ASC")
    abstract fun getSellers(destination: Int): Maybe<List<SellerEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<SellerEntity>, destination: Int) {
        removeSellers(destination)
        insert(entities)
    }
}