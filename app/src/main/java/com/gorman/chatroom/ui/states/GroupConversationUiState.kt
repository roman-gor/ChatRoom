package com.gorman.chatroom.ui.states

import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.ui.navigation.Destination

data class GroupConversationUiState(
    val args: Destination.GroupConversation,
    val userMap: Map<String?, UsersData>,
    val getterUsers: List<UsersData?>,
    val sortedMessages: List<MessagesData>,
    val currentUserId: String?,
)
