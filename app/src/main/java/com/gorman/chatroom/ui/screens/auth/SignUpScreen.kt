package com.gorman.chatroom.ui.screens.auth

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.R
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.ui.screens.add.LeadingIconMenu
import com.gorman.chatroom.viewmodel.MainScreenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("ContextCastToActivity")
@Composable
fun SignUpScreen(onStartClick: () -> Unit, onLoginClick: () -> Unit) {
    val context = LocalContext.current

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    val userId = mainScreenViewModel.userId.collectAsState().value
    val isAllDataLoaded = mainScreenViewModel.isUserDataLoaded.collectAsState().value
    val isPhoneNumberExist = mainScreenViewModel.isPhoneNumberExist.collectAsState().value

    var phoneNumber by remember { mutableStateOf("") }
    var phoneCode by remember { mutableStateOf("+375") }
    var userName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var alpha by remember { mutableFloatStateOf(0.2f) }
    var isPhoneNumberTrue by remember { mutableStateOf(false) }

    LaunchedEffect(userId, isPhoneNumberExist) {
        if (isAllDataLoaded) {
            onStartClick()
        }
        when (isPhoneNumberExist) {
            true -> Toast.makeText(context, "Пользователь с таким номером существует!", Toast.LENGTH_LONG).show()
            false -> isPhoneNumberTrue = true
            null -> {}
        }
    }
    Column(modifier = Modifier.systemBarsPadding().fillMaxSize().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.sign_up),
            fontFamily = mulishFont(),
            fontSize = 26.sp,
            color = colorResource(R.color.selected_indicator_color),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isPhoneNumberTrue) {
                Text(text = stringResource(R.string.phone),
                    fontFamily = mulishFont(),
                    fontSize = 16.sp,
                    color = colorResource(R.color.unselected_item_color),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 2.dp))
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
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 2.dp).fillMaxWidth(),
                    singleLine = true
                )
            }
                //TODO CODE FOR SMS AUTH
//            else if (isPhoneNumberTrue.value) {
//                Text(text = stringResource(R.string.enter_sms),
//                    fontFamily = mulishFont(),
//                    fontSize = 16.sp,
//                    color = colorResource(R.color.unselected_item_color),
//                    fontWeight = FontWeight.Medium,
//                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 2.dp))
//                OutlinedTextField(
//                    value = smsCode,
//                    onValueChange = { code->
//                        smsCode = code
//                    },
//                    leadingIcon = { LeadingIconMenu(onItemClick = {phoneCode = it}) },
//                    textStyle = TextStyle(
//                        fontFamily = mulishFont(),
//                        fontSize = 14.sp
//                    ),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedTextColor = colorResource(R.color.black),
//                        focusedBorderColor = colorResource(R.color.unselected_item_color)
//                    ),
//                    shape = RoundedCornerShape(12.dp),
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 2.dp).fillMaxWidth(),
//                    singleLine = true
//                )
//            }
            //TODO END CODE FOR SMS AUTH
            else {
                ItemsAfterPhoneNumber(
                    onUserNameChange = {userName = it},
                    onGenderChange = {gender = it},
                    onBirthdayChange = {birthday = it},
                    onEmailChange = {email = it}
                )
            }
        }
        ConfirmAndLoginButton(
            onLoginClick = { onLoginClick() },
            onStartClick = {
                if (!isPhoneNumberTrue) {
                    if ((phoneCode + phoneNumber).length >= 13) {
                        val phone = phoneCode + phoneNumber
                        mainScreenViewModel.findUserByPhoneNumber(phone)
                    }
                }
                else {
                    if (userName.isNotEmpty() && gender.isNotEmpty() && birthday.isNotEmpty() && email.isNotEmpty()) {
                        val user = UsersData(
                            username = userName,
                            birthday = birthday,
                            email = email,
                            phone = phoneNumber,
                            gender = gender,
                            profileImageUrl = "https://st3.depositphotos.com/19428878/37137/v/450/depositphotos_371377450-stock-illustration-default-avatar-profile-image-vector.jpg",
                            lastSeen = "",
                            groups = mapOf(),
                            chats = mapOf(),
                            userId = userId,
                            unreadMessagesCount = 0
                        )
                        mainScreenViewModel.loadNewUser(user)
                    }
                }
            },
            alpha = alpha)
    }
}

