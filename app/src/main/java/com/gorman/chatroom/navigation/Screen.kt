package com.gorman.chatroom.navigation

import com.gorman.chatroom.R

sealed class Screen(){
    sealed class BottomNavItem (val bRoute: String, val bTitle: Int, val bIcon: Int)
        : Screen () {
        object Chats : BottomNavItem("chats", R.string.chats_title, R.drawable.chats)
        object Groups : BottomNavItem("groups", R.string.groups_title, R.drawable.groups)
        object Profile : BottomNavItem("profile", R.string.profile_title, R.drawable.profile)
        object More : BottomNavItem("more", R.string.more_title, R.drawable.more)
    }

    sealed class AddScreenItem(val aRoute: String, val aTitle: Int, val aIcon: Int)
        : Screen () {
        object Friend: AddScreenItem("add_friend", R.string.add_friend, R.drawable.add_friend)
        object Group: AddScreenItem("add_group", R.string.add_group, R.drawable.add_group)
    }

    sealed class ConversationItem(val cRoute: String)
        : Screen () {
            object ChatConversation: ConversationItem("chat_conversation")
        }

    companion object {
        val bItems = listOf<BottomNavItem>(
            BottomNavItem.Chats,
            BottomNavItem.Groups,
            BottomNavItem.Profile,
            BottomNavItem.More)

        val aItems = listOf<AddScreenItem>(
            AddScreenItem.Friend,
            AddScreenItem.Group)
    }
}