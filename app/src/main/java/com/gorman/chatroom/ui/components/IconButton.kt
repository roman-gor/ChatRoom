package com.gorman.chatroom.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gorman.chatroom.R

@Composable
fun IconButton(icon: Int, onClick: () -> Unit){
    Card(
        modifier = Modifier
            .size(42.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { onClick() })
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ){
            Icon(painter = painterResource(icon),
                contentDescription = "Back",
                tint = colorResource(R.color.black))
        }
    }
}