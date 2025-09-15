package com.gorman.chatroom.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.chatroom.R
import com.gorman.chatroom.data.flagsList
import com.gorman.chatroom.ui.fonts.mulishFont

@Composable
fun LeadingIconMenu(onItemClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableIntStateOf(R.drawable.belarus) }
    var phoneCodePlaceholder by remember { mutableStateOf("+375") }
    Button(onClick = {expanded = !expanded},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(modifier = Modifier.width(16.dp))
            Image(painter = painterResource(selectedCountry),
                contentDescription = "Country Flag")
            Icon(imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Drop Down",
                tint = colorResource(R.color.black)
            )
            Text(text = phoneCodePlaceholder,
                fontFamily = mulishFont(),
                fontSize = 14.sp,
                color = colorResource(R.color.placeholder_message),
                modifier = Modifier.padding(start = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
    DropdownMenu(expanded = expanded,
        onDismissRequest = {expanded = !expanded},
        scrollState = rememberScrollState(),
        modifier = Modifier.height(150.dp).wrapContentWidth(),
        containerColor = colorResource(R.color.white),
        shape = RoundedCornerShape(12.dp)
    ) {
        flagsList.forEach { flag ->
            DropdownMenuItem(
                text = {
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically){
                        Image(painter = painterResource(flag.flagImage),
                            contentDescription = "Country Flag")
                        Text(text = stringResource(flag.flagCountryName),
                            fontSize = 14.sp,
                            fontFamily = mulishFont(),
                            color = colorResource(R.color.black),
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(4.dp))
                    }
                },
                onClick = {
                    expanded = !expanded
                    selectedCountry = flag.flagImage
                    phoneCodePlaceholder = flag.phoneCode
                    onItemClick(phoneCodePlaceholder)
                }
            )
        }
    }
}