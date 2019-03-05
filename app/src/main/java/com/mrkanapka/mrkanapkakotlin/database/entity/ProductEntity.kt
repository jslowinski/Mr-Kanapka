package com.mrkanapka.mrkanapkakotlin.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "products")

data class ProductEntity(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "id_product")
    var id_product: Int,

    @ColumnInfo(name = "id_seller")
    var id_seller: Int,

    @ColumnInfo(name = "photo_url")
    var photo_url: String,

    @ColumnInfo(name = "price")
    var price: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "id_category")
    var id_category: Int

) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}