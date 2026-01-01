package com.gorman.feature_chats.ui.states

import com.gorman.chatroom.domain.models.ChatPreviewData

sealed class ChatsUiState {
    object Loading: ChatsUiState()
    object Idle: ChatsUiState()
    data class Success(val chats: List<ChatPreviewData>) : ChatsUiState()
    data class Error(val message: String) : ChatsUiState()
}
