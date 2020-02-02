package ru.rain.ifmo.myvkmessenger.data.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.rain.ifmo.myvkmessenger.data.models.VKLongPollServer

class VKGetLongPollServerRequest
    : VKRequest<VKLongPollServer>("messages.getLongPollServer"){
    init {
        addParam("lp_version", 3)
    }

    override fun parse(r: JSONObject): VKLongPollServer {
        val response = r.getJSONObject("response")
        val key = response.getString("key")
        val server = response.getString("server")
        val ts = response.getInt("ts")
        return VKLongPollServer(key, server, ts)
    }
}