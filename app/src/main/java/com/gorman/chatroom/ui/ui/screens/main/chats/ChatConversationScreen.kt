package com.gorman.chatroom.ui.ui.screens.main.chats

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.ui.ui.components.BottomSendMessageView
import com.gorman.chatroom.ui.ui.components.DateItem
import com.gorman.chatroom.ui.ui.components.MessageItem
import com.gorman.chatroom.ui.ui.components.parseIso
import com.gorman.chatroom.ui.ui.components.IconButton
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.viewmodel.ChatConversationViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun ChatConversationScreen(mapId: Map<String, String>,
                           onBackClick: () -> Unit,
                           onMoreClick: () -> Unit,
                           onPhoneClick: () -> Unit,
                           onVideoClick: () -> Unit,
                           onPlusClick: () -> Unit) {
    val chatConversationViewModel: ChatConversationViewModel = hiltViewModel()
    val currentUserId = mapId["currentUserId"]
    val getterUserId = mapId["getterUserId"]
    val chatId = chatConversationViewModel.chatId.value

    LaunchedEffect(chatId, currentUserId, getterUserId) {
        if (!mapId["chatId"].isNullOrEmpty() && currentUserId != null) {
            chatConversationViewModel.initializeChat(mapId["chatId"]!!, currentUserId)
            Log.d("ConversationScreen", "Existing chat: chatId=${mapId["chatId"]} currentUserId=$currentUserId")
        } else if (currentUserId != null && getterUserId != null && chatId.isNullOrEmpty()){
            chatConversationViewModel.setupNewConversation(currentUserId, getterUserId)
            Log.d("ConversationScreen", "New chat: currentUserId=$currentUserId getterUserId=$getterUserId")
        }
    }
    val messagesList = chatConversationViewModel.messages.collectAsState().value
    val getterUser = chatConversationViewModel.getterUserData.value
    val sortedMessages = messagesList.sortedByDescending {
        if (it.timestamp.isNullOrEmpty()) {
            0L
        } else {
            parseIso(it.timestamp)
        }
    }

    Scaffold (
        topBar = { ChatTopBar(onBackClick = onBackClick, onMoreClick = onMoreClick) },
        bottomBar = {
            BottomSendMessageView(
                onPlusClick = onPlusClick,
                onSendMessageClick = {
                    if (chatId != null && currentUserId != null && getterUserId != null) {
                        chatConversationViewModel.sendMessage(
                            chatId = chatId.ifBlank { mapId["chatId"]!! },
                            currentUserId = currentUserId,
                            getterId = getterUserId,
                            text = it)
                    }
                },
                modifier = Modifier)
        }
    ){ innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .background(color = colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoChat(onVideoClick = onVideoClick, onPhoneClick = onPhoneClick, getterUser = getterUser)
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
                    .background(color = colorResource(R.color.chat_bg)),
                reverseLayout = true,
            ) {
                itemsIndexed(sortedMessages) { index, message ->
                    val messageDate = runCatching { Instant.parse(message.timestamp)
                        .atZone(ZoneId.systemDefault()).toLocalDate() }.getOrNull()
                    val nextMessageDate = sortedMessages.getOrNull(index + 1)?.timestamp
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            runCatching { Instant.parse(it).atZone(ZoneId.systemDefault()).toLocalDate() }.getOrNull()
                        }
                    if (message.timestamp != "" && getterUser?.username != null) {
                        currentUserId?.let {
                            val isFirstMessage = index == 0
                            val isLastMessage = index == sortedMessages.lastIndex
                            MessageItem(
                                message,
                                currentUserId,
                                isFirstMessage,
                                isLastMessage,
                                getterUser.username,
                                false)
                        }
                    }
                    if (index == sortedMessages.lastIndex || messageDate != nextMessageDate) {
                        messageDate?.let { DateItem(it) }
                    }
                }
            }
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
fun InfoChat(onVideoClick: () -> Unit, onPhoneClick: () -> Unit, getterUser: UsersData?){
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
                painter = rememberAsyncImagePainter(
                    model = getterUser?.profileImageUrl
                ),
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
                getterUser?.username?.let {
                    Text(text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = mulishFont(),
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                getterUser?.phone?.let {
                    Text(text = it,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = mulishFont(),
                        color = colorResource(R.color.unselected_item_color),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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