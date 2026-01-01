package com.gorman.core.domain.models

data class MessagesData(
    val messageId: String? = null,
    val senderId: String? = null,
    val status: Map<String, String>? = null,
    val text: String? = null,
    val timestamp: String? = null
)
