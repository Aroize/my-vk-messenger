package ru.rain.ifmo.myvkmessenger.domain

import ru.rain.ifmo.myvkmessenger.data.models.VKScreenConversation

interface ConversationUpdateListener {
    fun updateConversation(conversation: VKScreenConversation)
}