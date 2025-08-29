package com.gorman.chatroom.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.data.PeopleChatsDummyData
import com.gorman.chatroom.data.PeopleChatsList
import com.gorman.chatroom.data.avatars
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.ui.screens.main.chats.TextField

@Composable
fun GroupsScreen() {
    LazyColumn (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        items(PeopleChatsList){ item->
            GroupPreviewItem(item) { }
        }
    }
}

@Composable
fun GroupPreviewItem(item: PeopleChatsDummyData, onItemClick: () -> Unit){
    Row (modifier = Modifier.fillMaxWidth()
        .padding(start = 16.dp, end = 30.dp, top = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row (
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.Center
        ){
            OverlappingAvatars(avatars)
            Spacer(modifier = Modifier.width(12.dp))
            Column (
                verticalArrangement = Arrangement.Center
            ){
                Text(text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = mulishFont(),
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = item.message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Column (
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.End
            ){
                Text(text = "09:25",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = mulishFont())
                TextField("15")
            }
        }
    }
}

@Composable
fun OverlappingAvatars(
    avatarsList: List<Int>
) {
    val avatarSize = 50.dp
    val overlapOffset = (-40).dp

    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(overlapOffset) // аватары будут перекрываться
    ) {
        listOf(avatarsList[0],avatarsList[1],avatarsList[2]).forEach { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}