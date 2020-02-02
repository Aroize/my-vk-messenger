package ru.rain.ifmo.myvkmessenger.data.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import org.json.JSONObject

data class VKConversation(
    val id: Int = 0,
    var inRead: Int = 0,
    var outRead: Int = 0,
    var unreadCount: Int = 0,
    val chatSettings: ChatSettings? = null,
    var lastMessage: VKMessage? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(ChatSettings::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(inRead)
        parcel.writeInt(outRead)
        parcel.writeInt(unreadCount)
        parcel.writeParcelable(chatSettings, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKConversation> {
        override fun createFromParcel(parcel: Parcel): VKConversation {
            return VKConversation(parcel)
        }

        override fun newArray(size: Int): Array<VKConversation?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject): VKConversation {
            val peer = json.getJSONObject("peer")
            return VKConversation(
            id = peer.optInt("id"),
            inRead = json.optInt("in_read"),
            outRead = json.optInt("out_read"),
            unreadCount = json.optInt("unread_count"),
            chatSettings = if (peer.optString("type") == "chat") {
                ChatSettings.parse(json.getJSONObject("chat_settings"))
            } else
                null
            )
        }
    }

    class ChatSettings(
        var membersCount: Int = 0,
        var title: String = "",
        var state: String = "",
        var photo: String = "")
        : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(membersCount)
            parcel.writeString(title)
            parcel.writeString(state)
            parcel.writeString(photo)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ChatSettings> {
            override fun createFromParcel(parcel: Parcel): ChatSettings {
                return ChatSettings(parcel)
            }

            override fun newArray(size: Int): Array<ChatSettings?> {
                return arrayOfNulls(size)
            }

            fun parse(jsonObject: JSONObject): ChatSettings {
                val photo =
                    if (jsonObject.has("photo"))
                        jsonObject.getJSONObject("photo").optString("photo_200")
                    else
                        ""
                return ChatSettings(
                    jsonObject.optInt("members_count"),
                    jsonObject.optString("title"),
                    jsonObject.optString("state"),
                    photo
                )
            }
        }

    }
}