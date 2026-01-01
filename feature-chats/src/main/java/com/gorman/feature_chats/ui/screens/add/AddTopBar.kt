package com.gorman.feature_chats.ui.screens.add

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
import androidx.compose.material3.MaterialTheme
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
import com.gorman.core.R
import com.gorman.core.ui.fonts.mulishFont
import com.gorman.core.ui.theme.ChatRoomTheme

@Composable
fun AppTopBar(onBack: () -> Unit, title: Int){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(color = MaterialTheme.colorScheme.background),
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
                .padding(
                    top = ChatRoomTheme.dimens.paddingTopTopBar,
                    start = ChatRoomTheme.dimens.paddingExtraLarge,
                    end = ChatRoomTheme.dimens.paddingExtraLarge,
                    bottom = ChatRoomTheme.dimens.paddingBottomTopBar),
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
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = stringResource(title),
                fontFamily = mulishFont(),
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                color = MaterialTheme.colorScheme.secondary
            )
            Box(modifier = Modifier.size(42.dp).background(color = Color.Transparent)) {}
        }
    }
}
