package com.gorman.chatroom.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.gorman.chatroom.navigation.bottom.BottomNavItem
import com.gorman.chatroom.navigation.bottom.BottomNavigationBar
import com.gorman.chatroom.ui.screens.add.AddFriendScreen
import com.gorman.chatroom.ui.screens.add.CreateGroupScreen
import com.gorman.chatroom.ui.screens.main.ChatsScreen
import com.gorman.chatroom.ui.screens.main.GroupsScreen
import com.gorman.chatroom.ui.screens.main.MoreScreen
import com.gorman.chatroom.ui.screens.main.ProfileScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_graph") {
        navigation(startDestination = BottomNavItem.Chats.route, route = "main_graph"){
            composable("chats"){
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = { TopBar(navController)},
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    ChatsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
            composable("groups"){
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = { TopBar(navController)},
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    GroupsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
            composable("profile"){
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = { TopBar(navController)},
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    ProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
            composable("more"){
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    topBar = { TopBar(navController)},
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    MoreScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
        composable(AddScreenItem.Friend.route){
            AddFriendScreen { navController.popBackStack() }
        }
        composable(AddScreenItem.Group.route){
            CreateGroupScreen { navController.popBackStack() }
        }
    }
}