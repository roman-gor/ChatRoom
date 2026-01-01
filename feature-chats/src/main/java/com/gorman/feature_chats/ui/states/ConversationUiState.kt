package com.gorman.feature_chats.ui.states

import com.gorman.core.domain.models.MessagesData
import com.gorman.core.domain.models.UsersData

data class ConversationUiState(
    val getterUser: UsersData?,
    val getterUserId: String?,
    val currentUserId: String?,
    val chatId: String?,
    val sortedMessages: List<MessagesData>,
)
