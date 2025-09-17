package com.gorman.chatroom.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.presentation.ui.screens.main.chats.ChatsScreen
import com.gorman.chatroom.presentation.ui.screens.main.groups.GroupsScreen
import com.gorman.chatroom.presentation.ui.screens.main.MoreScreen
import com.gorman.chatroom.presentation.ui.screens.main.ProfileScreen

@Composable
fun MainScreen(navController: NavHostController, onLangChange: (String) -> Unit){
    val nestedNavController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = { TopBar(navController = navController) },
        bottomBar = {
            BottomNavigationBar(
                items = Screen.bItems,
                navController = nestedNavController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = Screen.BottomNavItem.Chats.bRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            Screen.bItems.forEach { bItem ->
                composable(bItem.bRoute) {
                    when (bItem) {
                        Screen.BottomNavItem.Chats -> ChatsScreen(onItemClick = {mapId ->
                            navController.navigate(Screen.ConversationItem.ChatConversation.cRoute + "/$mapId")
                        })
                        Screen.BottomNavItem.Groups -> GroupsScreen(onItemClick = {mapId->
                            navController.navigate(Screen.ConversationItem.GroupConversation.cRoute + "/$mapId")
                        })
                        Screen.BottomNavItem.Profile -> ProfileScreen(onLogoutClick = {
                            navController.navigate("login")
                        })
                        Screen.BottomNavItem.More -> MoreScreen(onLangChange = onLangChange)
                    }
                }
            }
        }
    }
}