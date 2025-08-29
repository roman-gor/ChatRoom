package com.gorman.chatroom.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.MainScreenViewModel

@Composable
fun TopBar(navController: NavHostController) {

    val viewModel: MainScreenViewModel = hiltViewModel()
    var isSearchBar by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.topbarbg),
            contentDescription = "TopBar Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        if (!isSearchBar) {
            DefaultTopBar(navController = navController, onSearchClick = {isSearchBar = !isSearchBar})
        } else {
            SearchBar(viewModel = viewModel,
                onCloseClick = {
                    isSearchBar = !isSearchBar
                    viewModel.onSearchValueChanged("")
                })
        }
    }
}

@Composable
fun DefaultTopBar(navController: NavHostController, onSearchClick: () -> Unit){
        Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(32.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier.padding(start = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = mulishFont(),
                fontStyle = FontStyle.Normal
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.search_magnifier),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = {
                            onSearchClick()
                        }),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            RotatingPlusIcon(navController = navController)
        }
    }
}

@Composable
fun SearchBar(viewModel: MainScreenViewModel, onCloseClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchTextField(
            value = viewModel.searchState.value,
            onValueChange = { viewModel.onSearchValueChanged(it) },
            modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(12.dp))
        CloseIcon(onCloseClick = { onCloseClick() })
    }
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = stringResource(R.string.search)
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontFamily = mulishFont(),
            fontSize = 14.sp,
            color = Color.Black
        ),
        modifier = modifier
            .height(43.dp)
            .background(Color.White, RoundedCornerShape(32.dp)),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Отступы здесь
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = TextStyle(
                                fontFamily = mulishFont(),
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun CloseIcon(onCloseClick: () -> Unit){
    Box(
        modifier = Modifier
            .clickable(
                onClick = {
                    onCloseClick()
                },
                indication = null,
                interactionSource = null
            )
            .size(42.dp)
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.plus),
            contentDescription = "Add",
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = -45f
                },
            tint = Color.White
        )
    }
}

@Composable
fun RotatingPlusIcon(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }
    val items = Screen.aItems
    var selectedColor by remember { mutableStateOf(Color.Transparent) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) -45f else 0f,
        label = "rotation"
    )

    selectedColor = if (expanded) Color.White.copy(alpha = 0.1f) else Color.Transparent

    Box(
        modifier = Modifier
            .clickable(
                onClick = {
                    expanded = !expanded
                },
                indication = null,
                interactionSource = null
            )
            .size(36.dp)
            .background(
                color = selectedColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.plus),
            contentDescription = "Add",
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer {
                    rotationZ = rotation
                },
            tint = Color.White
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {expanded = !expanded},
        offset = DpOffset(x = 0.dp, y = 10.dp),
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = stringResource(item.aTitle),
                    fontStyle = FontStyle.Normal,
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp)) },
                onClick = {
                    navController.navigate(item.aRoute)
                    expanded = !expanded},
                leadingIcon = {
                    Icon(
                        painter = painterResource(item.aIcon),
                        contentDescription = stringResource(item.aTitle),
                        modifier = Modifier.size(24.dp),
                        tint = colorResource(R.color.unselected_item_color)
                    )
                },
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .width(270.dp)
            )
        }
    }
}