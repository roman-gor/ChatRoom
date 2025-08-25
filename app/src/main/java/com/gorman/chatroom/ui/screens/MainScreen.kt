package com.gorman.chatroom.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.navigation.bottom.BottomNavItem
import com.gorman.chatroom.navigation.bottom.BottomNavigationBar
import com.gorman.chatroom.navigation.TopBar
import com.gorman.chatroom.ui.screens.add.AddFriendScreen
import com.gorman.chatroom.ui.screens.main.ChatsScreen
import com.gorman.chatroom.ui.screens.main.GroupsScreen
import com.gorman.chatroom.ui.screens.main.MoreScreen
import com.gorman.chatroom.ui.screens.main.ProfileScreen

@Composable
fun MainScreen(){
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = { TopBar(navController)},
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(navController = navController,
            startDestination = BottomNavItem.Chats.route,
            modifier = Modifier.padding(innerPadding)) {
            composable(BottomNavItem.Chats.route){ ChatsScreen() }
            composable(BottomNavItem.Groups.route){ GroupsScreen() }
            composable(BottomNavItem.Profile.route){ ProfileScreen() }
            composable(BottomNavItem.More.route){ MoreScreen() }
        }
    }
}