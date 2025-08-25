package com.gorman.chatroom.navigation.bottom

import com.gorman.chatroom.R

sealed class BottomNavItem (val route: String, val name: Int, val icon: Int) {
    object Chats: BottomNavItem("chats", R.string.chats_title, R.drawable.chats)
    object Groups: BottomNavItem("groups", R.string.groups_title, R.drawable.groups)
    object Profile: BottomNavItem("profile", R.string.profile_title, R.drawable.profile)
    object More: BottomNavItem("more", R.string.more_title, R.drawable.more)

    companion object {
        val items = listOf<BottomNavItem>(Chats, Groups, Profile, More)
    }
}