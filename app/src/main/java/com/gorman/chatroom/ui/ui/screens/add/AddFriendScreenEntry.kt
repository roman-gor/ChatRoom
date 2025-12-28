package com.gorman.chatroom.ui.ui.screens.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.ui.components.LeadingIconMenu
import com.gorman.chatroom.ui.ui.theme.ChatRoomTheme
import com.gorman.chatroom.ui.viewmodel.AddFriendViewModel

@Composable
fun AddFriendScreenEntry(
    addFriendViewModel: AddFriendViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onStartChatClick: (String) -> Unit
){
    val user by addFriendViewModel.usersData.collectAsStateWithLifecycle()
    val currentUser by addFriendViewModel.currentUserData.collectAsStateWithLifecycle()
    AddFriendScreen(
        onBack = onBack,
        user = user,
        currentUser = currentUser,
        onStartChatClick = onStartChatClick,
        onFindUserClick = { number ->
            addFriendViewModel.findUserByPhoneNumber(number)
        }
    )
}

@Composable
fun AddFriendScreen(
    onBack: () -> Unit,
    user: UsersData?,
    currentUser: UsersData?,
    onStartChatClick: (String) -> Unit,
    onFindUserClick: (String) -> Unit
){
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCode by remember { mutableStateOf("+375") }
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(title = R.string.add_friend, onBack = { onBack() }) }
    ){ innerPaddings ->
        Column (modifier = Modifier.fillMaxSize()
            .padding(innerPaddings)
            .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { number->
                    phoneNumber = number
                    onFindUserClick(phoneCode + number)
                },
                leadingIcon = { LeadingIconMenu(onItemClick = {phoneCode = it}) },
                textStyle = TextStyle(
                    fontFamily = mulishFont(),
                    fontSize = 14.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(ChatRoomTheme.dimens.cornerRadius),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.padding(ChatRoomTheme.dimens.paddingExtraLarge).fillMaxWidth(),
                singleLine = true
            )
            if (user?.userId != null && user.phone != currentUser?.phone && currentUser?.userId != null) {
                UserInfo(getterUser = user, onStartChatClick = onStartChatClick)
            }
            else {
                Placeholder()
            }
        }
    }
}

@Composable
fun UserInfo(getterUser: UsersData?, onStartChatClick: (String) -> Unit) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(
            start = ChatRoomTheme.dimens.paddingExtraLarge,
            end = ChatRoomTheme.dimens.paddingExtraLarge,
            bottom = ChatRoomTheme.dimens.paddingMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row (
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = rememberAsyncImagePainter(
                    model = getterUser?.profileImageUrl
                ),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size = ChatRoomTheme.dimens.avatarSize)
                    .clip(CircleShape))
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
                        color = Color.Black,
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
            IconButton(onClick = { onStartChatClick(getterUser?.userId ?: "") }) {
                Icon(painter = painterResource(R.drawable.start_chat),
                    contentDescription = "VideoCall",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .width(22.dp)
                        .height(24.dp))
            }
        }
    }
}

@Composable
fun Placeholder(){
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Icon(painter = painterResource(R.drawable.background_addfriend_placeholder),
            contentDescription = "Background Placeholder",
            modifier = Modifier.width(287.dp).height(240.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(100.dp))
    }
}