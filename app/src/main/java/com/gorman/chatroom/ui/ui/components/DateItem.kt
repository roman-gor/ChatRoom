package com.gorman.chatroom.ui.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import java.time.LocalDate

@Composable
fun DateItem(date: LocalDate) {
    Row (
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = formatDate(date),
            fontFamily = mulishFont(),
            color = colorResource(R.color.unselected_item_color),
            fontSize = 12.sp)
    }
}