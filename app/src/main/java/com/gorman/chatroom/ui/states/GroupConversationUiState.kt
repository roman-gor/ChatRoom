package com.gorman.chatroom.ui.states

import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData

data class GroupConversationUiState(
    val mapId: Map<String, String>,
    val userMap: Map<String?, UsersData>,
    val getterUsers: List<UsersData?>,
    val sortedMessages: List<MessagesData>,
    val currentUserId: String?,
)
