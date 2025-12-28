package com.gorman.chatroom.ui.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = White,
    secondary = White,
    onSecondary = LightGray80,
    tertiary = LightGray,
    background = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Blue80,
    onPrimary = White,
    secondary = Black,
    onSecondary = LightGray40,
    tertiary = LightGray,
    background = White
)

object ChatRoomTheme {
    val dimens: ChatRoomDimens
        @Composable
        get() = LocalChatRoomDimens.current
    val types: androidx.compose.material3.Typography
        @Composable
        get() = LocalChatRoomTypes.current
}

@Composable
fun ChatRoomTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val dimens = ChatRoomDimens()
    val types = Typography
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    CompositionLocalProvider(
        LocalChatRoomDimens provides dimens,
        LocalChatRoomTypes provides types
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = types,
            content = content
        )
    }
}