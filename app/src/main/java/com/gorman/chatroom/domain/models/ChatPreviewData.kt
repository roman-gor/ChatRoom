package com.gorman.chatroom.domain.models

data class ChatPreviewData(
    val chatId: String? = null,
    val user: UsersData? = null,
    val lastMessage: MessagesData? = null,
    val unreadQuantity: Int = 0
)
