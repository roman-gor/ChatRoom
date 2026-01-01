package com.gorman.feature_chats.ui.states

import com.gorman.core.domain.models.MessagesData
import com.gorman.core.domain.models.UsersData
import com.gorman.core.ui.navigation.Destination

data class GroupConversationUiState(
    val args: Destination.GroupConversation,
    val userMap: Map<String?, UsersData>,
    val getterUsers: List<UsersData?>,
    val sortedMessages: List<MessagesData>,
    val currentUserId: String?,
)
