package com.solooxy.popupword.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteItem(@PrimaryKey(autoGenerate = true)
                        val id: Int?,
                        val word: String?,
                        val uk: String?,
                        val us: String?,
                        val definition: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(word)
        parcel.writeString(uk)
        parcel.writeString(us)
        parcel.writeString(definition)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun isNotEmpty(): Boolean {
        return word?.isNotEmpty() == true
    }

    companion object CREATOR : Parcelable.Creator<FavoriteItem> {
        override fun createFromParcel(parcel: Parcel): FavoriteItem {
            return FavoriteItem(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteItem?> {
            return arrayOfNulls(size)
        }
    }
}
