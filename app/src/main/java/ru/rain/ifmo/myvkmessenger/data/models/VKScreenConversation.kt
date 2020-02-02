package ru.rain.ifmo.myvkmessenger.data.models

data class VKScreenConversation(
    val conversation: VKConversation,
    var photo: String = "",
    var title: String = ""
)