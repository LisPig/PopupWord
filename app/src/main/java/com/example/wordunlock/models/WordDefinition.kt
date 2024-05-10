package com.example.wordunlock.models

import android.os.Parcel
import android.os.Parcelable

data class WordDefinition(
                          val word: String,
                          var uk: String,
                          var us: String, val definition: String) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(word)
        parcel.writeString(uk)
        parcel.writeString(us)
        parcel.writeString(definition)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WordDefinition> {
        override fun createFromParcel(parcel: Parcel): WordDefinition {
            return WordDefinition(parcel)
        }

        override fun newArray(size: Int): Array<WordDefinition?> {
            return arrayOfNulls(size)
        }
    }
}
