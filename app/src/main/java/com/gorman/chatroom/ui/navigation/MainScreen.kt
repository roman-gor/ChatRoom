package com.gorman.chatroom.ui.navigation

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
import com.gorman.chatroom.ui.ui.screens.main.chats.ChatsScreenEntry
import com.gorman.chatroom.ui.ui.screens.main.groups.GroupsScreenEntry
import com.gorman.chatroom.ui.ui.screens.main.MoreScreenEntry
import com.gorman.chatroom.ui.ui.screens.main.ProfileScreenEntry

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
                        Screen.BottomNavItem.Chats -> ChatsScreenEntry(onItemClick = { mapId ->
                            navController.navigate(Screen.ConversationItem.ChatConversation.cRoute + "/$mapId")
                        })
                        Screen.BottomNavItem.Groups -> GroupsScreenEntry(onItemClick = { mapId->
                            navController.navigate(Screen.ConversationItem.GroupConversation.cRoute + "/$mapId")
                        })
                        Screen.BottomNavItem.Profile -> ProfileScreenEntry(onLogoutClick = {
                            navController.navigate("login")
                        })
                        Screen.BottomNavItem.More -> MoreScreenEntry(onLangChange = onLangChange)
                    }
                }
            }
        }
    }
}