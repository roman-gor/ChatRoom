package com.gorman.chatroom.ui.ui.screens.call

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.values
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.chatroom.R
import com.gorman.chatroom.service.CallService
import com.gorman.chatroom.service.CallServiceActions
import com.gorman.chatroom.ui.viewmodel.CallViewModel
import org.webrtc.SurfaceViewRenderer
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun CallScreen(
    targetId: String,
    isCaller: Boolean,
    isVideoCall: Boolean,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CallViewModel = hiltViewModel()
    val localRenderer = remember { SurfaceViewRenderer(context) }
    val remoteRenderer = remember { SurfaceViewRenderer(context) }

    viewModel.localSurfaceView.value = localRenderer
    viewModel.remoteSurfaceView.value = remoteRenderer

    LaunchedEffect(Unit) {
        CallService.endCallListener = object: CallService.EndCallListener {
            override fun onCallEnded() {
                onEndCall()
            }
        }
    }

    val permissionsToRequest = if (isVideoCall) {
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            viewModel.init(targetId, isCaller, isVideoCall)
        } else {
            onEndCall()
        }
    }

    LaunchedEffect(Unit) {
        multiplePermissionsLauncher.launch(permissionsToRequest)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { remoteRenderer }
        )

        AndroidView(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 70.dp)
                .size(width = 100.dp, height = 150.dp),
            factory = { localRenderer }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0x66000000))
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Звонок",
                color = Color.White,
                fontSize = 15.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0x66000000))
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { viewModel.onEndCallClicked() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_end_call),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { viewModel.onToggleMicClicked() }) {
                Icon(
                    painter = painterResource(
                        if (viewModel.isMicrophoneMuted.value) R.drawable.ic_mic_off
                            else R.drawable.ic_mic_on),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { viewModel.onToggleCameraClicked() }) {
                Icon(
                    painter = painterResource(
                        if (viewModel.isCameraMuted.value) R.drawable.ic_camera_off
                            else R.drawable.ic_camera_on),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { viewModel.onSwitchCameraClicked() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_switch_camera),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { viewModel.onToggleAudioDeviceClicked() }) {
                Icon(
                    painter = painterResource(
                        if (viewModel.currentAudioDevice.value.name == "SPEAKER_PHONE") R.drawable.ic_speaker
                            else R.drawable.ic_ear),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { viewModel.onToggleScreenShareClicked() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_screen_share),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}
