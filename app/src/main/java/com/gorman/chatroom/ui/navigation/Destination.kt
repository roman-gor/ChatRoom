package com.gorman.chatroom.ui.navigation

import com.gorman.chatroom.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {

    interface NavItem {
        val icon: Int
        val title: Int
    }

    @Serializable object LoadingPlaceholder: Destination()
    @Serializable object Login: Destination()
    @Serializable object SignUp: Destination()
    @Serializable object MainGraph: Destination()

    @Serializable object Chats: Destination(), NavItem {
        override val icon = R.drawable.chats
        override val title = R.string.chats_title
    }

    @Serializable object Groups: Destination(), NavItem {
        override val icon = R.drawable.groups
        override val title = R.string.groups_title
    }

    @Serializable object Profile: Destination(), NavItem {
        override val icon = R.drawable.profile
        override val title = R.string.profile_title
    }

    @Serializable object More: Destination(), NavItem {
        override val icon = R.drawable.more
        override val title = R.string.more_title
    }

    @Serializable object AddFriend: Destination(), NavItem {
        override val icon = R.drawable.add_friend
        override val title = R.string.add_friend
    }
    @Serializable object AddGroup: Destination(), NavItem {
        override val icon = R.drawable.add_group
        override val title = R.string.add_group
    }

    @Serializable data class ChatConversation(
        val chatId: String? = null,
        val getterUserId: String
    ): Destination()
    @Serializable data class GroupConversation(
        val groupId: String? = null,
        val groupName: String,
        val memberList: String? = null
    ): Destination()
    @Serializable data class CallScreen(
        val id: String,
        val isVideo: Boolean
    ): Destination()

    companion object {
        val bottomNavItems: List<NavItem> = listOf(Chats, Groups, Profile, More)
        val addItems: List<NavItem> = listOf(AddFriend, AddGroup)
    }

}
