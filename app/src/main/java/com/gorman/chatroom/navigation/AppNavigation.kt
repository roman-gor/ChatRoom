package com.gorman.chatroom.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.screens.add.AddFriendScreen
import com.gorman.chatroom.ui.screens.add.CreateGroupScreen
import com.gorman.chatroom.ui.screens.auth.LoginScreen
import com.gorman.chatroom.ui.screens.main.chats.ChatConversationScreen
import com.gorman.chatroom.viewmodel.MainScreenViewModel

@Composable
fun AppNavigation(onLangChange: (String) -> Unit){
    val navController = rememberNavController()
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    val userId = mainScreenViewModel.userId.collectAsState().value
    val isUserIdLoaded by mainScreenViewModel.isUserIdLoaded.collectAsState()

    LaunchedEffect(isUserIdLoaded) {
        if (isUserIdLoaded) {
            val startDestination = if (userId.isNotEmpty()) "main_screen" else "login"
            navController.navigate(startDestination) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "loading_placeholder") {
        composable("loading_placeholder") {}
        composable("login") {
            LoginScreen {
                navController.navigate("main_screen"){
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable("main_screen") {
            MainScreen(navController = navController, onLangChange = onLangChange)
        }
        Screen.aItems.forEach { aItem ->
            composable(aItem.aRoute) {
                when (aItem) {
                    Screen.AddScreenItem.Friend -> AddFriendScreen (
                        onBack = { navController.popBackStack() },
                        onStartChatClick = {it->
                            navController.navigate(Screen.ConversationItem.ChatConversation.cRoute + "/$it") {
                                popUpTo("main_screen")
                            }
                        })
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