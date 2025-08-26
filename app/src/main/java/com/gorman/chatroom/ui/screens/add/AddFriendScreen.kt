package com.gorman.chatroom.ui.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.gorman.chatroom.R

@Composable
fun AddFriendScreen(onBack: () -> Unit){
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(title = R.string.add_friend, onBack = { onBack() }) }
    ){ innerPaddings ->
        Column (modifier = Modifier.fillMaxSize().padding(innerPaddings),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.add_friend),
                color = Color.Black)
        }
    }
}