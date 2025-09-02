package com.gorman.chatroom.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.ui.screens.add.LeadingIconMenu
import com.gorman.chatroom.viewmodel.MainScreenViewModel

@Composable
fun LoginScreen(onStartClick: () -> Unit) {
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCode by remember { mutableStateOf("+375") }
    var alpha by remember { mutableFloatStateOf(0.2f) }
    val userId = mainScreenViewModel.userId.collectAsState().value
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            Log.d("UserLogin", "UserID: $userId")
            onStartClick()
        }
    }
    Column(modifier = Modifier.systemBarsPadding().fillMaxSize().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.login),
            fontFamily = mulishFont(),
            fontSize = 42.sp,
            color = colorResource(R.color.selected_indicator_color),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.enter_phone_for_sms),
            fontFamily = mulishFont(),
            fontSize = 16.sp,
            color = colorResource(R.color.unselected_item_color),
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { number->
                phoneNumber = number
                alpha = if ((phoneCode+phoneNumber).length >= 13) 1f else 0.2f
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp).fillMaxWidth(),
            singleLine = true
        )
        Image(painter = painterResource(R.drawable.start_button),
            contentDescription = "Start",
            modifier = Modifier.size(80.dp)
                .background(color = Color.Transparent, shape = CircleShape)
                .clickable(
                    onClick = {
                        if ((phoneCode+phoneNumber).length >= 13) {
                            Log.d("UserLogin", "UserID: ${mainScreenViewModel.userId.value}")
                            mainScreenViewModel.findUserByPhoneNumber(phoneCode + phoneNumber)
                        }
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            alpha = alpha)
    }
}