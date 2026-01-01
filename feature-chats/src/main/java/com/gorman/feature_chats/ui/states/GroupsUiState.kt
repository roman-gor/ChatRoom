package com.gorman.feature_chats.ui.states

import com.gorman.chatroom.domain.models.GroupPreviewData

sealed class GroupsUiState {
    object Loading: GroupsUiState()
    object Idle: GroupsUiState()
    data class Success(val groups: List<GroupPreviewData>): GroupsUiState()
    data class Error(val message: String): GroupsUiState()
}
