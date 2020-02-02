package ru.rain.ifmo.myvkmessenger.data.models

import android.os.Parcel
import android.os.Parcelable

class VKAttachment() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKAttachment> {
        override fun createFromParcel(parcel: Parcel): VKAttachment {
            return VKAttachment(parcel)
        }

        override fun newArray(size: Int): Array<VKAttachment?> {
            return arrayOfNulls(size)
        }
    }

}