@Composable
fun ConfirmAndLoginButton(onLoginClick: () -> Unit, onStartClick: () -> Unit, alpha: Float) {
    Row (modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        Button(
            onClick = {
                onLoginClick()
            },
            modifier = Modifier.height(50.dp),
            elevation = ButtonDefaults.buttonElevation(6.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.selected_indicator_color)
            )
        ) {
            Text(text = stringResource(R.string.login),
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = colorResource(R.color.white),
                fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(50.dp))
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

@Composable
fun ItemsAfterPhoneNumber(
    onUserNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onBirthdayChange: (String) -> Unit,
    onEmailChange: (String) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DefaultOutlinedTextField(
            value = userName,
            onValueChange = {
                userName = it
                onUserNameChange(it)
            },
            placeholder = stringResource(R.string.enter_username),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
        DatePickerDocked(onBirthdayChange = {onBirthdayChange(it)})
        DefaultOutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onEmailChange(it)
            },
            placeholder = stringResource(R.string.email),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        GenderDropDown(gender = gender, onGenderChange = {
            gender = it
            onGenderChange(it) })
    }
}

@Composable
fun DefaultOutlinedTextField(value: String, onValueChange: (String) -> Unit, placeholder: String? = null, keyboardOptions: KeyboardOptions) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        textStyle = TextStyle(
            fontFamily = mulishFont(),
            fontSize = 14.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(R.color.black),
            focusedBorderColor = colorResource(R.color.unselected_item_color)
        ),
        placeholder = {
            if (placeholder != null) {
                Text(text = placeholder,
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    color = colorResource(R.color.unselected_item_color),
                    fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp).fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(onBirthdayChange: (String) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                onBirthdayChange(selectedDate)
            },
            textStyle = TextStyle(
                fontFamily = mulishFont(),
                fontSize = 14.sp
            ),
            placeholder = {
                Text(text = stringResource(R.string.birthday),
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    color = colorResource(R.color.unselected_item_color),
                    fontWeight = FontWeight.Medium)
            },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_icon),
                        contentDescription = "Select date",
                        tint = colorResource(R.color.black)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorResource(R.color.black),
                focusedBorderColor = colorResource(R.color.unselected_item_color)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp),
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            containerColor = colorResource(R.color.white),
                            todayDateBorderColor = colorResource(R.color.selected_indicator_color),
                            dayInSelectionRangeContainerColor = colorResource(R.color.selected_indicator_color),
                            selectedYearContainerColor = colorResource(R.color.selected_indicator_color),
                            selectedDayContainerColor = colorResource(R.color.selected_indicator_color)
                        ),
                        title = {
                            Text(text = "Введите дату",
                                fontFamily = mulishFont(),
                                fontSize = 14.sp,
                                color = colorResource(R.color.black),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                        },
                        headline = {
                            Text(text = datePickerState.selectedDateMillis?.let {
                                convertMillisToDate(it)
                            } ?: "",
                                fontFamily = mulishFont(),
                                fontSize = 26.sp,
                                color = colorResource(R.color.black),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp))
                        }
                    )
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun GenderDropDown(gender: String, onGenderChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val womanGender = stringResource(R.string.genderWomanValue)
    val manGender = stringResource(R.string.genderManValue)
    Box (
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        OutlinedTextField(
            value = gender,
            onValueChange = {
                onGenderChange(it)
            },
            textStyle = TextStyle(
                fontFamily = mulishFont(),
                fontSize = 14.sp
            ),
            placeholder = {
                Text(text = stringResource(R.string.gender),
                    fontFamily = mulishFont(),
                    fontSize = 14.sp,
                    color = colorResource(R.color.unselected_item_color),
                    fontWeight = FontWeight.Medium)
            },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select date",
                        tint = colorResource(R.color.black)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorResource(R.color.black),
                focusedBorderColor = colorResource(R.color.unselected_item_color)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = !expanded},
            shape = RoundedCornerShape(20.dp),
            offset = DpOffset(x = 150.dp, y = (-20).dp),
            modifier = Modifier.wrapContentHeight().width(200.dp),
            containerColor = colorResource(R.color.white)
        ) {
            DropdownMenuItem(
                text = {
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = womanGender,
                            fontFamily = mulishFont(),
                            fontSize = 20.sp,
                            color = colorResource(R.color.black),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = colorResource(R.color.chat_bg)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    onGenderChange(womanGender)
                }
            )
            DropdownMenuItem(
                text = {
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = manGender,
                            fontFamily = mulishFont(),
                            fontSize = 20.sp,
                            color = colorResource(R.color.black),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    onGenderChange(manGender)
                }
            )
        }
    }
}