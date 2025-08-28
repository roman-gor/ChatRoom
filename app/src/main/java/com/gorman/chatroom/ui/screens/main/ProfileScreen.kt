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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.data.profileItemsList
import com.gorman.chatroom.ui.fonts.mulishFont

@Composable
fun ProfileScreen(){
    Column (modifier = Modifier.fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (modifier = Modifier.size(160.dp)){
            Image(painter = painterResource(R.drawable.profile_ava),
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
        Text(text = "John Lennon",
            fontFamily = mulishFont(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colorResource(R.color.black))
        Spacer(modifier = Modifier.height(20.dp))
        profileItemsList.forEach { item ->
            ProfileItem(item.name, item.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        ProfileButtons()
    }
}

@Composable
fun ProfileButtons(){
    Button(onClick = {},
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.selected_indicator_color)
        ),
        shape = RoundedCornerShape(12.dp)) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(painter = painterResource(R.drawable.edit_profile),
                contentDescription = "Edit Profile",
                tint = colorResource(R.color.white))
            Spacer(modifier = Modifier.width(20.dp))
            Text("Edit Profile",
                fontFamily = mulishFont(),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                color = colorResource(R.color.white))
        }
    }
    Button(onClick = {},
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.logout_background_color)
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(R.drawable.logout_icon),
                contentDescription = "Logout",
                tint = colorResource(R.color.red_logout_color))
            Spacer(modifier = Modifier.width(20.dp))
            Text("Logout",
                fontFamily = mulishFont(),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                color = colorResource(R.color.red_logout_color)
            )
        }
    }
}

@Composable
fun ProfileItem(name: Int, value: String){
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
            Text(text = value,
                fontFamily = mulishFont(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(onClick = {
            copyToClipboard(context = context, text = value)
        }) {
            Icon(painter = painterResource(R.drawable.copy),
                contentDescription = "Copy",
                tint = colorResource(R.color.black))
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}