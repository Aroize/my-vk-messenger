package ru.rain.ifmo.myvkmessenger.domain.viewholders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.rain.ifmo.myvkmessenger.App
import ru.rain.ifmo.myvkmessenger.R
import ru.rain.ifmo.myvkmessenger.data.models.VKMessage
import ru.rain.ifmo.myvkmessenger.data.models.VKScreenConversation

class ConversationViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val userId = App.token!!.userId

    private val mainView = view

    private val avatar = mainView.findViewById<ImageView>(R.id.avatar)

    private val title = mainView.findViewById<TextView>(R.id.interlocutor)

    private val lastMessage = mainView.findViewById<TextView>(R.id.last_message)

    fun bind(conversation: VKScreenConversation) {
        if (conversation.photo.isNotBlank()) {
            Picasso.get()
                .load(conversation.photo)
                .into(avatar)
        } else
            Picasso.get()
                .load("https://vk.com/images/camera_200.png")
                .into(avatar)
        title.text = conversation.title
        val vkLastMessage = conversation.conversation.lastMessage as VKMessage
        lastMessage.text = vkLastMessage.text
        if (vkLastMessage.fromId == userId) {
            lastMessage.setTextColor(Color.BLUE)
        }
    }
}