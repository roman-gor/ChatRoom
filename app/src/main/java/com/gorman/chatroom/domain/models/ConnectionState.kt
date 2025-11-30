package com.gorman.chatroom.domain.models

enum class ConnectionState {
    Active,
    Creating,
    Ready,
    Impossible,
    Offline
}