package com.gorman.chatroom.ui.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.ui.fonts.mulishFont

@Composable
fun ErrorLoading(
    text: String
) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = text,
            fontFamily = mulishFont(),
            color = Color.Black,
            fontSize = 16.sp)
    }
}