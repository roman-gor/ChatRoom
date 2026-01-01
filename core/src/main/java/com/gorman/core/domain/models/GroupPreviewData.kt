package com.gorman.core.domain.models

data class GroupPreviewData(
    val groupId: String? = null,
    val groupName: String? = null,
    val users: List<UsersData?>? = null,
    val lastMessage: MessagesData? = null,
    val unreadQuantity: Int? = 0
)
