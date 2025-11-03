package com.gorman.chatroom.ui.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.ui.ui.fonts.mulishFont

@Composable
fun RoundedButton(onClick: () -> Unit, modifier: Modifier, color: Color, text: Int, textColor: Color) {
    Button(onClick = { onClick() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = color),
        shape = RoundedCornerShape(32.dp)) {
        Text(text = stringResource(text),
            fontFamily = mulishFont(),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = textColor,
            modifier = Modifier.padding(8.dp))
    }
}