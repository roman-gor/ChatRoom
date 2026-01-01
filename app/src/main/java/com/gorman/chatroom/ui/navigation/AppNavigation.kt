package com.gorman.chatroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gorman.core.R
import com.gorman.feature_chats.ui.screens.add.AddFriendScreenEntry
import com.gorman.chatroom.ui.ui.screens.CreateGroupScreenEntry
import com.gorman.chatroom.ui.ui.screens.auth.LoginScreenEntry
import com.gorman.chatroom.ui.ui.screens.auth.SignUpScreenEntry
import com.gorman.feature_calls.ui.screens.call.CallScreenEntry
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel
import com.gorman.core.ui.components.ErrorLoading
import com.gorman.core.ui.navigation.Destination
import com.gorman.feature_calls.service.CallPermissionsWrapper
import com.gorman.feature_chats.ui.screens.chats.ChatConversationScreenEntry
import com.gorman.feature_chats.ui.screens.groups.GroupConversationScreenEntry

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
            val startDestination = if (userId.isNotEmpty()) Destination.MainGraph else Destination.Login
            navController.navigate(startDestination) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = Destination.LoadingPlaceholder) {
        composable<Destination.LoadingPlaceholder> {}
        composable<Destination.SignUp> {
            SignUpScreenEntry(onStartClick = {
                navController.navigate(Destination.MainGraph){
                    popUpTo(Destination.SignUp) { inclusive = true }
                }
            }, onLoginClick = {
                navController.navigate(Destination.Login){
                    popUpTo(Destination.SignUp) { inclusive = true }
                }
            })
        }
        composable<Destination.Login> {
            LoginScreenEntry (onStartClick = {
                navController.navigate(Destination.MainGraph){
                    popUpTo(Destination.Login) { inclusive = true }
                }
            }, onSignUpClick = {
                navController.navigate(Destination.SignUp){
                    popUpTo(Destination.Login) { inclusive = true }
                }
            })
        }
        composable<Destination.MainGraph> {
            MainScreen(navController = navController, onLangChange = onLangChange)
        }
        composable<Destination.AddFriend> {
            AddFriendScreenEntry (
                onBack = { navController.popBackStack() },
                onStartChatClick = {
                    navController.navigate(Destination.ChatConversation(getterUserId = it)) {
                        popUpTo(Destination.MainGraph)
                    }
                })
        }
        composable<Destination.AddGroup> {
            CreateGroupScreenEntry(
                onBack = { navController.popBackStack() },
                onGroupStart = {
                    navController.navigate(Destination.GroupConversation(
                        groupName = it.groupName,
                        memberList = it.membersList
                    ))  {
                        popUpTo(Destination.MainGraph)
                    }
                })
        }
        composable<Destination.ChatConversation> { backStackEntry ->
            val args = backStackEntry.toRoute<Destination.ChatConversation>()
            ChatConversationScreenEntry(
                currentUserId = userId,
                args = args,
                onPlusClick = {},
                onBackClick = { navController.popBackStack() },
                onNavigateToCall = { id, isVideo ->
                    navController.navigate(Destination.CallScreen(id, isVideo))
                },
                onMoreClick = {}
            )
        }
        composable<Destination.CallScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Destination.CallScreen>()
            CallPermissionsWrapper(
                onPermissionsGranted = {
                    CallScreenEntry(
                        targetId = args.id,
                        isCaller = true,
                        isVideoCall = args.isVideo,
                        onEndCall = { navController.popBackStack() }
                    )
                },
                onPermissionsDenied = {
                    ErrorLoading(
                        text = stringResource(R.string.callPermissionsDenied)
                    )
                }
            )
        }
        composable<Destination.GroupConversation> { backStackEntry ->
            val args = backStackEntry.toRoute<Destination.GroupConversation>()
            GroupConversationScreenEntry(
                args = args,
                currentUserId = userId,
                onVideoClick = {},
                onPlusClick = {},
                onPhoneClick = {},
                onBackClick = { navController.popBackStack() },
                onMoreClick = {}
            )
        }
    }
}
