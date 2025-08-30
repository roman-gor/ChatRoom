package com.gorman.chatroom.data

import com.gorman.chatroom.R

data class PeopleChatsDummyData(
    val id: Int,
    val name: String,
    val avatar: Int,
    val message: String
)

val PeopleChatsList = listOf(
    PeopleChatsDummyData(0, "David WayneDavid WayneDavid Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!Thanks a bunch! Have a great day!Thanks a bunch! Have a great day!"),
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
    )

val avatars = listOf(
    R.drawable.default_avatar,
    R.drawable.default_avatar,
    R.drawable.default_avatar
)
