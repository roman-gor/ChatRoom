package com.gorman.chatroom.ui.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.gorman.core.R
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel
import com.gorman.core.domain.models.ChatPreviewData
import com.gorman.core.domain.models.UsersData
import com.gorman.core.ui.components.ErrorLoading
import com.gorman.core.ui.components.LoadingStub
import com.gorman.core.ui.components.RoundedButton
import com.gorman.core.ui.fonts.mulishFont
import com.gorman.core.ui.theme.ChatRoomTheme
import com.gorman.feature_chats.ui.screens.add.AppTopBar
import com.gorman.feature_chats.ui.states.ChatsUiState
import com.gorman.feature_chats.ui.viewmodels.ChatsScreenViewModel

data class CreateGroupScreenEntry(
    val groupName: String,
    val membersList: String
)

@Composable
fun CreateGroupScreenEntry(
    chatsScreenViewModel: ChatsScreenViewModel = hiltViewModel(),
    mainScreenViewModel: MainScreenViewModel =  hiltViewModel(),
    onBack: () -> Unit,
    onGroupStart: (CreateGroupScreenEntry) -> Unit
) {
    val context = LocalContext.current
    val userId by mainScreenViewModel.userId.collectAsStateWithLifecycle()
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            chatsScreenViewModel.loadAllChats(userId)
            Log.d("Loading chats", "Loading")
        }
    }
    val uiState by chatsScreenViewModel.chatsUiState.collectAsStateWithLifecycle()
    when(val state = uiState) {
        is ChatsUiState.Error -> ErrorLoading(stringResource(R.string.errorGroupsLoading))
        ChatsUiState.Idle -> LoadingStub()
        ChatsUiState.Loading -> LoadingStub()
        is ChatsUiState.Success -> {
            CreateGroupScreen(
                onBack = onBack,
                context = context,
                chatsList = state.chats,
                onGroupStart = onGroupStart
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    context: Context,
    chatsList: List<ChatPreviewData?>,
    onGroupStart: (CreateGroupScreenEntry) -> Unit
){
    var groupName by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val addList = remember { mutableStateListOf<UsersData?>() }
    val addListSize = remember { mutableIntStateOf(addList.size) }
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(title = R.string.add_group, onBack = { onBack() }) }
    ){ innerPaddings ->
        Column (modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddings)
            .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ChatRoomTheme.dimens.paddingExtraLarge,
                        vertical = ChatRoomTheme.dimens.paddingMedium),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ){
                Text(text = stringResource(R.string.groupName),
                    fontFamily = mulishFont(),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(
                        start = ChatRoomTheme.dimens.paddingSmall,
                        bottom = ChatRoomTheme.dimens.paddingSmall))
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { name->
                        groupName = name
                    },
                    textStyle = TextStyle(
                        fontFamily = mulishFont(),
                        fontSize = 14.sp
                    ),
                    placeholder = { Text(text = stringResource(R.string.enterGroupName),
                        fontFamily = mulishFont(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(ChatRoomTheme.dimens.cornerRadius),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ChatRoomTheme.dimens.paddingExtraLarge,
                        vertical = ChatRoomTheme.dimens.paddingMedium),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ){
                Text(text = stringResource(R.string.members),
                    fontFamily = mulishFont(),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(
                        start = ChatRoomTheme.dimens.paddingSmall,
                        bottom = ChatRoomTheme.dimens.paddingSmall))
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            menuExpanded = !menuExpanded
                        }),
                    shape = RoundedCornerShape(ChatRoomTheme.dimens.cornerRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ){
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(painter = painterResource(R.drawable.plus),
                            contentDescription = "Plus",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(ChatRoomTheme.dimens.iconSizeSmall))
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.add_members_to_group),
                            fontFamily = mulishFont(),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp)
                    }
                }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)
                .padding(
                    horizontal = ChatRoomTheme.dimens.paddingExtraLarge,
                    vertical = ChatRoomTheme.dimens.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally) {
                items(addList) { item ->
                    ContactAddedItem(item, onDeleteItem = {addList.remove(item)})
                }
            }
            if (addListSize.intValue > 1) {
                Button(onClick = {
                    if (groupName.isEmpty()) {
                        Toast.makeText(context, "Введите имя группы", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val membersList = addList.mapNotNull { it?.userId }.joinToString(",")
                        onGroupStart(CreateGroupScreenEntry(groupName = groupName, membersList = membersList))
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(36.dp),
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = ChatRoomTheme.dimens.paddingLarge,
                            end = ChatRoomTheme.dimens.paddingLarge,
                            bottom = ChatRoomTheme.dimens.paddingExtraLarge)) {
                    Text(text = stringResource(R.string.add_group),
                        fontFamily = mulishFont(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
    if (menuExpanded) {
        BottomAddMembersSheetDialog(
            onDismiss = {menuExpanded = !menuExpanded},
            sheetState = sheetState,
            chatsList = chatsList,
            onConfirm = {
                addList.clear()
                addList.addAll(it)
                addListSize.intValue = addList.size
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAddMembersSheetDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<UsersData?>) -> Unit,
    sheetState: SheetState,
    chatsList: List<ChatPreviewData?>
) {
    var friendName by remember { mutableStateOf("") }
    val addList = remember { mutableStateListOf<UsersData?>() }
    val isExpanded = sheetState.currentValue == SheetValue.Expanded
    ModalBottomSheet(
        onDismissRequest = {onDismiss()},
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(ChatRoomTheme.dimens.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.add_members_to_group),
                    fontFamily = mulishFont(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(ChatRoomTheme.dimens.paddingLarge))
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { name ->
                        friendName = name
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search_magnifier),
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = mulishFont(),
                        fontSize = 14.sp
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            fontFamily = mulishFont(),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(ChatRoomTheme.dimens.cornerRadius),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(ChatRoomTheme.dimens.paddingLarge))
                if (isExpanded) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(chatsList) { item ->
                            if (item?.user != null) {
                                ContactListItem(
                                    user = item.user,
                                    onBoxChange = { isChecked ->
                                        if (isChecked) {
                                            addList.add(item.user)
                                        } else {
                                            addList.remove(item.user)
                                        }
                                    },
                                    isChecked = addList.contains(item.user)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn {
                        items(chatsList) { item ->
                            if (item?.user != null) {
                                ContactListItem(
                                    user = item.user,
                                    onBoxChange = { isChecked ->
                                        if (isChecked) {
                                            addList.add(item.user)
                                        } else {
                                            addList.remove(item.user)
                                        }
                                    },
                                    isChecked = addList.contains(item.user)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(ChatRoomTheme.dimens.paddingLarge),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    RoundedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        text = R.string.cancel,
                        textColor = MaterialTheme.colorScheme.primary
                    )
                    RoundedButton(
                        onClick = {
                            onConfirm(addList)
                            onDismiss()},
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        text = R.string.add,
                        textColor = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}

@Composable
fun ContactListItem(user: UsersData?,
                    onBoxChange: (Boolean) -> Unit,
                    isChecked: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBoxChange(!isChecked) }
            .padding(horizontal = ChatRoomTheme.dimens.paddingLarge,
                vertical = ChatRoomTheme.dimens.paddingMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            DefaultContactInfo(
                modifier = Modifier.weight(1f, fill = false),
                user = user)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        onBoxChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.background,
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}

@Composable
fun ContactAddedItem(user: UsersData?,
                     onDeleteItem: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = ChatRoomTheme.dimens.paddingLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            DefaultContactInfo(
                modifier = Modifier.weight(1f, fill = false),
                user = user)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card (
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.wrapContentSize()
                        .clickable(onClick = {
                            onDeleteItem()
                        })
                ){
                    Icon(painterResource(R.drawable.plus),
                        contentDescription = "Delete",
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = -45f
                            }
                            .size(24.dp),
                        tint = colorResource(R.color.red_logout_color))
                }
            }
        }
    }
}

@Composable
fun DefaultContactInfo(modifier: Modifier, user: UsersData?) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = user?.profileImageUrl
            ),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(ChatRoomTheme.dimens.avatarSize)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            user?.username?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = mulishFont(),
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            user?.phone?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = mulishFont(),
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
