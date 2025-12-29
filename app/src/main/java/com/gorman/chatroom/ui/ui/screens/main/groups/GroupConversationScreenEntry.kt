package com.gorman.chatroom.ui.ui.screens.main.groups

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.MessageUiModel
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.ui.navigation.Destination
import com.gorman.chatroom.ui.states.GroupConversationUiState
import com.gorman.chatroom.ui.ui.components.BottomSendMessageView
import com.gorman.chatroom.ui.ui.components.DateItem
import com.gorman.chatroom.ui.ui.components.MessageItem
import com.gorman.chatroom.ui.ui.components.parseIso
import com.gorman.chatroom.ui.ui.components.IconButton
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.viewmodel.GroupConversationViewModel
import java.time.Instant
import java.time.ZoneId
import kotlin.let

@Composable
fun GroupConversationScreenEntry(
    groupConversationViewModel: GroupConversationViewModel = hiltViewModel(),
    currentUserId: String,
    args: Destination.GroupConversation,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onVideoClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    val groupId = groupConversationViewModel.groupId.value
    val getterUsers = groupConversationViewModel.getterUsersData.value
    LaunchedEffect(groupId, currentUserId) {
        if (!args.groupId.isNullOrEmpty()) {
            groupConversationViewModel.initializeGroup(args.groupId, currentUserId)
            Log.d("ConversationScreen", "Existing group: groupId=${args.groupId} currentUserId=$currentUserId")
        }
        else if (groupId.isNullOrEmpty() && args.groupName.isNotEmpty()){
            val membersList = args.memberList?.split(",")?.map { it.trim() }!!
            groupConversationViewModel.setupNewConversation(currentUserId, membersList, args.groupName)
            Log.d("ConversationScreen", "New chat: currentUserId=$currentUserId getterUsers=${getterUsers.size}")
        }
    }
    val messagesList by groupConversationViewModel.messages.collectAsStateWithLifecycle()
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
    GroupConversationScreen(
        state = GroupConversationUiState(
            args = args,
            userMap = userMap,
            getterUsers = getterUsers,
            sortedMessages = sortedMessages,
            currentUserId = currentUserId
        ),
        onBackClick = onBackClick,
        onMoreClick = onMoreClick,
        onPlusClick = onPlusClick,
        onCallClick = { isVideo ->
            if (isVideo) onVideoClick() else onPhoneClick()
        },
        onSendMessageClick = {
            if (groupId != null && getterUsers.isNotEmpty()) {
                groupConversationViewModel.sendMessage(
                    groupId = groupId.ifBlank { args.groupId!! },
                    currentUserId = currentUserId,
                    getterUsers = getterUsers,
                    text = it)
            }
        }
    )
}

@Composable
fun GroupConversationScreen(
    state: GroupConversationUiState,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onCallClick: (Boolean) -> Unit,
    onPlusClick: () -> Unit,
    onSendMessageClick: (String) -> Unit
) {
    Scaffold (
        topBar = { ChatTopBar(onBackClick = onBackClick, onMoreClick = onMoreClick) },
        bottomBar = {
            BottomSendMessageView(
                onPlusClick = onPlusClick,
                onSendMessageClick = { text ->
                    onSendMessageClick(text)
                },
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
            InfoChat(onCallClick = onCallClick, getterUsers = state.getterUsers, groupName = state.args.groupName)
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
                    if (message.timestamp != "") {
                        state.currentUserId?.let {
                            val isFirstMessage = index == 0
                            val isLastMessage = index == state.sortedMessages.lastIndex
                            val senderName = state.userMap[message.senderId]?.username ?: stringResource(R.string.unknown)
                            MessageItem(
                                MessageUiModel(
                                    message,
                                    state.currentUserId,
                                    isFirstMessage,
                                    isLastMessage,
                                    senderName,
                                    true
                                )
                            )
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
                Text(text = stringResource(R.string.group),
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
fun InfoChat(onCallClick: (Boolean) -> Unit, getterUsers: List<UsersData?>, groupName: String?){
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
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "${getterUsers.size} " + membersResource,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = mulishFont(),
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row {
            IconButton(onClick = { onCallClick(true) }) {
                Icon(
                    painter = painterResource(R.drawable.camera_icon),
                    contentDescription = "VideoCall",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .width(24.dp)
                        .height(19.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { onCallClick(false) }) {
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