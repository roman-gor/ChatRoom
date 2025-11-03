package com.gorman.chatroom.domain.models

data class ChatsData(
    val chatId: String? = null,
    val isGroup: Boolean? = null,
    val lastMessageId: String? = null,
    val lastMessageTimestamp: String? = null,
    val members: Map<String, Boolean>? = null
)
