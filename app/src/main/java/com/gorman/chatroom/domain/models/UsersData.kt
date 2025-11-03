package com.gorman.chatroom.domain.models

data class UsersData(
    val username: String? = null,
    val birthday: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val profileImageUrl: String? = null,
    val lastSeen: String? = null,
    val groups: Map<String, Boolean>? = null,
    val chats: Map<String, Boolean>? = null,
    val userId: String? = null,
    val unreadMessagesCount: Int? = null
)