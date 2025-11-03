package com.gorman.chatroom.domain.models

data class GroupPreviewData(
    val users: List<UsersData?>? = null,
    val lastMessage: MessagesData? = null,
    val unreadQuantity: Int? = 0
)
