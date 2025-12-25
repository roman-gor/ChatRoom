package com.gorman.chatroom.ui.ui.screens.main.chats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.ChatPreviewData
import com.gorman.chatroom.ui.states.ChatsUiState
import com.gorman.chatroom.ui.ui.components.ErrorLoading
import com.gorman.chatroom.ui.ui.components.LoadingStub
import com.gorman.chatroom.ui.ui.components.formatMessageTimestamp
import com.gorman.chatroom.ui.ui.components.parseIso
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.viewmodel.ChatsScreenViewModel
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel

@Composable
fun ChatsScreenEntry(
    chatsScreenViewModel: ChatsScreenViewModel = hiltViewModel(),
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    onItemClick: (String) -> Unit
) {
    val userId by mainScreenViewModel.userId.collectAsStateWithLifecycle()
    val uiState by chatsScreenViewModel.chatsUiState.collectAsStateWithLifecycle()
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            chatsScreenViewModel.loadAllChats(userId)
        }
    }
    when(val state = uiState) {
        is ChatsUiState.Error -> ErrorLoading(stringResource(R.string.errorChatsLoading))
        ChatsUiState.Idle -> Box(modifier = Modifier.fillMaxSize())
        ChatsUiState.Loading -> LoadingStub()
        is ChatsUiState.Success -> {
            val sortedChats = state.chats.sortedByDescending {
                parseIso(it.lastMessage?.timestamp)
            }
            ChatsScreen(
                userId = userId,
                chatPreviews = sortedChats,
                onItemClick = onItemClick,
                onDeleteChat = { chatId -> chatsScreenViewModel.deleteChat(chatId) },
            )
        }
    }
}

@Composable
fun ChatsScreen(
    userId: String,
    chatPreviews: List<ChatPreviewData>,
    onItemClick: (String) -> Unit,
    onDeleteChat: (String) -> Unit
){
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        itemsIndexed(
            items = chatPreviews,
            key = { _, item -> item.user?.userId ?: "" }
        ) { index, item ->
            val datetime = formatMessageTimestamp(item.lastMessage?.timestamp)
            DismissibleChatPreviewItem(
                item = item,
                onDeleteChat = {
                    if (item.chatId != null)
                        onDeleteChat(item.chatId)
                },
                onItemClick = onItemClick,
                userId = userId,
                datetime = datetime
            )
            if (index < chatPreviews.size - 1)
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(R.color.chat_bg)
                )
        }
    }
}

@Composable
fun DismissibleChatPreviewItem(
    item: ChatPreviewData,
    onDeleteChat: (String) -> Unit,
    onItemClick: (String) -> Unit,
    userId: String,
    datetime: String
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                item.chatId?.let { id -> onDeleteChat(id) }
                true
            } else false
        },
        positionalThreshold = { it / 2 }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    Icon(
                        painter = painterResource(R.drawable.delete_icon),
                        contentDescription = "Delete Item",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(R.color.logout_background_color))
                            .wrapContentSize(Alignment.CenterEnd)
                            .padding(end = 24.dp),
                        tint = colorResource(R.color.red_logout_color)
                    )
                }
                SwipeToDismissBoxValue.Settled -> {}
                SwipeToDismissBoxValue.StartToEnd -> {}
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        ChatPreviewItem(
            item = item,
            onItemClick = onItemClick,
            userId = userId,
            datetime = datetime
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatPreviewItem(item: ChatPreviewData,
                    onItemClick: (String) -> Unit,
                    userId: String,
                    datetime: String) {
    val user = item.user
    val lastMessage = item.lastMessage
    val unreadMessages = item.unreadQuantity
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    if (item.chatId != null && user?.userId != null) {
                        val serialized = "getterUserId=${user.userId};currentUserId=${userId};chatId=${item.chatId}"
                        onItemClick(serialized)
                    }
                }
            )
            .background(colorResource(R.color.white))
            .padding(start = 24.dp, end = 30.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = user?.profileImageUrl
                    ),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    user?.username?.let {
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = mulishFont(),
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    lastMessage?.text?.let {
                        Text(
                            text = it,
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
            Row(verticalAlignment = Alignment.Top) {
                Spacer(modifier = Modifier.width(30.dp))
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = datetime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = mulishFont()
                    )
                    if (unreadMessages != 0) {
                        TextField(unreadMessages.toString())
                    } else {
                        Spacer(modifier = Modifier.height(27.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TextField(value: String){
    Card (
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.selected_indicator_color)
        )
    ){
        Text(text = value,
            fontFamily = mulishFont(),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp))
    }
}