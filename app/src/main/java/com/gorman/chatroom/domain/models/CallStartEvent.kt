package com.gorman.chatroom.domain.models

data class CallStartEvent(
    val targetId: String,
    val isVideoCall: Boolean
)
