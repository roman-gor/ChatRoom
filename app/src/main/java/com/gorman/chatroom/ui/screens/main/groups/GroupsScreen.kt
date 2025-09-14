package com.gorman.chatroom.ui.screens.main.groups

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import com.gorman.chatroom.data.GroupPreviewData
import com.gorman.chatroom.data.GroupsData
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.ui.screens.main.chats.TextField
import com.gorman.chatroom.ui.screens.main.chats.formatMessageTimestamp
import com.gorman.chatroom.ui.screens.main.chats.parseIso
import com.gorman.chatroom.viewmodel.GroupsScreenViewModel
import com.gorman.chatroom.viewmodel.MainScreenViewModel
import kotlin.collections.get

@Composable
fun GroupsScreen() {
    val groupsViewModel: GroupsScreenViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel =  hiltViewModel()
    val userId by mainScreenViewModel.userId.collectAsState()
    LaunchedEffect(userId) {
        groupsViewModel.getUserGroups(userId)
    }
    val groupsList = groupsViewModel.groupsState.collectAsState().value
    val sortedGroupsList = groupsList.sortedByDescending {
        parseIso(it?.lastMessageTimestamp)
    }
    LazyColumn (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        items(sortedGroupsList){ item->
            LaunchedEffect(item?.groupId, item?.lastMessageId) {
                if (item?.groupId != null && item.lastMessageId != null) {
                    groupsViewModel.initGroupPreview(
                        userId,
                        item.groupId
                    )
                    Log.d("Item", item.groupId)
                }
            }
            val datetime = formatMessageTimestamp(item?.lastMessageTimestamp)
            val chatMap = groupsViewModel.groupPreview.collectAsState().value
            val preview = chatMap[item?.groupId]
            val text = preview?.lastMessage?.text
            Log.d("Text", "$text")
            Log.d("Getters", "${chatMap[item?.groupId]?.users?.size}")
            GroupPreviewItem(
                item = item,
                chatMap = chatMap,
                datetime = datetime,
                onItemClick = {})
        }
    }
}

@Composable
fun GroupPreviewItem(item: GroupsData?,
                     chatMap: Map<String, GroupPreviewData>,
                     datetime: String,
                     onItemClick: () -> Unit){
    val lastMessage = chatMap[item?.groupId]?.lastMessage
    val unreadQuantity = chatMap[item?.groupId]?.unreadQuantity
    val getterUsers = chatMap[item?.groupId]?.users
    val usersAvatars = mutableListOf<String?>()
    if (getterUsers != null) {
        for (user in getterUsers) {
            usersAvatars.add(user?.profileImageUrl)
        }
    }
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 30.dp, top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row (
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.Center
        ){
            OverlappingAvatars(usersAvatars)
            Spacer(modifier = Modifier.width(12.dp))
            Column (
                verticalArrangement = Arrangement.Center
            ){
                item?.groupName?.let {
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
                if (unreadQuantity != 0) {
                    TextField(unreadQuantity.toString())
                } else {
                    Spacer(modifier = Modifier.height(27.dp))
                }
            }
        }
    }
}

@Composable
fun OverlappingAvatars(
    avatarsList: MutableList<String?>
) {
    val avatarSize = 50.dp
    val overlapOffset = (-15 * avatarsList.size).dp
    var counter = 0

    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(overlapOffset)
    ) {
        avatarsList.forEach { resId ->
            if (counter < 3) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = resId
                    ),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
                counter++
            }
        }
    }
}