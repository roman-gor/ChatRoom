package com.gorman.feature_calls.data.repository

import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.gorman.core.data.datasource.remote.FirebaseCallClient
import com.gorman.core.domain.models.CallModel
import com.gorman.core.domain.models.CallModelType
import com.gorman.feature_calls.data.datasource.webrtc.MyPeerObserver
import com.gorman.feature_calls.data.datasource.webrtc.WebRTCClient
import com.gorman.feature_calls.domain.repository.CallRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepositoryImpl @Inject constructor(
    private val firebaseClient: FirebaseCallClient,
    private val webRTCClient: WebRTCClient,
    private val gson: Gson
) : CallRepository, WebRTCClient.Listener {

    private var target: String? = null
    override var listener: CallRepository.Listener? = null
    private var remoteView: SurfaceViewRenderer? = null
    private var localView: SurfaceViewRenderer? = null
    private var currentUsername: String? = null

    override fun initWebRTCAndFirebase(username: String) {
        this.currentUsername = username
        firebaseClient.setClientId(username)
        firebaseClient.subscribeForLatestEvent(object : FirebaseCallClient.Listener {
            override fun onLatestEventReceived(event: CallModel) {
                if (event.sender == currentUsername) return
                
                listener?.onLatestEventReceived(event)
                when (event.type) {
                    CallModelType.Offer -> {
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(SessionDescription.Type.OFFER, event.data.toString())
                        )
                        target?.let { webRTCClient.answer(it) }
                    }
                    CallModelType.Answer -> {
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(SessionDescription.Type.ANSWER, event.data.toString())
                        )
                    }
                    CallModelType.IceCandidates -> {
                        val candidate: IceCandidate? = try {
                            gson.fromJson(event.data.toString(), IceCandidate::class.java)
                        } catch (_: Exception) { null }
                        candidate?.let { webRTCClient.addIceCandidateToPeer(it) }
                    }
                    CallModelType.EndCall -> listener?.endCall()
                    else -> Unit
                }
            }
        })
        webRTCClient.listener = this
        webRTCClient.initializeWebrtcClient(username, object : MyPeerObserver() {
            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                try {
                    p0?.videoTracks?.get(0)?.addSink(remoteView)
                } catch (e: Exception) {
                    Log.e("CallRepository", "${e.message}")
                }
            }
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let { candidate ->
                    target?.let { targetUser ->
                        webRTCClient.sendIceCandidate(targetUser, candidate)
                    }
                }
            }
        })
    }

    override fun startCall() {
        target?.let { webRTCClient.call(it) }
    }

    override fun sendEndCall() {
        target?.let {
            onTransferEventToSocket(
                CallModel(
                    type = CallModelType.EndCall,
                    target = it
                )
            )
        }
    }

    override suspend fun sendConnectionRequest(target: String, isVideoCall: Boolean): Boolean {
        this.target = target
        val message = CallModel(
            type = if (isVideoCall) CallModelType.StartVideoCall else CallModelType.StartAudioCall,
            target = target,
            sender = currentUsername ?: ""
        )
        return firebaseClient.sendMessageToOtherClient(message)
    }

    override fun setTarget(target: String) {
        this.target = target
    }

    override fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        this.localView = view
        webRTCClient.initLocalSurfaceView(view, isVideoCall)
    }

    override fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        this.remoteView = view
        webRTCClient.initRemoteSurfaceView(view)
    }

    override fun endCall() {
        webRTCClient.closeConnection()
    }

    override fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
    }

    override fun toggleVideo(shouldBeMuted: Boolean) {
        webRTCClient.toggleVideo(shouldBeMuted)
    }

    override fun switchCamera() {
        webRTCClient.switchCamera()
    }

    override fun onTransferEventToSocket(data: CallModel) {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseClient.sendMessageToOtherClient(data.copy(sender = currentUsername))
        }
    }

    override fun setScreenCaptureIntent(screenPermissionIntent: Intent) {
        webRTCClient.setPermissionIntent(screenPermissionIntent)
    }

    override fun toggleScreenShare(isStarting: Boolean) {
        if (isStarting) {
            webRTCClient.startScreenCapturing()
        } else {
            webRTCClient.stopScreenCapturing()
        }
    }

    override fun clearViews() {
        this.localView = null
        this.remoteView = null
    }
}
