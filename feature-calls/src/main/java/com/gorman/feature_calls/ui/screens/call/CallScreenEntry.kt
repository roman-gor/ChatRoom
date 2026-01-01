package com.gorman.feature_calls.ui.screens.call

import android.Manifest
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.core.R
import com.gorman.feature_calls.service.CallService
import com.gorman.feature_calls.ui.viewmodel.CallViewModel
import org.webrtc.SurfaceViewRenderer
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun CallScreenEntry(
    viewModel: CallViewModel = hiltViewModel(),
    targetId: String,
    isCaller: Boolean,
    isVideoCall: Boolean,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    val localRenderer = remember { SurfaceViewRenderer(context) }
    val remoteRenderer = remember { SurfaceViewRenderer(context) }
    val isMicrophoneMuted by viewModel.isMicrophoneMuted.collectAsStateWithLifecycle()
    val isCameraMuted by viewModel.isCameraMuted.collectAsStateWithLifecycle()
    val isSpeakerPhoneOn by viewModel.isSpeakerPhoneOn.collectAsStateWithLifecycle()
    viewModel.localSurfaceView.value = localRenderer
    viewModel.remoteSurfaceView.value = remoteRenderer
    LaunchedEffect(Unit) {
        CallService.endCallListener = object: CallService.EndCallListener {
            override fun onCallEnded() {
                onEndCall()
            }
        }
    }
    DisposableEffect(localRenderer, remoteRenderer) {
        onDispose {
            localRenderer.release()
            remoteRenderer.release()
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
    CallScreen(
        isMicrophoneMuted = isMicrophoneMuted,
        isCameraMuted = isCameraMuted,
        isSpeakerPhoneOn = isSpeakerPhoneOn,
        onViewModelEndCall = { viewModel.onEndCallClicked() },
        onToggleMicClicked = { viewModel.onToggleMicClicked() },
        onToggleCamClicked = { viewModel.onToggleCameraClicked() },
        onSwitchCameraClicked = { viewModel.onSwitchCameraClicked() },
        onToggleAudioDeviceClick = { viewModel.onToggleAudioDeviceClicked() },
        onToggleScreenSharedClick = { viewModel.onToggleScreenShareClicked() },
        remoteRenderer = remoteRenderer,
        localRenderer = localRenderer
    )
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun CallScreen(
    isMicrophoneMuted: Boolean,
    isCameraMuted: Boolean,
    isSpeakerPhoneOn: Boolean,
    onViewModelEndCall: () -> Unit,
    onToggleMicClicked: () -> Unit,
    onToggleCamClicked: () -> Unit,
    onSwitchCameraClicked: () -> Unit,
    onToggleAudioDeviceClick: () -> Unit,
    onToggleScreenSharedClick: () -> Unit,
    remoteRenderer: SurfaceViewRenderer,
    localRenderer: SurfaceViewRenderer
) {

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
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
                text = stringResource(R.string.call),
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

            IconButton(onClick = { onViewModelEndCall() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_end_call),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { onToggleMicClicked() }) {
                Icon(
                    painter = painterResource(
                        if (isMicrophoneMuted) R.drawable.ic_mic_off
                            else R.drawable.ic_mic_on),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { onToggleCamClicked() }) {
                Icon(
                    painter = painterResource(
                        if (isCameraMuted) R.drawable.ic_camera_off
                            else R.drawable.ic_camera_on),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { onSwitchCameraClicked() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_switch_camera),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { onToggleAudioDeviceClick() }) {
                Icon(
                    painter = painterResource(
                        if (isSpeakerPhoneOn) R.drawable.ic_speaker
                            else R.drawable.ic_ear),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = { onToggleScreenSharedClick() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_screen_share),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}
