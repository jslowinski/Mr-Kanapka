package com.mrkanapka.mrkanapkakotlin.database.dao

import android.arch.persistence.room.*
import com.mrkanapka.mrkanapkakotlin.database.entity.JuiceEntity
import io.reactivex.Maybe


@Dao
abstract class JuiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<JuiceEntity>)

    @Query("DELETE FROM juices")
    abstract fun removeAll()

    @Query("SELECT * FROM juices ORDER BY id_product ASC")
    abstract fun getJuices(): Maybe<List<JuiceEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<JuiceEntity>) {
        removeAll()
        insert(entities)
    }
}