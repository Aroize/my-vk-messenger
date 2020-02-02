package ru.rain.ifmo.myvkmessenger.data.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKUser(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val photo: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(photo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKUser> {
        override fun createFromParcel(parcel: Parcel): VKUser {
            return VKUser(parcel)
        }

        override fun newArray(size: Int): Array<VKUser?> {
            return arrayOfNulls(size)
        }

        fun parse(jsonObject: JSONObject) = VKUser(
            id = jsonObject.optInt("id"),
            firstName = jsonObject.optString("first_name"),
            lastName = jsonObject.optString("last_name"),
            photo = jsonObject.optString("photo_200")
        )
    }
}