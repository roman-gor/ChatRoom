package com.gorman.chatroom.ui.screens.main.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.data.Message
import com.gorman.chatroom.data.messagesList
import com.gorman.chatroom.ui.fonts.mulishFont

@Composable
fun ChatConversationScreen(onBackClick: () -> Unit,
                           onMoreClick: () -> Unit,
                           onPhoneClick: () -> Unit,
                           onVideoClick: () -> Unit,
                           onPlusClick: () -> Unit,
                           onSendMessageClick: (String) -> Unit) {
    Scaffold (
        topBar = { ChatTopBar(onBackClick = onBackClick, onMoreClick = onMoreClick) },
        bottomBar = { BottomSendMessageView(onPlusClick = onPlusClick, onSendMessageClick = onSendMessageClick) }
    ){ innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .background(color = colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoChat(onVideoClick = onVideoClick, onPhoneClick = onPhoneClick)
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
                    .background(color = colorResource(R.color.chat_bg)),
                reverseLayout = true,
            ) {
                items(messagesList) { message ->
                    MessageItem(message)
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message){
    val colorBackground =
        if (message.isOwn) colorResource(R.color.own_message)
        else colorResource(R.color.white)

    val colorText =
        if (message.isOwn) colorResource(R.color.white)
        else colorResource(R.color.black)

    val colorTime =
        if (message.isOwn) colorResource(R.color.chat_bg)
        else colorResource(R.color.not_own_message_time_text)

    val alignment = if (message.isOwn) Alignment.CenterEnd else Alignment.CenterStart

    val corners =
        if (message.isOwn) RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 0.dp
        )
        else RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 16.dp
        )

    val boxPaddings = when (message.messageId) {
        messagesList.size - 1 -> {
            if (message.isOwn) {
                PaddingValues(bottom = 24.dp, start = 56.dp, end = 24.dp, top = 4.dp)
            } else {
                PaddingValues(bottom = 24.dp, start = 24.dp, end = 56.dp, top = 4.dp)
            }
        }
        0 -> {
            if (message.isOwn) {
                PaddingValues(bottom = 4.dp, start = 56.dp, end = 24.dp, top = 24.dp)
            } else {
                PaddingValues(bottom = 4.dp, start = 24.dp, end = 56.dp, top = 24.dp)
            }
        }
        else -> {
            if (message.isOwn) {
                PaddingValues(bottom = 4.dp, start = 56.dp, end = 24.dp, top = 4.dp)
            } else {
                PaddingValues(bottom = 4.dp, start = 24.dp, end = 56.dp, top = 4.dp)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(boxPaddings),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = colorBackground,
                    shape = corners
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.content,
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colorText
            )
            Text(
                text = message.time,
                fontFamily = mulishFont(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = colorTime
            )
        }
    }
}

@Composable
fun ChatTopBar(onBackClick: () -> Unit, onMoreClick: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.white))
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(R.color.white))
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(R.drawable.back_icon, onClick = { onBackClick() })
                Text(text = stringResource(R.string.message),
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(R.drawable.more_icon, onClick = { onMoreClick() })
            }
        }
    }
}

@Composable
fun IconButton(icon: Int, onClick: () -> Unit){
    Card(
        modifier = Modifier
            .size(42.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { onClick() })
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ){
            Icon(painter = painterResource(icon),
                contentDescription = "Back",
                tint = colorResource(R.color.black))
        }
    }
}

@Composable
fun InfoChat(onVideoClick: () -> Unit, onPhoneClick: () -> Unit){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row (
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(R.drawable.default_avatar),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ){
                Text(text = "David Waynee",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = mulishFont(),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "+(44) 59 4485 5794",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row {
            IconButton(onClick = { onVideoClick() }) {
                Icon(painter = painterResource(R.drawable.camera_icon),
                    contentDescription = "VideoCall",
                    tint = colorResource(R.color.black),
                    modifier = Modifier
                        .width(24.dp)
                        .height(19.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { onPhoneClick() }) {
                Icon(painter = painterResource(R.drawable.phone_icon),
                    contentDescription = "VideoCall",
                    tint = colorResource(R.color.black),
                    modifier = Modifier
                        .width(21.dp)
                        .height(20.dp))
            }
        }
    }
}

@Composable
fun BottomSendMessageView(onPlusClick: () -> Unit, onSendMessageClick: (String) -> Unit){
    var value by remember { mutableStateOf("") }
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.white))
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClick = { onPlusClick() },
            modifier = Modifier
                .background(color = Color.Transparent)
                .size(56.dp)) {
            Icon(painter = painterResource(R.drawable.plus),
                contentDescription = "Plus",
                tint = colorResource(R.color.selected_indicator_color),
                modifier = Modifier.size(18.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorResource(R.color.chat_bg),
                unfocusedContainerColor = colorResource(R.color.chat_bg),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            placeholder = {
                Text(
                    text = stringResource(R.string.type_message_placeholder),
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    color = colorResource(R.color.placeholder_message)
                )
            }
        )
        Image(
            painter = painterResource(R.drawable.send_message),
            contentDescription = "Send Data",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable { onSendMessageClick(value) },
            contentScale = ContentScale.Crop
        )
    }
}