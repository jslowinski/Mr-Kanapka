package com.mrkanapka.mrkanapkakotlin.api.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CategoryDto(

    @SerializedName("id_category")
    var id_category: Int,

    @SerializedName("name")
    var name: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_category)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<CategoryDto>
        {
            override fun createFromParcel(parcel: Parcel): CategoryDto {
                return CategoryDto(parcel)
            }

            override fun newArray(size: Int): Array<CategoryDto?> {
                return arrayOfNulls(size)
            }
        }
    }
}