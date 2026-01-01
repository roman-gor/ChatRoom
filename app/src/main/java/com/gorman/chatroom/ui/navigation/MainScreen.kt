package com.gorman.chatroom.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.ui.ui.screens.main.MoreScreenEntry
import com.gorman.chatroom.ui.ui.screens.main.ProfileScreenEntry
import com.gorman.core.ui.navigation.BottomNavigationBar
import com.gorman.core.ui.navigation.Destination
import com.gorman.feature_chats.ui.screens.chats.ChatsScreenEntry
import com.gorman.feature_chats.ui.screens.groups.GroupsScreenEntry

@Composable
fun MainScreen(navController: NavHostController, onLangChange: (String) -> Unit){
    val nestedNavController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar(navController = navController) },
        bottomBar = {
            BottomNavigationBar(
                items = Destination.bottomNavItems,
                navController = nestedNavController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = Destination.Chats,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Destination.Chats> {
                ChatsScreenEntry(onItemClick = {
                    navController.navigate(Destination.ChatConversation(
                            chatId = it.chatId,
                            getterUserId = it.getterUserId
                        )
                    )
                })
            }
            composable<Destination.Groups> {
                GroupsScreenEntry(onItemClick = {
                    navController.navigate(Destination.GroupConversation(
                        groupId = it.groupId,
                        groupName = it.groupName
                    ))
                })
            }
            composable<Destination.Profile> {
                ProfileScreenEntry(onLogoutClick = {
                    navController.navigate(Destination.Login)
                })
            }
            composable<Destination.More> {
                MoreScreenEntry(onLangChange = onLangChange)
            }
        }
    }
}
