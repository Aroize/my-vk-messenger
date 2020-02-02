package ru.rain.ifmo.myvkmessenger.data.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKMessage(
    val id: Int = 0,
    val date: Int = 0,
    val peerId: Int = 0,
    val fromId: Int = 0,
    val text: String = "",
    var attachments: Array<out VKAttachment> = emptyArray()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(date)
        parcel.writeInt(peerId)
        parcel.writeInt(fromId)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VKMessage

        if (id != other.id) return false
        if (date != other.date) return false
        if (peerId != other.peerId) return false
        if (fromId != other.fromId) return false
        if (text != other.text) return false
        if (!attachments.contentEquals(other.attachments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + date
        result = 31 * result + peerId
        result = 31 * result + fromId
        result = 31 * result + text.hashCode()
        result = 31 * result + attachments.contentHashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<VKMessage> {
        override fun createFromParcel(parcel: Parcel): VKMessage {
            return VKMessage(parcel)
        }

        override fun newArray(size: Int): Array<VKMessage?> {
            return arrayOfNulls(size)
        }

        fun parse(jsonObject: JSONObject): VKMessage =
            VKMessage(
                id = jsonObject.optInt("id"),
                date = jsonObject.optInt("date"),
                peerId = jsonObject.optInt("peer_id"),
                fromId = jsonObject.optInt("from_id"),
                text = jsonObject.optString("text")
            )
    }
}