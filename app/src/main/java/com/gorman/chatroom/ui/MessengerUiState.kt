package com.gorman.chatroom.ui

sealed class MessengerUiState {
    object Loading: MessengerUiState()
    object Success: MessengerUiState()
    object Login: MessengerUiState()
    data class Error(val error: String): MessengerUiState()
}