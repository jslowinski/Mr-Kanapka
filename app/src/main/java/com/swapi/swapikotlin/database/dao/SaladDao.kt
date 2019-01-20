package com.swapi.swapikotlin.database.dao

import android.arch.persistence.room.*
import com.swapi.swapikotlin.database.entity.SaladEntity
import io.reactivex.Maybe


@Dao
abstract class SaladDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<SaladEntity>)

    @Query("DELETE FROM salads")
    abstract fun removeAll()

    @Query("SELECT * FROM salads ORDER BY id_product ASC")
    abstract fun getSalads(): Maybe<List<SaladEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<SaladEntity>) {
        removeAll()
        insert(entities)
    }
}