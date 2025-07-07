package com.piggywuwuwu.healingyukslayy

import android.os.Parcel
import android.os.Parcelable

data class HealingLocation(
    val id: String,
    val name: String,
    val category: String,
    val photoUrl: String,
    val shortDescription: String,
    val fullDescription: String,
    var isFavorite: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    constructor(
        id: Int,
        name: String?,
        category_name: String?,
        photo_url: String?,
        short_description: String?,
        full_description: String?
    ) : this(
        id.toString(),
        name ?: "Unknown",
        category_name ?: "Uncategorized",
        photo_url ?: "",
        short_description ?: "",
        full_description ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(photoUrl)
        parcel.writeString(shortDescription)
        parcel.writeString(fullDescription)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<HealingLocation> {
        override fun createFromParcel(parcel: Parcel): HealingLocation {
            return HealingLocation(parcel)
        }

        override fun newArray(size: Int): Array<HealingLocation?> {
            return arrayOfNulls(size)
        }
    }
}