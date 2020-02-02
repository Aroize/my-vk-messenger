package ru.rain.ifmo.myvkmessenger.data.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.rain.ifmo.myvkmessenger.data.models.VKUser
import java.util.ArrayList

class VKUsersGetRequest(
    uid: IntArray = intArrayOf(),
    fields: Array<String> = emptyArray()): VKRequest<List<VKUser>>("users.get") {

    init {
        if (uid.isNotEmpty()) {
            addParam("user_ids", uid)
        }
        if (fields.isNotEmpty()) {
            addParam("fields", fields)
        }
    }

    override fun parse(r: JSONObject): List<VKUser> {
        val response = r.getJSONArray("response")
        val result = ArrayList<VKUser>()
        for (i in 0 until response.length()) {
            result.add(VKUser.parse(response.getJSONObject(i)))
        }
        return result
    }
}