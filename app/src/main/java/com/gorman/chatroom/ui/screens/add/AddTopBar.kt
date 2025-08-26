package com.gorman.chatroom.ui.screens.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.fonts.mulishFont

@Composable
fun AppTopBar(onBack: () -> Unit, title: Int){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.topbarbg),
            contentDescription = "TopBar Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            onBack()
                        },
                        indication = null,
                        interactionSource = null
                    )
                    .size(42.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color.White
                )
            }
            Text(
                text = stringResource(title),
                fontFamily = mulishFont(),
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                color = Color.White
            )
            Box(modifier = Modifier.size(42.dp).background(color = Color.Transparent)) {}
        }
    }
}