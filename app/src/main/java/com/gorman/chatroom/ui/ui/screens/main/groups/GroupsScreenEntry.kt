package com.gorman.chatroom.ui.ui.screens.main.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.gorman.chatroom.domain.models.GroupPreviewData
import com.gorman.chatroom.ui.states.GroupsUiState
import com.gorman.chatroom.ui.ui.components.ErrorLoading
import com.gorman.chatroom.ui.ui.components.LoadingStub
import com.gorman.chatroom.ui.ui.components.formatMessageTimestamp
import com.gorman.chatroom.ui.ui.components.parseIso
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.ui.screens.main.chats.TextField
import com.gorman.chatroom.ui.ui.theme.ChatRoomTheme
import com.gorman.chatroom.ui.viewmodel.GroupsScreenViewModel
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel

data class GroupEntryData(
    val groupName: String,
    val groupId: String
)

@Composable
fun GroupsScreenEntry(
    groupsViewModel: GroupsScreenViewModel = hiltViewModel(),
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    onItemClick: (GroupEntryData) -> Unit
) {
    val userId by mainScreenViewModel.userId.collectAsStateWithLifecycle()
    val uiState by groupsViewModel.groupUiState.collectAsStateWithLifecycle()
    LaunchedEffect(userId) {
        groupsViewModel.loadAllGroups(userId)
    }
    when(val state = uiState) {
        is GroupsUiState.Error -> ErrorLoading(stringResource(R.string.errorGroupsLoading))
        GroupsUiState.Idle -> LoadingStub()
        GroupsUiState.Loading -> LoadingStub()
        is GroupsUiState.Success -> {
            val sortedGroupsList = state.groups.sortedByDescending {
                parseIso(it.lastMessage?.timestamp)
            }
            GroupsScreen(
                onItemClick = onItemClick,
                sortedGroupsList = sortedGroupsList
            )
        }
    }
}

@Composable
fun GroupsScreen(
    onItemClick: (GroupEntryData) -> Unit,
    sortedGroupsList: List<GroupPreviewData>
) {
    LazyColumn (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        itemsIndexed(sortedGroupsList){ index, item->
            val datetime = formatMessageTimestamp(item.lastMessage?.timestamp)
            GroupPreviewItem(
                item = item,
                datetime = datetime,
                onItemClick = onItemClick)
            if (index < sortedGroupsList.size - 1)
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSecondary
                )
        }
    }
}

@Composable
fun GroupPreviewItem(item: GroupPreviewData?,
                     datetime: String,
                     onItemClick: (GroupEntryData) -> Unit){
    val lastMessage = item?.lastMessage
    val unreadQuantity = item?.unreadQuantity
    val getterUsers = item?.users
    val usersAvatars = mutableListOf<String?>()
    if (getterUsers != null) {
        for (user in getterUsers) {
            usersAvatars.add(user?.profileImageUrl)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    if (item?.groupId != null && getterUsers != null && item.groupName != null) {
                        onItemClick(GroupEntryData(
                            groupName = item.groupName,
                            groupId = item.groupId
                        ))
                    }
                }
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(ChatRoomTheme.dimens.paddingLarge)
    ) {
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)  {
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
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    lastMessage?.text?.let {
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
                        color = MaterialTheme.colorScheme.tertiary,
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
}

@Composable
fun OverlappingAvatars(
    avatarsList: MutableList<String?>
) {
    val avatarSize = ChatRoomTheme.dimens.avatarSize
    val overlapOffset = (-35).dp
    val remainingUsersCount = avatarsList.size - 3
    val displayedAvatars = if (remainingUsersCount <= 0) avatarsList.take(3) else avatarsList.take(2)

    Row(
        modifier = Modifier.width(85.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset)
    ) {
        displayedAvatars.forEach { resId ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(resId)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Avatar",
                placeholder = painterResource(R.drawable.default_ava),
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        if (remainingUsersCount > 0) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${remainingUsersCount + 1}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = mulishFont()
                )
            }
        }
    }
}