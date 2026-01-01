package com.gorman.core.domain.models

data class CallStartEvent(
    val targetId: String,
    val isVideoCall: Boolean
)
