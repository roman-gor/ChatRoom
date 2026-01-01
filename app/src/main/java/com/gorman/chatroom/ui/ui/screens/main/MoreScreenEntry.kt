package com.gorman.chatroom.ui.ui.screens.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.core.R
import com.gorman.chatroom.ui.states.MoreScreenUiState
import com.gorman.chatroom.ui.viewmodel.MoreScreenViewModel
import com.gorman.core.domain.models.MoreScreenData
import com.gorman.core.ui.fonts.mulishFont

@Composable
fun MoreScreenEntry(
    moreScreenViewModel: MoreScreenViewModel = hiltViewModel(),
    onLangChange: (String) -> Unit
){
    val uiState by moreScreenViewModel.uiState.collectAsStateWithLifecycle()
    val items = moreScreenViewModel.items
    MoreScreen(
        state = uiState,
        items = items,
        onLangChange = onLangChange,
        onChangeChatHistory = { history -> moreScreenViewModel.setHideChatHistory(history) },
        onChangeSecurity = { security -> moreScreenViewModel.setSecurity(security) },
        onChangeNotifications = { muted -> moreScreenViewModel.changeNotificationsState(muted) },
        onChangeDarkMode = { darkMode -> moreScreenViewModel.changeDarkModeState(darkMode) }
    )
}

@Composable
fun MoreScreen(
    state: MoreScreenUiState,
    items: List<MoreScreenData>,
    onLangChange: (String) -> Unit,
    onChangeChatHistory: (Boolean) -> Unit,
    onChangeSecurity: (Boolean) -> Unit,
    onChangeNotifications: (Boolean) -> Unit,
    onChangeDarkMode: (Boolean) -> Unit
){
    LazyColumn (modifier = Modifier.padding(16.dp)){
        itemsIndexed(items) { index, item ->
            val specialPadding = if (index > 2 && index != 5 && index != 6) 12.dp else 0.dp
            MoreItem(
                item = item,
                index = index,
                onLangChange = onLangChange,
                onChangeDarkMode = onChangeDarkMode,
                onChangeNotifications = onChangeNotifications,
                onChangeSecurity = onChangeSecurity,
                onChangeChatHistory = onChangeChatHistory,
                language = state.language,
                darkMode = state.isDarkMode,
                mute = state.isNotificationsMuted,
                hideChatHistory = state.isChatHistoryHidden,
                security = state.isSecurityEnabled,
                specialPadding = specialPadding)
            if (index == 2) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    thickness = 1.dp)
            }
        }
    }
}

@Composable
fun MoreItem(item: MoreScreenData,
             index: Int,
             onLangChange: (String) -> Unit,
             onChangeDarkMode: (Boolean) -> Unit,
             onChangeNotifications: (Boolean) -> Unit,
             onChangeChatHistory: (Boolean) -> Unit,
             onChangeSecurity: (Boolean) -> Unit,
             language: String,
             darkMode: Boolean,
             mute: Boolean,
             hideChatHistory: Boolean,
             security: Boolean,
             specialPadding: Dp){
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = {
                when (index) {
                    1 -> onChangeDarkMode(!darkMode)
                    2 -> onChangeNotifications(!mute)
                    else -> {}
                }
            })
            .padding(horizontal = 12.dp, vertical = specialPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if (index == 10) {
                Icon(painter = painterResource(item.icon),
                    contentDescription = stringResource(item.name),
                    tint = colorResource(R.color.red_logout_color),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(item.name),
                    fontFamily = mulishFont(),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.red_logout_color),
                    fontSize = 16.sp
                )
            }
            else {
                Icon(painter = painterResource(item.icon),
                    contentDescription = stringResource(item.name),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(item.name),
                    fontFamily = mulishFont(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            when(item.id){
                0 -> LanguageMenu(
                    language = language,
                    onLangChange = onLangChange)
                1 -> SwitchMode(
                    onClick = { onChangeDarkMode(!darkMode) },
                    checked = darkMode)
                2 -> SwitchMode(
                    onClick = { onChangeNotifications(!mute) },
                    checked = mute)
                5 -> {
                    SwitchMode(
                        onClick = { onChangeChatHistory(!hideChatHistory) },
                        checked = hideChatHistory
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconArrowForward()
                }
                6 -> {
                    SwitchMode(
                        onClick = { onChangeSecurity(!security) },
                        checked = security
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconArrowForward()
                }
                10 -> {}
                else -> IconArrowForward()
            }
        }
    }
}

@Composable
fun LanguageMenu(language: String, onLangChange: (String) -> Unit){
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier.wrapContentSize()
    ){
        Button(
            onClick = {expanded = !expanded},
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .wrapContentSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(text = if (language == "en") stringResource(R.string.language_en)
                else stringResource(R.string.language_ru),
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Icon(imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "ArrowDown",
                tint = MaterialTheme.colorScheme.secondary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = !expanded},
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(12.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.language_en),
                        fontFamily = mulishFont(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Normal
                    )
                },
                onClick = {
                    onLangChange("en")
                    expanded = !expanded
                }
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.language_ru),
                        fontFamily = mulishFont(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Normal
                    )
                },
                onClick = {
                    onLangChange("ru")
                    expanded = !expanded
                }
            )
        }
    }
}

@Composable
fun IconArrowForward(){
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Go to item",
        tint = MaterialTheme.colorScheme.secondary)
}

@Composable
fun SwitchMode(onClick: () -> Unit, checked: Boolean){
    Switch(
        checked = checked,
        onCheckedChange = {onClick()}
    )
}

