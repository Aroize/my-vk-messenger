package ru.rain.ifmo.myvkmessenger.data.models

import android.os.Parcel
import android.os.Parcelable

data class VKLongPollServer(
    val key: String = "",
    val server: String = "",
    var ts: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(server)
        parcel.writeInt(ts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKLongPollServer> {
        override fun createFromParcel(parcel: Parcel): VKLongPollServer {
            return VKLongPollServer(parcel)
        }

        override fun newArray(size: Int): Array<VKLongPollServer?> {
            return arrayOfNulls(size)
        }
    }
}