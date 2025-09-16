package com.gorman.chatroom.ui.screens.add

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
import androidx.compose.runtime.collectAsState
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
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.ui.components.RoundedButton
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.ChatsScreenViewModel
import com.gorman.chatroom.viewmodel.MainScreenViewModel
import kotlin.collections.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(onBack: () -> Unit,
                      onGroupStart: (String) -> Unit){
    val chatsScreenViewModel: ChatsScreenViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel =  hiltViewModel()
    val userId by mainScreenViewModel.userId.collectAsState()
    var groupName by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val addList = remember { mutableStateListOf<UsersData?>() }
    val addListSize = remember { mutableIntStateOf(addList.size) }
    val context = LocalContext.current
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            chatsScreenViewModel.getUserChats(userId)
            Log.d("Loading chats", "Loading")
        }
    }
    val chatsList by chatsScreenViewModel.chatsList.collectAsState()
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(title = R.string.add_group, onBack = { onBack() }) }
    ){ innerPaddings ->
        Column (modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddings)
            .background(color = colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ){
                Text(text = stringResource(R.string.groupName),
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.placeholder_message),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
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
                        color = colorResource(R.color.unselected_item_color)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        focusedBorderColor = colorResource(R.color.unselected_item_color)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ){
                Text(text = stringResource(R.string.members),
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.placeholder_message),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            menuExpanded = !menuExpanded
                        }),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.selected_indicator_color).copy(alpha = 0.1f),
                        contentColor = colorResource(R.color.selected_indicator_color)
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
                            tint = colorResource(R.color.selected_indicator_color),
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.add_members_to_group),
                            fontFamily = mulishFont(),
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.selected_indicator_color),
                            fontSize = 16.sp)
                    }
                }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 32.dp, vertical = 16.dp),
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
                        val serialized = "groupName=${groupName};currentUserId=${userId};getterUsers=$membersList"
                        onGroupStart(serialized)
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.selected_indicator_color)
                    ),
                    shape = RoundedCornerShape(36.dp),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 32.dp)) {
                    Text(text = stringResource(R.string.add_group),
                        fontFamily = mulishFont(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(R.color.white),
                        modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
    if (menuExpanded) {
        BottomAddMembersSheetDialog(
            onDismiss = {menuExpanded = !menuExpanded},
            sheetState = sheetState,
            chatsScreenViewModel = chatsScreenViewModel,
            chatsList = chatsList,
            userId = userId,
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
    chatsList: List<ChatsData?>,
    chatsScreenViewModel: ChatsScreenViewModel,
    userId: String) {
    var friendName by remember { mutableStateOf("") }
    val addList = remember { mutableStateListOf<UsersData?>() }
    val isExpanded = sheetState.currentValue == SheetValue.Expanded
    ModalBottomSheet(
        onDismissRequest = {onDismiss()},
        sheetState = sheetState,
        containerColor = colorResource(R.color.chat_bg)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.add_members_to_group),
                    fontFamily = mulishFont(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = colorResource(R.color.black)
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { name ->
                        friendName = name
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search_magnifier),
                            contentDescription = "Search",
                            tint = colorResource(R.color.unselected_item_color),
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
                            color = colorResource(R.color.unselected_item_color)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        focusedBorderColor = colorResource(R.color.unselected_item_color)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
                if (isExpanded) {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(chatsList) { item ->
                            LaunchedEffect(item?.chatId, item?.lastMessageId) {
                                if (item?.chatId != null && item.lastMessageId != null) {
                                    chatsScreenViewModel.initChatPreview(item.chatId, userId)
                                    Log.d("Item", item.chatId)
                                }
                            }
                            val chatMap by chatsScreenViewModel.chatPreviews.collectAsState()
                            val user = chatMap[item?.chatId]?.user
                            if (user != null) {
                                ContactListItem(
                                    user = user,
                                    onBoxChange = { isChecked ->
                                        if (isChecked) {
                                            addList.add(user)
                                        } else {
                                            addList.remove(user)
                                        }
                                    },
                                    isChecked = addList.contains(user)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn {
                        items(chatsList) { item ->
                            LaunchedEffect(item?.chatId, item?.lastMessageId) {
                                if (item?.chatId != null && item.lastMessageId != null) {
                                    chatsScreenViewModel.initChatPreview(item.chatId, userId)
                                    Log.d("Item", item.chatId)
                                }
                            }
                            val chatMap by chatsScreenViewModel.chatPreviews.collectAsState()
                            val user = chatMap[item?.chatId]?.user
                            if (user != null) {
                                ContactListItem(
                                    user = user,
                                    onBoxChange = { isChecked ->
                                        if (isChecked) {
                                            addList.add(user)
                                        } else {
                                            addList.remove(user)
                                        }
                                    },
                                    isChecked = addList.contains(user)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    RoundedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        color = colorResource(R.color.selected_indicator_color).copy(alpha = 0.1f),
                        text = R.string.cancel,
                        textColor = colorResource(R.color.selected_indicator_color)
                    )
                    RoundedButton(
                        onClick = {
                            onConfirm(addList)
                            onDismiss()},
                        modifier = Modifier.weight(1f),
                        color = colorResource(R.color.selected_indicator_color),
                        text = R.string.add,
                        textColor = colorResource(R.color.white)
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
            .padding(bottom = 16.dp)
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
                        checkmarkColor = colorResource(R.color.white),
                        checkedColor = colorResource(R.color.selected_indicator_color),
                        uncheckedColor = colorResource(R.color.unselected_item_color)
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
            .padding(bottom = 16.dp)
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = mulishFont(),
                    color = Color.Black,
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
                    color = colorResource(R.color.unselected_item_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}