package ru.rain.ifmo.myvkmessenger.data.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.rain.ifmo.myvkmessenger.data.models.VKConversation
import ru.rain.ifmo.myvkmessenger.data.models.VKMessage
import kotlin.math.max

class VKGetConversationsRequest(
   offset: Int = -1,
   count: Int = -1,
   startMessageId: Int = -1
): VKRequest<List<VKConversation>>("messages.getConversations") {
    init {
        if (offset > 0)
            addParam("offset", offset)
        if (count > 0)
            addParam("count", max(count, 200))
        if (startMessageId > 0)
            addParam("start_message_id", startMessageId)
    }

    override fun parse(r: JSONObject): List<VKConversation> {
        val response = r.getJSONObject("response")
        val items = response.getJSONArray("items")
        val result = ArrayList<VKConversation>()
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            val conv = VKConversation.parse(item.getJSONObject("conversation"))
            val message = VKMessage.parse(item.getJSONObject("last_message"))
            result.add(conv.apply { lastMessage = message })
        }
        return result
    }
}