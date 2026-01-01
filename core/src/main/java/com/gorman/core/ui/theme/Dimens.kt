package com.gorman.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 8.dp,
    val paddingLarge: Dp = 16.dp,
    val paddingExtraLarge: Dp = 24.dp,
    val paddingTopTopBar: Dp = 40.dp,
    val paddingBottomTopBar: Dp = 20.dp,
    val avatarSize: Dp = 50.dp,
    val profileAvatarSize: Dp = 160.dp,
    val iconSizeSmall: Dp = 18.dp,
    val cornerRadius: Dp = 12.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
