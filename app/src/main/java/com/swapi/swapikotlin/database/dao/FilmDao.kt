package com.swapi.swapikotlin.database.dao

import android.arch.persistence.room.*
import com.swapi.swapikotlin.database.entity.FilmEntity
import io.reactivex.Maybe

@Dao
abstract class FilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<FilmEntity>)

    @Query("DELETE FROM films")
    abstract fun removeAll()

    @Query("SELECT * FROM films ORDER BY episode_id ASC")
    abstract fun getFilms(): Maybe<List<FilmEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<FilmEntity>) {
        removeAll()
        insert(entities)
    }
}