package com.gorman.chatroom.ui.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.ui.fonts.mulishFont

@Composable
fun BottomSendMessageView(onPlusClick: () -> Unit, onSendMessageClick: (String) -> Unit, modifier: Modifier){
    var value by remember { mutableStateOf("") }
    Row (
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = { onPlusClick() },
            modifier = modifier
                .background(color = Color.Transparent)
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = "Plus",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(18.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = modifier.weight(1f),
            textStyle = TextStyle(
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            placeholder = {
                Text(
                    text = stringResource(R.string.type_message_placeholder),
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        )
        Image(
            painter = painterResource(R.drawable.send_message),
            contentDescription = "Send Data",
            modifier = modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable {
                    if (value.isNotBlank())
                        onSendMessageClick(value)
                    value = ""
                },
            contentScale = ContentScale.Crop
        )
    }
}