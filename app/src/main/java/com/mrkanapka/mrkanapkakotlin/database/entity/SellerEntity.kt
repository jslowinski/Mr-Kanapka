package com.mrkanapka.mrkanapkakotlin.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "sellers")

data class SellerEntity(

    @ColumnInfo(name = "id_seller")
    var id_seller: Int,

    @ColumnInfo(name = "sellername")
    var sellername: String,

    @ColumnInfo(name = "id_destination")
    var id_destination: Int

)
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
