package com.gorman.chatroom.navigation.bottom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gorman.chatroom.R

@Composable
fun BottomNavigationBar(navController: NavHostController){
    val items = BottomNavItem.items
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Card (
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ){
        NavigationBar (
            containerColor = Color.Transparent
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(item.icon), contentDescription = stringResource(item.name)) },
                    label = { Text(stringResource(item.name)) },
                    selected = currentRoute == item.route,
                    colors = NavigationBarItemColors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.Black,
                        selectedIndicatorColor = colorResource(R.color.selected_indicator_color),
                        unselectedIconColor = colorResource(R.color.unselected_item_color),
                        unselectedTextColor = colorResource(R.color.unselected_item_color),
                        disabledIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent
                    ),
                    onClick = {
                        val startDestinationId = navController.graph.findStartDestination().id

                        navController.navigate(item.route) {
                            popUpTo(startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}