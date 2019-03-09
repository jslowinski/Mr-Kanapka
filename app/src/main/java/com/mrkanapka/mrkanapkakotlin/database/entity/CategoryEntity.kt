package com.mrkanapka.mrkanapkakotlin.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "category")

data class CategoryEntity(

    @ColumnInfo(name = "id_category")
    var id_category: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "id_seller")
    var id_seller: Int

)
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
