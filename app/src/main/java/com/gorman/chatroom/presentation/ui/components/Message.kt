package com.gorman.chatroom.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.entities.MessagesData
import com.gorman.chatroom.presentation.ui.fonts.mulishFont

@Composable
fun MessageItem(message: MessagesData,
                currentUserId: String,
                isFirstMessage: Boolean,
                isLastMessage: Boolean,
                senderName: String,
                isGroup: Boolean){
    val colorBackground =
        if (message.senderId == currentUserId) colorResource(R.color.own_message)
        else colorResource(R.color.white)

    val colorText =
        if (message.senderId == currentUserId) colorResource(R.color.white)
        else colorResource(R.color.black)

    val colorTime =
        if (message.senderId == currentUserId) colorResource(R.color.chat_bg)
        else colorResource(R.color.not_own_message_time_text)

    val alignment = if (message.senderId == currentUserId) Alignment.CenterEnd else Alignment.CenterStart
    val corners =
        if (message.senderId == currentUserId) {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 0.dp)
        } else {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 16.dp)
        }
    val boxPaddings = when {
        isLastMessage -> {
            if (message.senderId == currentUserId) {
                PaddingValues(bottom = 4.dp, start = 56.dp, end = 24.dp, top = 4.dp)
            } else {
                PaddingValues(bottom = 4.dp, start = 24.dp, end = 56.dp, top = 4.dp)
            }
        }
        isFirstMessage -> {
            if (message.senderId == currentUserId) {
                PaddingValues(bottom = 24.dp, start = 56.dp, end = 24.dp, top = 4.dp)
            } else {
                PaddingValues(bottom = 24.dp, start = 24.dp, end = 56.dp, top = 4.dp)
            }
        }
        else -> {
            if (message.senderId == currentUserId) {
                PaddingValues(bottom = 4.dp, start = 56.dp, end = 24.dp, top = 4.dp)
            } else {
                PaddingValues(bottom = 4.dp, start = 24.dp, end = 56.dp, top = 4.dp)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(boxPaddings),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = colorBackground,
                    shape = corners
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isGroup) {
                if (message.senderId != currentUserId) {
                    Text(
                        text = senderName,
                        fontFamily = mulishFont(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.selected_indicator_color)
                    )
                }
            }
            message.text?.let {
                Text(
                    text = it,
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorText
                )
            }
            Text(
                text = if (message.timestamp != "")
                    formatTimestamp(message.timestamp)
                else "",
                fontFamily = mulishFont(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = colorTime
            )
        }
    }
}