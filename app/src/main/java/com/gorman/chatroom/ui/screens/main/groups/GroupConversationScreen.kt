package com.gorman.chatroom.ui.screens.main.groups

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.R
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.ui.components.BottomSendMessageView
import com.gorman.chatroom.ui.components.DateItem
import com.gorman.chatroom.ui.components.IconButton
import com.gorman.chatroom.ui.components.MessageItem
import com.gorman.chatroom.ui.components.parseIso
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.GroupConversationViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun GroupConversationScreen(mapId: Map<String, String>,
                           onBackClick: () -> Unit,
                           onMoreClick: () -> Unit,
                           onPhoneClick: () -> Unit,
                           onVideoClick: () -> Unit,
                           onPlusClick: () -> Unit) {
    val groupConversationViewModel: GroupConversationViewModel = hiltViewModel()
    val currentUserId = mapId["currentUserId"]
    val groupId = groupConversationViewModel.groupId.value
    val getterUsers = groupConversationViewModel.getterUsersData.value

    LaunchedEffect(groupId, currentUserId) {
        if (!mapId["groupId"].isNullOrEmpty() && currentUserId != null) {
            groupConversationViewModel.initializeGroup(mapId["groupId"]!!, currentUserId)
            Log.d("ConversationScreen", "Existing group: groupId=${mapId["groupId"]} currentUserId=$currentUserId")
        }
        else if (currentUserId != null && groupId.isNullOrEmpty() && !mapId["groupName"].isNullOrEmpty()){
            val membersList = mapId["getterUsers"]?.split(",")?.map { it.trim() }!!
            groupConversationViewModel.setupNewConversation(currentUserId, membersList, mapId["groupName"]!!)
            Log.d("ConversationScreen", "New chat: currentUserId=$currentUserId getterUsers=${getterUsers.size}")
        }
    }
    val messagesList = groupConversationViewModel.messages.collectAsState().value
    val sortedMessages = messagesList.sortedByDescending {
        if (it.timestamp.isNullOrEmpty()) {
            0L
        } else {
            parseIso(it.timestamp)
        }
    }
    val userMap = remember(getterUsers) {
        getterUsers.filterNotNull().associateBy { it.userId }
    }

    Scaffold (
        topBar = { ChatTopBar(onBackClick = onBackClick, onMoreClick = onMoreClick) },
        bottomBar = {
            BottomSendMessageView(
                onPlusClick = onPlusClick,
                onSendMessageClick = {
                    if (groupId != null && currentUserId != null && getterUsers.isNotEmpty()) {
                        groupConversationViewModel.sendMessage(
                            groupId = groupId.ifBlank { mapId["groupId"]!! },
                            currentUserId = currentUserId,
                            getterUsers = getterUsers,
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
            InfoChat(onVideoClick = onVideoClick, onPhoneClick = onPhoneClick, getterUsers = getterUsers, groupName = mapId["groupName"])
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
                    if (message.timestamp != "") {
                        currentUserId?.let {
                            val isFirstMessage = index == 0
                            val isLastMessage = index == sortedMessages.lastIndex
                            val senderName = userMap[message.senderId]?.username ?: "Неизвестный"
                            MessageItem(
                                message,
                                currentUserId,
                                isFirstMessage,
                                isLastMessage,
                                senderName,
                                true)
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
                Text(text = stringResource(R.string.group),
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
fun InfoChat(onVideoClick: () -> Unit, onPhoneClick: () -> Unit, getterUsers: List<UsersData?>, groupName: String?){
    val membersResource = if (getterUsers.size % 100 in 11..14) {
        stringResource(R.string.membersX5_X0)
    } else {
        when (getterUsers.size % 10) {
            1 -> stringResource(R.string.membersX1)
            2, 3, 4 -> stringResource(R.string.members3_4_X2_X4)
            else -> stringResource(R.string.membersX5_X0)
        }
    }
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
            OverlappingAvatars(getterUsers.filterNotNull().map { it.profileImageUrl }.toMutableList())
            Spacer(modifier = Modifier.width(12.dp))
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ){
                Text(text = "$groupName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "${getterUsers.size} " + membersResource,
                    fontSize = 12.sp,
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
                Icon(
                    painter = painterResource(R.drawable.camera_icon),
                    contentDescription = "VideoCall",
                    tint = colorResource(R.color.black),
                    modifier = Modifier
                        .width(24.dp)
                        .height(19.dp)
                )
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