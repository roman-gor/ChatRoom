package com.gorman.chatroom.data.repository

import android.content.Intent
import com.google.gson.Gson
import com.gorman.chatroom.data.datasource.remote.FirebaseCallClient
import com.gorman.chatroom.domain.models.CallModel
import com.gorman.chatroom.domain.models.CallModelType
import com.gorman.chatroom.domain.repository.CallRepository
import com.gorman.chatroom.webrtc.MyPeerObserver
import com.gorman.chatroom.webrtc.WebRTCClient
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

    override fun initFirebase() {
        firebaseClient.subscribeForLatestEvent(object : FirebaseCallClient.Listener {
            override fun onLatestEventReceived(event: CallModel) {
                listener?.onLatestEventReceived(event)
                when (event.type) {
                    CallModelType.Offer ->{
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.OFFER,
                                event.data.toString()
                            )
                        )
                        webRTCClient.answer(target!!)
                    }
                    CallModelType.Answer ->{
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.ANSWER,
                                event.data.toString()
                            )
                        )
                    }
                    CallModelType.IceCandidates ->{
                        val candidate: IceCandidate? = try {
                            gson.fromJson(event.data.toString(),IceCandidate::class.java)
                        }catch (_ :Exception){
                            null
                        }
                        candidate?.let {
                            webRTCClient.addIceCandidateToPeer(it)
                        }
                    }
                    CallModelType.EndCall ->{
                        listener?.endCall()
                    }
                    else -> Unit
                }
            }

        })
    }

    override suspend fun sendConnectionRequest(target: String, isVideoCall: Boolean): Boolean {
        val message = CallModel(
            type = if (isVideoCall) CallModelType.StartVideoCall else CallModelType.StartAudioCall,
            target = target
        )
        return firebaseClient.sendMessageToOtherClient(message)
    }

    override fun setTarget(target: String) {
        this.target = target
    }

    override fun initWebrtcClient(username: String) {
        webRTCClient.listener = this
        webRTCClient.initializeWebrtcClient(username, object : MyPeerObserver() {
            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                try {
                    p0?.videoTracks?.get(0)?.addSink(remoteView)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRTCClient.sendIceCandidate(target!!, it)
                }
            }
        })
    }

    override fun initLocalSurfaceView(view: SurfaceViewRenderer, isVideoCall: Boolean) {
        webRTCClient.initLocalSurfaceView(view, isVideoCall)
    }

    override fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        webRTCClient.initRemoteSurfaceView(view)
        this.remoteView = view
    }

    override fun startCall() {
        webRTCClient.call(target!!)
    }

    override fun endCall() {
        webRTCClient.closeConnection()
    }

    override fun sendEndCall() {
        onTransferEventToSocket(
            CallModel(
                type = CallModelType.EndCall,
                target = target!!
            )
        )
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
            firebaseClient.sendMessageToOtherClient(data)
        }
    }

    override fun setScreenCaptureIntent(screenPermissionIntent: Intent) {
        webRTCClient.setPermissionIntent(screenPermissionIntent)
    }

    override fun toggleScreenShare(isStarting: Boolean) {
        if (isStarting){
            webRTCClient.startScreenCapturing()
        }else{
            webRTCClient.stopScreenCapturing()
        }
    }
}
