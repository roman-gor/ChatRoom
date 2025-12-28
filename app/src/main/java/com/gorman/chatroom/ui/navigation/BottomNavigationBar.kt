package com.gorman.chatroom.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.ui.fonts.mulishFont

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<Destination.NavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Card (
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(24.dp)
    ){
        NavigationBar (
            modifier = Modifier.background(color = Color.Transparent),
            containerColor = Color.Transparent,
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            items.forEach { item ->
                val route = item as Destination
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(item.icon), contentDescription = stringResource(item.title)) },
                    label = {
                        Text(text = stringResource(item.title),
                            fontStyle = FontStyle.Normal,
                            fontFamily = mulishFont()
                        ) },
                    selected = currentDestination?.hasRoute(route::class) ?: false,
                    colors = NavigationBarItemColors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                        unselectedTextColor = MaterialTheme.colorScheme.tertiary,
                        disabledIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent
                    ),
                    onClick = {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
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