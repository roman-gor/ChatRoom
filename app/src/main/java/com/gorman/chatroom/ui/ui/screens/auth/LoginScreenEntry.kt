package com.gorman.chatroom.ui.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.chatroom.R
import com.gorman.chatroom.ui.ui.fonts.mulishFont
import com.gorman.chatroom.ui.ui.components.LeadingIconMenu
import com.gorman.chatroom.ui.ui.theme.ChatRoomTheme
import com.gorman.chatroom.ui.viewmodel.MainScreenViewModel

@Composable
fun LoginScreenEntry(
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    onStartClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val userId by mainScreenViewModel.userId.collectAsStateWithLifecycle()
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            Log.d("UserLogin", "UserID: $userId")
            onStartClick()
        }
    }
    LoginScreen(
        onStartClick = { phone ->
            mainScreenViewModel.findUserByPhoneNumber(phone)
            Log.d("UserLogin", "UserID: ${mainScreenViewModel.userId.value}")
        },
        onSignUpClick = onSignUpClick
    )
}

@Composable
fun LoginScreen(
    onStartClick: (String) -> Unit,
    onSignUpClick: () -> Unit,
) {
    var phoneNumber by remember { mutableStateOf("") }
    var phoneCode by remember { mutableStateOf("+375") }
    var alpha by remember { mutableFloatStateOf(0.2f) }
    Column(modifier = Modifier.systemBarsPadding().fillMaxSize().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.login),
            fontFamily = mulishFont(),
            fontSize = 42.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.enter_phone_for_sms),
            fontFamily = mulishFont(),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.tertiary,
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
                focusedTextColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.tertiary
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(
                horizontal = ChatRoomTheme.dimens.paddingExtraLarge,
                vertical = ChatRoomTheme.dimens.paddingLarge
            ).fillMaxWidth(),
            singleLine = true
        )
        ConfirmAndSignUpButton(onSignUpClick = {onSignUpClick()}, onStartClick = {
            if ((phoneCode+phoneNumber).length >= 13) { onStartClick(phoneCode + phoneNumber) }
        }, alpha = alpha)
    }
}

@Composable
fun ConfirmAndSignUpButton(onSignUpClick: () -> Unit, onStartClick: () -> Unit, alpha: Float) {
    Row (modifier = Modifier.fillMaxWidth().padding(top = ChatRoomTheme.dimens.paddingLarge),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Button(
            onClick = {
                onSignUpClick()
            },
            modifier = Modifier.height(50.dp),
            elevation = ButtonDefaults.buttonElevation(6.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(R.string.sign_up),
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(25.dp))
        Image(painter = painterResource(R.drawable.start_button),
            contentDescription = "Start",
            modifier = Modifier.size(80.dp)
                .background(color = Color.Transparent, shape = CircleShape)
                .clickable(
                    onClick = {
                        onStartClick()
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            alpha = alpha)
    }
}