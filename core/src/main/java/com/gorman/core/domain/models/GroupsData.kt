package com.gorman.core.domain.models

data class GroupsData(
    val groupId: String? = null,
    val admins: Map<String, Boolean>? = null,
    val groupName: String? = null,
    val lastMessageId: String? = null,
    val lastMessageTimestamp: String? = null,
    val members: Map<String, Boolean>? = null
)
