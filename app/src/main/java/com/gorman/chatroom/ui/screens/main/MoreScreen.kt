package com.gorman.chatroom.ui.screens.main

import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.R
import com.gorman.chatroom.data.MoreScreenData
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.MoreScreenViewModel

@Composable
fun MoreScreen(onLangChange: (String) -> Unit){
    val moreScreenViewModel: MoreScreenViewModel = hiltViewModel()
    val language by moreScreenViewModel.language.collectAsState()
    val darkMode by moreScreenViewModel.darkMode.collectAsState()
    val mute by moreScreenViewModel.notificationsMute.collectAsState()

    LazyColumn (modifier = Modifier.padding(16.dp)){
        items(moreScreenViewModel.items) { item ->
            MoreItem(
                item = item,
                onLangChange = onLangChange,
                viewModel = moreScreenViewModel,
                language = language,
                darkMode = darkMode,
                mute = mute)
        }
    }
}

@Composable
fun MoreItem(item: MoreScreenData,
             onLangChange: (String) -> Unit,
             viewModel: MoreScreenViewModel,
             language: String,
             darkMode: Boolean,
             mute: Boolean){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Icon(painter = painterResource(item.icon),
                contentDescription = stringResource(item.name),
                tint = colorResource(R.color.black),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(item.name),
                fontFamily = mulishFont(),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black),
                fontSize = 16.sp
            )
        }
        Row {
            when(item.id){
                0 -> LanguageMenu(
                    language = language,
                    viewModel = viewModel,
                    onLangChange = onLangChange)
                1 -> SwitchMode(
                    onClick = { viewModel.changeDarkModeState(!darkMode) },
                    checked = darkMode)
                2 -> SwitchMode(
                    onClick = { viewModel.changeNotificationsState(!mute) },
                    checked = mute)
            }
        }
    }
}

@Composable
fun LanguageMenu(language: String, viewModel: MoreScreenViewModel, onLangChange: (String) -> Unit){
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier.wrapContentSize()
    ){
        Button(
            onClick = {expanded = !expanded},
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.white)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .wrapContentSize()
                .border(
                    width = 1.dp,
                    color = colorResource(R.color.unselected_item_color),
                    shape = RoundedCornerShape(12.dp))
        ) {
            Text(text = if (language == "en") stringResource(R.string.language_en)
                else stringResource(R.string.language_ru),
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.Bold
            )
            Icon(imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "ArrowDown",
                tint = colorResource(R.color.black))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = !expanded},
            containerColor = colorResource(R.color.white),
            shape = RoundedCornerShape(12.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.language_en),
                        fontFamily = mulishFont(),
                        fontSize = 14.sp,
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight.Normal
                    )
                },
                onClick = {
                    onLangChange("en")
                    //viewModel.changeLanguage("en")
                    expanded = !expanded
                }
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.language_ru),
                        fontFamily = mulishFont(),
                        fontSize = 14.sp,
                        color = colorResource(R.color.black),
                        fontWeight = FontWeight.Normal
                    )
                },
                onClick = {
                    onLangChange("ru")
                    //viewModel.changeLanguage("ru")
                    expanded = !expanded
                }
            )
        }
    }
}

@Composable
fun IconArrowForward(){
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Go to item",
        tint = colorResource(R.color.black))
}

@Composable
fun SwitchMode(
    onClick: () -> Unit,
    checked: Boolean){
    Switch(
        checked = checked,
        onCheckedChange = {onClick()}
    )
}

