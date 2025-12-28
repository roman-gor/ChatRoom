package com.gorman.chatroom.ui.ui.screens.main.chats

import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.ui.navigation.Destination
import com.gorman.chatroom.ui.states.ConversationUiState
import com.gorman.chatroom.ui.ui.components.BottomSendMessageView
import com.gorman.chatroom.ui.ui.components.DateItem
import com.gorman.chatroom.ui.ui.components.MessageItem
import com.gorman.chatroom.ui.ui.components.parseIso
import com.gorman.chatroom.ui.ui.components.IconButton
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.ui.theme.ChatRoomTheme
import com.gorman.chatroom.ui.viewmodel.ChatConversationViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun ChatConversationScreenEntry(
    chatConversationViewModel: ChatConversationViewModel = hiltViewModel(),
    currentUserId: String,
    args: Destination.ChatConversation,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlusClick: () -> Unit,
    onNavigateToCall: (targetId: String, isVideoCall: Boolean) -> Unit,
) {
    val getterUserId = args.getterUserId
    val chatId = chatConversationViewModel.chatId.value
    LaunchedEffect(Unit) {
        chatConversationViewModel.startCallEvent.collect { event->
            event?.let {
                onNavigateToCall(it.targetId, it.isVideoCall)
            }
        }
    }
    LaunchedEffect(chatId, currentUserId, getterUserId) {
        if (!args.chatId.isNullOrEmpty()) {
            chatConversationViewModel.initializeChat(args.chatId, currentUserId)
            Log.d("ConversationScreen", "Existing chat: chatId=${args.chatId} currentUserId=$currentUserId")
        } else if (chatId.isNullOrEmpty()){
            chatConversationViewModel.setupNewConversation(currentUserId, getterUserId)
            Log.d("ConversationScreen", "New chat: currentUserId=$currentUserId getterUserId=$getterUserId")
        }
    }
    val messagesList by chatConversationViewModel.messages.collectAsStateWithLifecycle()
    val getterUser = chatConversationViewModel.getterUserData.value
    val sortedMessages = messagesList.sortedByDescending {
        if (it.timestamp.isNullOrEmpty()) {
            0L
        } else {
            parseIso(it.timestamp)
        }
    }
    ChatConversationScreen(
        state = ConversationUiState(
            getterUser = getterUser,
            getterUserId = getterUserId,
            currentUserId = currentUserId,
            chatId = chatId,
            sortedMessages = sortedMessages
        ),
        onBackClick = onBackClick,
        onMoreClick = onMoreClick,
        onPlusClick = onPlusClick,
        onSendMessage = { message ->
            if (chatId != null) {
                chatConversationViewModel.sendMessage(
                    chatId = chatId.ifBlank { args.chatId!! },
                    currentUserId = currentUserId,
                    getterId = getterUserId,
                    text = message)
            }
        },
        onStartCallClick = { isVideo ->
            getterUserId.let { chatConversationViewModel.startCall(it, isVideo) }
        }
    )
}

@Composable
fun ChatConversationScreen(
    state: ConversationUiState,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlusClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onStartCallClick: (Boolean) -> Unit
) {
    Scaffold (
        topBar = { ChatTopBar(onBackClick = onBackClick, onMoreClick = onMoreClick) },
        bottomBar = {
            BottomSendMessageView(
                onPlusClick = onPlusClick,
                onSendMessageClick = { text -> onSendMessage(text) },
                modifier = Modifier)
        }
    ){ innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoChat(
                onVideoClick = {
                    onStartCallClick(true)
                },
                onPhoneClick = {
                    onStartCallClick(false)
                },
                getterUser = state.getterUser)
            LazyColumn (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
                    .background(color = MaterialTheme.colorScheme.onSecondary),
                reverseLayout = true,
            ) {
                itemsIndexed(state.sortedMessages) { index, message ->
                    val messageDate = runCatching { Instant.parse(message.timestamp)
                        .atZone(ZoneId.systemDefault()).toLocalDate() }.getOrNull()
                    val nextMessageDate = state.sortedMessages.getOrNull(index + 1)?.timestamp
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            runCatching { Instant.parse(it).atZone(ZoneId.systemDefault()).toLocalDate() }.getOrNull()
                        }
                    if (message.timestamp != "" && state.getterUser?.username != null) {
                        state.currentUserId?.let {
                            val isFirstMessage = index == 0
                            val isLastMessage = index == state.sortedMessages.lastIndex
                            MessageItem(
                                message,
                                state.currentUserId,
                                isFirstMessage,
                                isLastMessage,
                                state.getterUser.username,
                                false)
                        }
                    }
                    if (index == state.sortedMessages.lastIndex || messageDate != nextMessageDate) {
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
            .background(color = MaterialTheme.colorScheme.background)
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
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
                    color = MaterialTheme.colorScheme.secondary,
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getterUser?.profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Avatar",
                placeholder = painterResource(R.drawable.default_ava),
                modifier = Modifier
                    .size(ChatRoomTheme.dimens.avatarSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
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
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                getterUser?.phone?.let {
                    Text(text = it,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = mulishFont(),
                        color = MaterialTheme.colorScheme.tertiary,
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
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .width(24.dp)
                        .height(19.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { onPhoneClick() }) {
                Icon(painter = painterResource(R.drawable.phone_icon),
                    contentDescription = "VideoCall",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .width(21.dp)
                        .height(20.dp))
            }
        }
    }
}