package com.gorman.chatroom.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatRoomData(
    val chats: Map<String, ChatsData>? = null,
    val groups: Map<String, GroupsData>? = null,
    val messages: Map<String, MessagesData>? = null,
    val users: Map<String, UsersData>? = null
)

@IgnoreExtraProperties
data class ChatsData(
    val isGroup: Boolean? = null,
    val lastMessageId: String? = null,
    val lastMessageTimestamp: Long? = null,
    val members: Map<String, Boolean>? = null
)

@IgnoreExtraProperties
data class GroupsData(
    val admins: Map<String, Boolean>? = null,
    val groupName: String? = null,
    val lastMessageId: String? = null,
    val lastMessageTimestamp: Long? = null,
    val members: Map<String, Boolean>? = null
)

@IgnoreExtraProperties
data class MessagesData(
    val senderId: String? = null,
    val status: Map<String, String>? = null,
    val text: String? = null,
    val timestamp: Long? = null
)

@IgnoreExtraProperties
data class UsersData(
    val username: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val lastSeen: Long? = null,
    val groups: Map<String, Boolean>? = null,
    val chats: Map<String, Boolean>? = null,
    val unreadMessagesCount: Int? = null
)