package com.gorman.chatroom.domain.models

data class MessageUiModel(
    val message: MessagesData,
    val currentUserId: String,
    val isFirstMessage: Boolean,
    val isLastMessage: Boolean,
    val senderName: String,
    val isGroup: Boolean
)
