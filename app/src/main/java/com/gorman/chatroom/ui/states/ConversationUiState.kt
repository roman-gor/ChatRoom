package com.gorman.chatroom.ui.states

import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.ui.navigation.Destination

data class ConversationUiState(
    val getterUser: UsersData?,
    val getterUserId: String?,
    val currentUserId: String?,
    val chatId: String?,
    val sortedMessages: List<MessagesData>,
)