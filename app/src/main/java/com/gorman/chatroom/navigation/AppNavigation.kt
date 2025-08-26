package com.gorman.chatroom.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.ui.screens.add.AddFriendScreen
import com.gorman.chatroom.ui.screens.add.CreateGroupScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main_screen") {

        composable("main_screen") {
            MainScreen(navController = navController)
        }
        Screen.aItems.forEach { aItem ->
            composable(aItem.aRoute) {
                when (aItem) {
                    Screen.AddScreenItem.Friend -> AddFriendScreen(
                        navController = navController,
                        onBack =  { navController.popBackStack() })
                    Screen.AddScreenItem.Group -> CreateGroupScreen { navController.popBackStack() }
                }
            }
        }
    }
}