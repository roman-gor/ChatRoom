package com.gorman.chatroom.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.ui.screens.add.AddFriendScreen
import com.gorman.chatroom.ui.screens.add.CreateGroupScreen
import com.gorman.chatroom.ui.screens.main.chats.ChatConversationScreen

@Composable
fun AppNavigation(onLangChange: (String) -> Unit){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_screen") {

        composable("main_screen") {
            MainScreen(navController = navController, onLangChange = onLangChange)
        }
        Screen.aItems.forEach { aItem ->
            composable(aItem.aRoute) {
                when (aItem) {
                    Screen.AddScreenItem.Friend -> AddFriendScreen { navController.popBackStack() }
                    Screen.AddScreenItem.Group -> CreateGroupScreen { navController.popBackStack() }
                }
            }
        }
        composable(Screen.ConversationItem.ChatConversation.cRoute + "/{map_id}") { backStackEntry ->
            val mapId = backStackEntry.arguments?.getString("map_id") ?: ""
            val restoredMap = mapId.split(";")
                .mapNotNull { it -> it.split("=").takeIf { it.size == 2 } }
                .associate { it[0] to it[1] }
            ChatConversationScreen(
                mapId = restoredMap,
                onVideoClick = {},
                onPlusClick = {},
                onPhoneClick = {},
                onBackClick = { navController.popBackStack() },
                onMoreClick = {}
            )
        }
    }
}