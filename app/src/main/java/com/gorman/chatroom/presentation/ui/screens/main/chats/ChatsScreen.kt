package com.gorman.chatroom.presentation.ui.screens.main.chats

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.entities.ChatPreviewData
import com.gorman.chatroom.domain.entities.ChatsData
import com.gorman.chatroom.ui.components.formatMessageTimestamp
import com.gorman.chatroom.ui.components.parseIso
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.ChatsScreenViewModel
import com.gorman.chatroom.viewmodel.MainScreenViewModel

@Composable
fun ChatsScreen(onItemClick: (String) -> Unit){
    val chatsScreenViewModel: ChatsScreenViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel =  hiltViewModel()

    val userId by mainScreenViewModel.userId.collectAsState()
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            chatsScreenViewModel.getUserChats(userId)
            Log.d("Loading chats", "Loading")
        }
    }
    val chatsList by chatsScreenViewModel.chatsList.collectAsState()
    val sortedChatsList = chatsList.sortedByDescending { chat->
        parseIso(chat?.lastMessageTimestamp)
    }

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        itemsIndexed(
            items = sortedChatsList,
            key = { _, item -> item?.chatId ?: "" }
        ) { index, item ->
            LaunchedEffect(item?.chatId, item?.lastMessageId) {
                if (item?.chatId != null && item.lastMessageId != null) {
                    chatsScreenViewModel.initChatPreview(
                        item.chatId,
                        userId
                    )
                    Log.d("Item", item.chatId)
                }
            }
            val datetime = formatMessageTimestamp(item?.lastMessageTimestamp)
            val chatMap by chatsScreenViewModel.chatPreviews.collectAsState()
            val preview = chatMap[item?.chatId]
            val text = preview?.lastMessage?.text
            if (!text.isNullOrBlank()) {
                DismissibleChatPreviewItem(
                    item = item,
                    onDeleteChat = {
                        if (item?.chatId != null)
                            chatsScreenViewModel.deleteChat(item.chatId)
                    },
                    onItemClick = onItemClick,
                    userId = userId,
                    chatMap = chatMap,
                    datetime = datetime
                )
                if (index < chatsList.lastIndex)
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colorResource(R.color.chat_bg)
                    )
            }
        }
    }
}

@Composable
fun DismissibleChatPreviewItem(
    item: ChatsData?,
    onDeleteChat: (String) -> Unit,
    onItemClick: (String) -> Unit,
    userId: String,
    chatMap: Map<String, ChatPreviewData>,
    datetime: String
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                if (item?.chatId != null) {
                    onDeleteChat(item.chatId)
                }
                true
            } else {
                false
            }
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
            chatMap = chatMap,
            datetime = datetime
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatPreviewItem(item: ChatsData?,
                    onItemClick: (String) -> Unit,
                    userId: String,
                    chatMap: Map<String, ChatPreviewData>,
                    datetime: String) {
    val mapId = mutableMapOf<String, String>()
    val user = chatMap[item?.chatId]?.user
    val lastMessage = chatMap[item?.chatId]?.lastMessage
    val unreadMessages = chatMap[item?.chatId]?.unreadQuantity
    if (item?.chatId != null && user?.userId != null) {
        mapId.put("getterUserId", user.userId)
        mapId.put("currentUserId", userId)
        mapId.put("chatId", item.chatId)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    if (item?.chatId != null && user?.userId != null) {
                        val serialized =
                            "getterUserId=${user.userId};currentUserId=${userId};chatId=${item.chatId}"
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