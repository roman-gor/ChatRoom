package com.gorman.chatroom.data

import androidx.compose.ui.res.stringResource
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
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
    PeopleChatsDummyData(0, "David Wayne", R.drawable.default_avatar, "Thanks a bunch! Have a great day!"),
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

data class ProfileItems(
    val name: Int,
    val value: String
)

val profileItemsList = listOf(
    ProfileItems(name = R.string.phone, value = "+375296017600"),
    ProfileItems(name = R.string.gender, value = "Мужской"),
    ProfileItems(name = R.string.birthday, value = "14.05.2006"),
    ProfileItems(name = R.string.email, value = "romangorbachev2006@gmail.com")
)
