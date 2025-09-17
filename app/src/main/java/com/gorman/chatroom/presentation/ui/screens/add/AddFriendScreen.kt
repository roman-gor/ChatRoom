package com.gorman.chatroom.presentation.ui.screens.add

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.presentation.ui.fonts.mulishFont
import com.gorman.chatroom.presentation.ui.components.LeadingIconMenu
import com.gorman.chatroom.presentation.viewmodel.AddFriendViewModel

@Composable
fun AddFriendScreen(onBack: () -> Unit, onStartChatClick: (String) -> Unit){
    val addFriendViewModel: AddFriendViewModel = hiltViewModel()
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCode by remember { mutableStateOf("+375") }
    val user = addFriendViewModel.usersData.collectAsState().value
    val currentUser = addFriendViewModel.currentUserData.collectAsState().value
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(title = R.string.add_friend, onBack = { onBack() }) }
    ){ innerPaddings ->
        Column (modifier = Modifier.fillMaxSize()
            .padding(innerPaddings)
            .background(color = colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { number->
                    phoneNumber = number
                    addFriendViewModel.findUserByPhoneNumber(phoneCode + number)
                },
                leadingIcon = { LeadingIconMenu(onItemClick = {phoneCode = it}) },
                textStyle = TextStyle(
                    fontFamily = mulishFont(),
                    fontSize = 14.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.black),
                    focusedBorderColor = colorResource(R.color.unselected_item_color)
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp).fillMaxWidth(),
                singleLine = true
            )
            if (user?.userId != null && user.phone != currentUser?.phone && currentUser?.userId != null) {
                UserInfo(getterUser = user, onStartChatClick = onStartChatClick, userId = currentUser.userId)
            }
            else {
                Placeholder()
            }
        }
    }
}

@Composable
fun UserInfo(getterUser: UsersData?, onStartChatClick: (String) -> Unit, userId: String) {
    val forChatDataMap = mapOf(
        "currentUserId" to userId,
        "getterUserId" to getterUser?.userId
    )
    val serialized = forChatDataMap.entries.joinToString(";") { "${it.key}=${it.value}" }
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(start = 32.dp, end = 32.dp, bottom = 8.dp),
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
                    .size(size = 50.dp)
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
                        color = colorResource(R.color.unselected_item_color),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Row {
            IconButton(onClick = { onStartChatClick(serialized) }) {
                Icon(painter = painterResource(R.drawable.start_chat),
                    contentDescription = "VideoCall",
                    tint = colorResource(R.color.selected_indicator_color),
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
            tint = colorResource(R.color.selected_indicator_color).copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(100.dp))
    }
}