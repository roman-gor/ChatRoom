package com.gorman.chatroom.data

sealed class MessengerUiState {
    object Loading: MessengerUiState()
    object Success: MessengerUiState()
    data class Error(val error: String): MessengerUiState()
}