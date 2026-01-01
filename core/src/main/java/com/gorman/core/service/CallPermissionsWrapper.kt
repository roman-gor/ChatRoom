package com.gorman.core.service

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallPermissionsWrapper(
    onPermissionsGranted: @Composable () -> Unit,
    onPermissionsDenied: @Composable () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )
    if (permissionState.allPermissionsGranted) {
        onPermissionsGranted()
    }
    else {
        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }

        if (permissionState.shouldShowRationale) {
            onPermissionsDenied()
        }
    }
}
