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

    // 1
    companion object {
        // 2
        inline fun <reified T : Parcelable> createParcel(
            crossinline createFromParcel: (Parcel) -> T?
        ): Parcelable.Creator<T> =
            object : Parcelable.Creator<T> {
                override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
                override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
            }

        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { CategoryDto(it) } // 3
    }

    // 4
    protected constructor(parcelIn: Parcel) : this(
        parcelIn.readInt(),
        parcelIn.readString()

    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id_category)
        dest.writeString(name)
    }

    override fun describeContents() = 0
}