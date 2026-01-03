package com.gorman.feature_calls.data.datasource.webrtc

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.google.gson.Gson
import com.gorman.core.domain.models.CallModel
import com.gorman.core.domain.models.CallModelType
import dagger.hilt.android.qualifiers.ApplicationContext
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRTCClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val gson: Gson
) {
    var listener: Listener? = null
    private lateinit var username: String

    private val eglBaseContext = EglBase.create().eglBaseContext
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private var peerConnection: PeerConnection? = null

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80")
            .setUsername("ddb58e07673bbb5a8470d687")
            .setPassword("HOcUZ5IKxcyyGDU8")
            .createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:80?transport=tcp")
            .setUsername("ddb58e07673bbb5a8470d687")
            .setPassword("HOcUZ5IKxcyyGDU8")
            .createIceServer(),
        PeerConnection.IceServer.builder("turn:global.relay.metered.ca:443")
            .setUsername("ddb58e07673bbb5a8470d687")
            .setPassword("HOcUZ5IKxcyyGDU8")
            .createIceServer()
    )

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }
    
    private var videoCapturer: CameraVideoCapturer? = null 
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
    }

    private var localSurfaceView: SurfaceViewRenderer? = null
    private var remoteSurfaceView: SurfaceViewRenderer? = null
    private var localTrackId = ""
    private var localStreamId = ""
    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null

    private var permissionIntent: Intent? = null
    private var screenCapturer: VideoCapturer? = null
    private val localScreenVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private var localScreenShareVideoTrack: VideoTrack? = null

    init {
        initPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBaseContext, true, true))
            .setOptions(PeerConnectionFactory.Options().apply {
                disableNetworkMonitor = false
                disableEncryption = false
            }).createPeerConnectionFactory()
    }

    fun initializeWebrtcClient(username: String, observer: PeerConnection.Observer) {
        this.username = username
        localTrackId = "${username}_track"
        localStreamId = "${username}_stream"
        peerConnection = createPeerConnection(observer)
    }

    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        return peerConnectionFactory.createPeerConnection(rtcConfig, observer)
    }

    fun call(target: String) {
        peerConnection?.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket(
                            CallModel(
                                type = CallModelType.Offer,
                                sender = username,
                                target = target,
                                data = desc?.description
                            )
                        )
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun answer(target: String) {
        peerConnection?.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket(
                            CallModel(
                                type = CallModelType.Answer,
                                sender = username,
                                target = target,
                                data = desc?.description
                            )
                        )
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    fun addIceCandidateToPeer(iceCandidate: IceCandidate) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun sendIceCandidate(target: String, iceCandidate: IceCandidate) {
        listener?.onTransferEventToSocket(
            CallModel(
                type = CallModelType.IceCandidates,
                sender = username,
                target = target,
                data = gson.toJson(iceCandidate)
            )
        )
    }

    fun closeConnection() {
        try {
            localVideoTrack?.setEnabled(false)
            
            videoCapturer?.stopCapture()
            videoCapturer?.dispose()
            videoCapturer = null

            screenCapturer?.stopCapture()
            screenCapturer?.dispose()
            screenCapturer = null

            surfaceTextureHelper?.dispose()
            surfaceTextureHelper = null

            peerConnection?.close()

            localVideoTrack = null
            localAudioTrack = null
            peerConnection = null
        } catch (e: Exception) {
            Log.e("WebRTCClient", "Error closing connection: ${e.message}")
        }
    }

    fun switchCamera() {
        videoCapturer?.switchCamera(null)
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        localAudioTrack?.setEnabled(!shouldBeMuted)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        localVideoTrack?.setEnabled(!shouldBeMuted)
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        this.remoteSurfaceView = view
        try {
            view.init(eglBaseContext, null)
        } catch (_: IllegalStateException) {
            Log.w("WebRTCClient", "Remote view already initialized")
        }
        view.setEnableHardwareScaler(true)
    }

    fun initLocalSurfaceView(localView: SurfaceViewRenderer, isVideoCall: Boolean) {
        this.localSurfaceView = localView
        try {
            localView.init(eglBaseContext, null)
        } catch (_: IllegalStateException) {
            Log.w("WebRTCClient", "Local view already initialized")
        }
        localView.setMirror(true)
        localView.setEnableHardwareScaler(true)
        startLocalStreaming(localView, isVideoCall)
    }

    private fun startLocalStreaming(localView: SurfaceViewRenderer, isVideoCall: Boolean) {
        localAudioTrack = peerConnectionFactory.createAudioTrack(localTrackId + "_audio", localAudioSource)
        peerConnection?.addTrack(localAudioTrack, listOf(localStreamId))

        if (isVideoCall) {
            startCapturingCamera(localView)
        }
    }

    private fun startCapturingCamera(localView: SurfaceViewRenderer) {
        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = SurfaceTextureHelper.create(
            "CaptureThread", eglBaseContext
        )

        videoCapturer = getVideoCapturer(context)
        videoCapturer?.initialize(
            surfaceTextureHelper, context, localVideoSource.capturerObserver
        )

        videoCapturer?.startCapture(720, 480, 20)

        localVideoTrack = peerConnectionFactory.createVideoTrack(localTrackId + "_video", localVideoSource)
        localVideoTrack?.addSink(localView)

        peerConnection?.addTrack(localVideoTrack, listOf(localStreamId))
    }

    private fun getVideoCapturer(context: Context): CameraVideoCapturer =
        Camera2Enumerator(context).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException("No front facing camera found")
        }

    fun setPermissionIntent(screenPermissionIntent: Intent) {
        this.permissionIntent = screenPermissionIntent
    }

    @Suppress("DEPRECATION")
    fun startScreenCapturing() {
        val displayMetrics = DisplayMetrics()
        val windowsManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidthPixels = displayMetrics.widthPixels
        val screenHeightPixels = displayMetrics.heightPixels

        surfaceTextureHelper?.dispose()
        surfaceTextureHelper = SurfaceTextureHelper.create(
            "ScreenCaptureThread", eglBaseContext
        )

        screenCapturer = createScreenCapturer()
        screenCapturer!!.initialize(
            surfaceTextureHelper, context, localScreenVideoSource.capturerObserver
        )
        screenCapturer!!.startCapture(screenWidthPixels, screenHeightPixels, 15)

        localScreenShareVideoTrack =
            peerConnectionFactory.createVideoTrack(localTrackId + "_screen", localScreenVideoSource)

        localScreenShareVideoTrack?.addSink(localSurfaceView)
        peerConnection?.addTrack(localScreenShareVideoTrack, listOf(localStreamId))
    }

    fun stopScreenCapturing() {
        screenCapturer?.stopCapture()
        screenCapturer?.dispose()
        screenCapturer = null
        
        localScreenShareVideoTrack?.removeSink(localSurfaceView)
        localSurfaceView?.clearImage()
        localScreenShareVideoTrack?.dispose()
        localScreenShareVideoTrack = null
    }

    private fun createScreenCapturer(): VideoCapturer {
        return ScreenCapturerAndroid(permissionIntent, object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.d("WebRTCClient", "Screen casting stopped")
            }
        })
    }

    interface Listener {
        fun onTransferEventToSocket(data: CallModel)
    }
}
