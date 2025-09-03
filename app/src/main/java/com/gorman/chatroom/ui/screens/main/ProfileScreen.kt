package com.gorman.chatroom.ui.screens.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.viewmodel.MainScreenViewModel
import com.gorman.chatroom.viewmodel.ProfileScreenViewModel

@Composable
fun ProfileScreen(){
    val profileScreenViewModel: ProfileScreenViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    val userData by profileScreenViewModel.userData.collectAsState()
    val profileItems = profileScreenViewModel.profileItems.value
    Column (modifier = Modifier.fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (modifier = Modifier.size(160.dp)){
            Image(painter = rememberAsyncImagePainter(
                model = userData.profileImageUrl
            ),
                contentDescription = "Profile Avatar",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(160.dp)
                    .clickable(onClick = {}))
            Image(painter = painterResource(R.drawable.edit_ava),
                contentDescription = "Edit Avatar",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .clickable(onClick = {}, indication = null, interactionSource = null))
        }
        Spacer(modifier = Modifier.height(20.dp))
        userData.username?.let {
            Text(text = it,
                fontFamily = mulishFont(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colorResource(R.color.black))
        }
        Spacer(modifier = Modifier.height(20.dp))
        profileItems.forEach { item ->
            if (item.key == R.string.gender) {
                if (item.value == "woman")
                    ProfileItem(item.key, stringResource(R.string.genderWomanValue))
                else
                    ProfileItem(item.key, stringResource(R.string.genderManValue))
            }
            else
                ProfileItem(item.key, item.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        ProfileButtons(
            onClick = {},
            containerColor = colorResource(R.color.selected_indicator_color),
            icon = painterResource(R.drawable.edit_profile),
            iconTint = colorResource(R.color.white),
            text = "Edit Profile",
            textColor = colorResource(R.color.white),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp))
        ProfileButtons(
            onClick = {
                mainScreenViewModel.setUserId("")
            },
            containerColor = colorResource(R.color.logout_background_color),
            icon = painterResource(R.drawable.logout_icon),
            iconTint = colorResource(R.color.red_logout_color),
            text = "Logout",
            textColor = colorResource(R.color.red_logout_color),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp))
    }
}

@Composable
fun ProfileButtons(onClick: () -> Unit,
                   containerColor: Color,
                   icon: Painter,
                   iconTint: Color,
                   text: String,
                   textColor: Color,
                   modifier: Modifier){
    Button(onClick = {onClick()},
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(12.dp)) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(painter = icon,
                contentDescription = text,
                tint = iconTint)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text,
                fontFamily = mulishFont(),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                color = textColor)
        }
    }
}

@Composable
fun ProfileItem(name: Int, value: String?){
    val context = LocalContext.current
    Row (
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row (modifier = Modifier.weight(1f, false)){
            Text(text = stringResource(name),
                fontFamily = mulishFont(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.unselected_item_color),
                modifier = Modifier.padding(end = 8.dp)
            )
            value?.let {
                Text(text = it,
                    fontFamily = mulishFont(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        value?.let {
            IconButton(onClick = {
                copyToClipboard(context = context, text = it)
            }) {
                Icon(painter = painterResource(R.drawable.copy),
                    contentDescription = "Copy",
                    tint = colorResource(R.color.black))
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}