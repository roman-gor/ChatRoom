package com.gorman.chatroom.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.fonts.mulishFont

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<Screen.BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Card (
        modifier = Modifier.fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(24.dp)
    ){
        NavigationBar (
            modifier = Modifier.background(color = Color.Transparent),
            containerColor = Color.Transparent,
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(item.bIcon), contentDescription = stringResource(item.bTitle)) },
                    label = {
                        Text(text = stringResource(item.bTitle),
                            fontStyle = FontStyle.Normal,
                            fontFamily = mulishFont()
                        ) },
                    selected = currentRoute == item.bRoute,
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
                        navController.navigate(item.bRoute) {
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