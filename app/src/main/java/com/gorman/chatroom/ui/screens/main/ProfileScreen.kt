package com.gorman.chatroom.ui.screens.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gorman.chatroom.R
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.ui.fonts.mulishFont
import com.gorman.chatroom.ui.components.LeadingIconMenu
import com.gorman.chatroom.ui.screens.auth.DatePickerDocked
import com.gorman.chatroom.ui.screens.auth.DefaultOutlinedTextField
import com.gorman.chatroom.ui.screens.auth.GenderDropDown
import com.gorman.chatroom.viewmodel.MainScreenViewModel
import com.gorman.chatroom.viewmodel.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogoutClick: () -> Unit){
    val profileScreenViewModel: ProfileScreenViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    val userData by profileScreenViewModel.userData.collectAsState()
    val profileItems = profileScreenViewModel.profileItems.value
    val editSheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    Column (modifier = Modifier.fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (modifier = Modifier.size(160.dp)){
            Image(painter = rememberAsyncImagePainter(
                model = userData.profileImageUrl
            ),
                contentDescription = "Profile Avatar",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(160.dp)
                    .clickable(onClick = {}))
            Image(painter = painterResource(R.drawable.edit_ava),
                contentDescription = "Edit Avatar",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .clickable(onClick = {}, indication = null, interactionSource = null))
        }
        Spacer(modifier = Modifier.height(20.dp))
        userData.username?.let {
            Text(text = it,
                fontFamily = mulishFont(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colorResource(R.color.black))
        }
        Spacer(modifier = Modifier.height(20.dp))
        profileItems.forEach { item ->
            if (item.key == R.string.gender) {
                if (item.value == "woman")
                    ProfileItem(item.key, stringResource(R.string.genderWomanValue))
                else
                    ProfileItem(item.key, stringResource(R.string.genderManValue))
            }
            else
                ProfileItem(item.key, item.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        ProfileButtons(
            onClick = {showSheet = !showSheet},
            containerColor = colorResource(R.color.selected_indicator_color),
            icon = painterResource(R.drawable.edit_profile),
            iconTint = colorResource(R.color.white),
            text = "Edit Profile",
            textColor = colorResource(R.color.white),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp))
        ProfileButtons(
            onClick = {
                mainScreenViewModel.setUserId("")
                onLogoutClick()
            },
            containerColor = colorResource(R.color.logout_background_color),
            icon = painterResource(R.drawable.logout_icon),
            iconTint = colorResource(R.color.red_logout_color),
            text = "Logout",
            textColor = colorResource(R.color.red_logout_color),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp))
    }
    if(showSheet) {
        BottomSheetDialog(onDismiss = {showSheet = !showSheet},
            sheetState = editSheetState,
            user = userData,
            onSave = { newUserData->
                userData.userId?.let { userId->
                    profileScreenViewModel.updateUserData(userId, newUserData)
                }
            })
    }
}

@Composable
fun ProfileButtons(onClick: () -> Unit,
                   containerColor: Color,
                   icon: Painter,
                   iconTint: Color,
                   text: String,
                   textColor: Color,
                   modifier: Modifier){
    Button(onClick = {onClick()},
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(12.dp)) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(painter = icon,
                contentDescription = text,
                tint = iconTint)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text,
                fontFamily = mulishFont(),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                color = textColor)
        }
    }
}

@Composable
fun ProfileItem(name: Int, value: String?){
    val context = LocalContext.current
    Row (
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row (modifier = Modifier.weight(1f, false)){
            Text(text = stringResource(name),
                fontFamily = mulishFont(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.unselected_item_color),
                modifier = Modifier.padding(end = 8.dp)
            )
            value?.let {
                Text(text = it,
                    fontFamily = mulishFont(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        value?.let {
            IconButton(onClick = {
                copyToClipboard(context = context, text = it)
            }) {
                Icon(painter = painterResource(R.drawable.copy),
                    contentDescription = "Copy",
                    tint = colorResource(R.color.black))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(onDismiss: () -> Unit, sheetState: SheetState, user: UsersData?, onSave: (UsersData?) -> Unit) {
    var newUsername by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newPhoneCode by remember { mutableStateOf("+375") }
    var newGender by remember { mutableStateOf("") }
    var newBirthday by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    user?.phone?.let {
        newPhone = it.substring(4, it.length)
    }
    user?.username?.let {
        newUsername = it
    }
    user?.gender?.let {
        newGender = if (it == "man") stringResource(R.string.genderManValue)
        else stringResource(R.string.genderWomanValue)
    }
    user?.birthday?.let {
        newBirthday = it
    }
    user?.email?.let {
        newEmail = it
    }

    ModalBottomSheet(
        onDismissRequest = {onDismiss()},
        sheetState = sheetState,
        containerColor = colorResource(R.color.chat_bg)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column (
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ){
                Text(text = stringResource(R.string.enter_username),
                    fontSize = 12.sp,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    modifier = Modifier.padding(8.dp))
                DefaultOutlinedTextField(
                    value = newUsername,
                    onValueChange = {
                        newUsername = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth())
            }
            Column (
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ){
                Text(text = stringResource(R.string.phone),
                    fontSize = 12.sp,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                    value = newPhone,
                    onValueChange = { number->
                        newPhone = number
                    },
                    leadingIcon = { LeadingIconMenu(onItemClick = {newPhoneCode = it}) },
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
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Column (
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ){
                Text(text = stringResource(R.string.gender),
                    fontSize = 12.sp,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    modifier = Modifier.padding(8.dp))
                GenderDropDown(gender = newGender, onGenderChange = {
                    newGender = it },
                    modifier = Modifier.fillMaxWidth())
            }
            Column (
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ){
                Text(text = stringResource(R.string.birthday),
                    fontSize = 12.sp,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    modifier = Modifier.padding(8.dp))
                DatePickerDocked(onBirthdayChange = { newBirthday = it },
                    modifier = Modifier.fillMaxWidth())
            }
            Column (
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ){
                Text(text = stringResource(R.string.email),
                    fontSize = 12.sp,
                    fontFamily = mulishFont(),
                    color = colorResource(R.color.unselected_item_color),
                    modifier = Modifier.padding(8.dp))
                DefaultOutlinedTextField(
                    value = newEmail,
                    onValueChange = {
                        newEmail = it
                    },
                    placeholder = null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth())
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ){
                Button(onClick = {onDismiss()},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.selected_indicator_color).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(32.dp)) {
                    Text(text = stringResource(R.string.cancel),
                        fontFamily = mulishFont(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorResource(R.color.selected_indicator_color),
                        modifier = Modifier.padding(8.dp))
                }
                Button(onClick = {
                    val newUser = user?.copy(
                        username = newUsername,
                        phone = newPhoneCode+newPhone,
                        email = newEmail,
                        gender = newGender,
                        birthday = newBirthday
                    )
                    onDismiss()
                    onSave(newUser) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.selected_indicator_color)),
                    shape = RoundedCornerShape(32.dp)) {
                    Text(text = stringResource(R.string.save),
                        fontFamily = mulishFont(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorResource(R.color.white),
                        modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}