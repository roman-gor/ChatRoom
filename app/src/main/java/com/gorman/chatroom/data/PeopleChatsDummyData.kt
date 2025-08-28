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

data class Message(
    val messageId: Int,
    val content: String,
    val time: String,
    val isOwn: Boolean
)

val messagesList = listOf(
    Message(5, "Oh, it's great!", "14:49", false),
    Message(4, "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car" +
            "Today i'll drive a car", "09:26", true),
    Message(3, "I'm fine", "09:26", true),
    Message(2, "How are you?", "09:26", false),
    Message(1, "Hi", "09:25", false),
    Message(0, "Hi", "09:25", true)
    )
