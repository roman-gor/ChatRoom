package com.gorman.feature_calls.domain.repository

import android.content.Intent
import com.gorman.core.domain.models.CallModel
import org.webrtc.SurfaceViewRenderer

interface CallRepository {

    fun initWebRTCAndFirebase(username: String)

    suspend fun sendConnectionRequest(target: String, isVideoCall: Boolean): Boolean

    fun setTarget(target: String)

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

    fun clearViews()

    var listener: Listener?

    interface Listener {
        fun onLatestEventReceived(data: CallModel)
        fun endCall()
    }
}
