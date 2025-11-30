package com.gorman.chatroom.ui.ui.screens.call

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.gorman.chatroom.R
import org.webrtc.SurfaceViewRenderer

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun CallScreen(
    remoteRenderer: SurfaceViewRenderer,
    localRenderer: SurfaceViewRenderer,
    callTime: String,
    callTitle: String,
    onEndCall: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleCamera: () -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleAudioDevice: () -> Unit,
    onScreenShare: () -> Unit
) {
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
                text = callTime,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = callTitle,
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

            IconButton(onClick = onEndCall) {
                Icon(
                    painter = painterResource(R.drawable.ic_end_call),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onToggleMic) {
                Icon(
                    painter = painterResource(R.drawable.ic_mic_off),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onToggleCamera) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera_off),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onSwitchCamera) {
                Icon(
                    painter = painterResource(R.drawable.ic_switch_camera),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onToggleAudioDevice) {
                Icon(
                    painter = painterResource(R.drawable.ic_ear),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onScreenShare) {
                Icon(
                    painter = painterResource(R.drawable.ic_screen_share),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}
