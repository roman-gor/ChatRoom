package com.gorman.chatroom.domain.repository

import android.content.Intent
import com.gorman.chatroom.domain.models.CallModel
import org.webrtc.SurfaceViewRenderer

interface CallRepository {

    fun initFirebase()

    suspend fun sendConnectionRequest(target: String, isVideoCall: Boolean): Boolean

    fun setTarget(target: String)

    fun initWebrtcClient(username: String)

    fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean)

    fun initRemoteSurfaceView(view: SurfaceViewRenderer)

    fun startCall()

    fun endCall()

    fun sendEndCall()

    fun toggleAudio(shouldBeMuted: Boolean)

    fun toggleVideo(shouldBeMuted: Boolean)

    fun switchCamera()

    fun setScreenCaptureIntent(screenPermissionIntent: Intent)

    fun toggleScreenShare(isStarting: Boolean)

    var listener: Listener?

    interface Listener {
        fun onLatestEventReceived(data: CallModel)
        fun endCall()
    }
}
