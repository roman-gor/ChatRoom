package com.gorman.chatroom.ui.screens.main.chats

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.ChatsScreenViewModel
import com.gorman.chatroom.viewmodel.MainScreenViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun ChatsScreen(onItemClick: (String) -> Unit){
    val chatsScreenViewModel: ChatsScreenViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel =  hiltViewModel()

    val userId by mainScreenViewModel.userId.collectAsState()
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            chatsScreenViewModel.getUserChats(userId)
        }
    }
    val chatsList by chatsScreenViewModel.chatsList.collectAsState()

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items(chatsList){ item ->
            ChatPreviewItem(item, onItemClick = onItemClick, userId, chatsScreenViewModel)
        }
    }
}

@Composable
fun ChatPreviewItem(item: ChatsData?,
                    onItemClick: (String) -> Unit,
                    userId: String,
                    chatsScreenViewModel: ChatsScreenViewModel){
    LaunchedEffect(key1 = item?.chatId) {
        if (item?.chatId != null && item.lastMessageId != null) {
            chatsScreenViewModel.initChatPreview(
                item.chatId,
                userId,
                item.lastMessageId)
            Log.d("Item", item.chatId)
        }
    }
    val datetime = formatMessageTimestamp(item?.lastMessageTimestamp)
    val mapId = mutableMapOf<String, String>()
    val chatMap by chatsScreenViewModel.chatPreviews.collectAsState()
    val user = chatMap[item?.chatId]?.user
    val lastMessage = chatMap[item?.chatId]?.lastMessage
    val unreadMessages = chatMap[item?.chatId]?.unreadQuantity
    if (item?.chatId != null && user?.userId != null) {
        mapId.put("getterUserId", user.userId)
        mapId.put("currentUserId", userId)
        mapId.put("chatId", item.chatId)
    }
    val serialized = mapId.entries.joinToString(";") { "${it.key}=${it.value}" }

    Column (modifier = Modifier.fillMaxWidth()){
        Row (modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(serialized) })
            .padding(start = 24.dp, end = 30.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row (
                modifier = Modifier.weight(1f, fill = false),
                horizontalArrangement = Arrangement.Center
            ){
                Image(
                    painter = rememberAsyncImagePainter(
                        model = user?.profileImageUrl
                    ),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column (
                    verticalArrangement = Arrangement.Center
                ){
                    user?.username?.let {
                        Text(text = it,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = mulishFont(),
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    lastMessage?.text?.let {
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
            Row (verticalAlignment = Alignment.Top){
                Spacer(modifier = Modifier.width(30.dp))
                Column (
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.End
                ){
                    Text(text = datetime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = mulishFont())
                    if (unreadMessages != 0) {
                        TextField(unreadMessages.toString())
                    }
                    else {
                        Spacer(modifier = Modifier.height(27.dp))
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(),
            color = colorResource(R.color.chat_bg))
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

fun formatMessageTimestamp(isoOrEpoch: String?): String {
    if (isoOrEpoch.isNullOrBlank()) return ""

    val locale = Locale.getDefault()
    val zone = ZoneId.systemDefault()

    val instant = runCatching {
        Instant.parse(isoOrEpoch)
    }.getOrElse {
        runCatching { Instant.ofEpochMilli(isoOrEpoch.toLong()) }.getOrElse { return "" }
    }

    val dt = instant.atZone(zone)
    val today = LocalDate.now(zone)
    val date = dt.toLocalDate()

    return when (ChronoUnit.DAYS.between(date, today)) {
        0L -> DateTimeFormatter.ofPattern("HH:mm", locale).format(dt)                 // сегодня → время
        in 1..6 -> {
            val w = DateTimeFormatter.ofPattern("eee", locale).format(dt)
            w.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        }
        else -> DateTimeFormatter.ofPattern("dd.MM.yyyy", locale).format(dt)          // иначе → дата
    }
}