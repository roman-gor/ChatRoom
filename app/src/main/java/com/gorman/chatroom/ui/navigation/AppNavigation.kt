package com.gorman.chatroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gorman.chatroom.ui.ui.screens.add.AddFriendScreenEntry
import com.gorman.chatroom.ui.ui.screens.add.CreateGroupScreenEntry
import com.gorman.chatroom.ui.ui.screens.auth.LoginScreenEntry
import com.gorman.chatroom.ui.ui.screens.auth.SignUpScreenEntry
import com.gorman.chatroom.ui.ui.screens.call.CallScreen
import com.gorman.chatroom.ui.ui.screens.main.chats.ChatConversationScreenEntry
import com.gorman.chatroom.ui.ui.screens.main.groups.GroupConversationScreenEntry
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel

@Composable
fun AppNavigation(
    onLangChange: (String) -> Unit,
    mainScreenViewModel: MainScreenViewModel = hiltViewModel()
){
    val navController = rememberNavController()
    val userId = mainScreenViewModel.userId.collectAsStateWithLifecycle().value
    val isUserIdLoaded by mainScreenViewModel.isUserIdLoaded.collectAsStateWithLifecycle()

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
        composable("sign_up") {
            SignUpScreenEntry(onStartClick = {
                navController.navigate("main_screen"){
                    popUpTo("sign_up") { inclusive = true }
                }
            }, onLoginClick = {
                navController.navigate("login"){
                    popUpTo("sign_up") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreenEntry (onStartClick = {
                navController.navigate("main_screen"){
                    popUpTo("login") { inclusive = true }
                }
            }, onSignUpClick = {
                navController.navigate("sign_up"){
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("main_screen") {
            MainScreen(navController = navController, onLangChange = onLangChange)
        }
        Screen.aItems.forEach { aItem ->
            composable(aItem.aRoute) {
                when (aItem) {
                    Screen.AddScreenItem.Friend -> AddFriendScreenEntry (
                        onBack = { navController.popBackStack() },
                        onStartChatClick = {
                            navController.navigate(Screen.ConversationItem.ChatConversation.cRoute + "/$it") {
                                popUpTo("main_screen")
                            }
                        })
                    Screen.AddScreenItem.Group -> CreateGroupScreenEntry(
                        onBack = { navController.popBackStack() },
                        onGroupStart = {
                            navController.navigate(Screen.ConversationItem.GroupConversation.cRoute + "/$it")  {
                                popUpTo("main_screen")
                            }
                        })
                }
            }
        }
        composable(Screen.ConversationItem.ChatConversation.cRoute + "/{map_id}") { backStackEntry ->
            val mapId = backStackEntry.arguments?.getString("map_id") ?: ""
            val restoredMap = mapId.split(";")
                .mapNotNull { it -> it.split("=").takeIf { it.size == 2 } }
                .associate { it[0] to it[1] }
            ChatConversationScreenEntry(
                mapId = restoredMap,
                onPlusClick = {},
                onBackClick = { navController.popBackStack() },
                onNavigateToCall = { id, isVideo ->
                    navController.navigate("${Screen.ConversationItem.CallScreen.cRoute}/$id/$isVideo")
                },
                onMoreClick = {}
            )
        }
        composable(
            route = "${Screen.ConversationItem.CallScreen.cRoute}/{id}/{isVideo}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("isVideo") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val targetId = backStackEntry.arguments?.getString("id") ?: ""
            val isVideoCall = backStackEntry.arguments?.getBoolean("isVideo") ?: false
            CallScreen(
                targetId = targetId,
                isCaller = true,
                isVideoCall = isVideoCall,
                onEndCall = { navController.popBackStack() }
            )
        }
        composable(Screen.ConversationItem.GroupConversation.cRoute + "/{map_id}") { backStackEntry ->
            val mapId = backStackEntry.arguments?.getString("map_id") ?: ""
            val restoredMap = mapId.split(";")
                .mapNotNull { it -> it.split("=").takeIf { it.size == 2 } }
                .associate { it[0] to it[1] }
            GroupConversationScreenEntry(
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