package ru.rain.ifmo.myvkmessenger.presentation.view

import ru.rain.ifmo.myvkmessenger.data.models.VKScreenConversation
import ru.rain.ifmo.myvkmessenger.domain.mvp.MvpView

interface ConversationView: MvpView {
    fun setConversations(conversations: List<VKScreenConversation>)
}