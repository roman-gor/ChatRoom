package com.gorman.chatroom.navigation

import com.gorman.chatroom.R

sealed class AddScreenItem(val route: String, val title: Int, val icon: Int) {
    object Friend: AddScreenItem("add_friend", R.string.add_friend, R.drawable.add_friend)
    object Group: AddScreenItem("add_group", R.string.add_group, R.drawable.add_group)

    companion object {
        val items = listOf<AddScreenItem>(Friend, Group)
    }
}