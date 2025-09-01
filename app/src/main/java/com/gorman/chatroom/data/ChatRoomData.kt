package com.gorman.chatroom.data

import com.google.firebase.database.IgnoreExtraProperties
import com.gorman.chatroom.R

@IgnoreExtraProperties
data class ChatRoomData(
    val chats: Map<String, ChatsData>? = null,
    val groups: Map<String, GroupsData>? = null,
    val messages: Map<String, MessagesData>? = null,
    val users: Map<String, UsersData>? = null
)

@IgnoreExtraProperties
data class ChatsData(
    val chatId: String? = null,
    val isGroup: Boolean? = null,
    val lastMessageId: String? = null,
    val lastMessageTimestamp: String? = null,
    val members: Map<String, Boolean>? = null
)

@IgnoreExtraProperties
data class GroupsData(
    val admins: Map<String, Boolean>? = null,
    val groupName: String? = null,
    val lastMessageId: String? = null,
    val lastMessageTimestamp: String? = null,
    val members: Map<String, Boolean>? = null
)

@IgnoreExtraProperties
data class MessagesData(
    val messageId: String? = null,
    val senderId: String? = null,
    val status: Map<String, String>? = null,
    val text: String? = null,
    val timestamp: String? = null
)

@IgnoreExtraProperties
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

data class ChatPreviewData(
    val user: UsersData? = null,
    val lastMessage: MessagesData? = null,
    val unreadQuantity: Int = 0
)

data class Flag(
    val flagImage: Int,
    val flagCountryName: Int,
    val phoneCode: String
)

data class ChatSettingsItem(
    val icon: Int,
    val title: Int
)

val flagsList = listOf(
    Flag(R.drawable.belarus, R.string.belarus, "+375"),
    Flag(R.drawable.united_states_of_america, R.string.usa, "+1")
)

val settingsList = listOf(
    ChatSettingsItem(R.drawable.delete_icon, R.string.delete_chat)
)