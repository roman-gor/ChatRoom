package com.gorman.chatroom.ui.states

import com.gorman.chatroom.domain.models.UsersData

sealed class ProfileUiState {
    object Loading: ProfileUiState()
    object Idle: ProfileUiState()
    data class Success(
        val usersData: UsersData,
        val profileItems: Map<Int, String?>
    ): ProfileUiState()
    data class Error(val e: String): ProfileUiState()
}