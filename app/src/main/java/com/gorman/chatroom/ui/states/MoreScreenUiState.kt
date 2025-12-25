package com.gorman.chatroom.ui.states

data class MoreScreenUiState(
    val language: String = "en",
    val isDarkMode: Boolean = false,
    val isNotificationsMuted: Boolean = false,
    val isChatHistoryHidden: Boolean = false,
    val isSecurityEnabled: Boolean = false
)
