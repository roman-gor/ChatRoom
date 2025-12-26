package com.gorman.chatroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gorman.chatroom.service.CallService
import com.gorman.chatroom.service.CallServiceConstants
import com.gorman.chatroom.service.CallServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val serviceRepository: CallServiceRepository
): ViewModel() {
    private val _isMicrophoneMuted = MutableStateFlow(false)
    val isMicrophoneMuted = _isMicrophoneMuted.asStateFlow()

    private val _isCameraMuted = MutableStateFlow(false)
    val isCameraMuted = _isCameraMuted.asStateFlow()

    private val _isScreenSharing = MutableStateFlow(false)
    val isScreenSharing = _isScreenSharing.asStateFlow()

    private val _isSpeakerPhoneOn = MutableStateFlow(true)
    val isSpeakerPhoneOn = _isSpeakerPhoneOn.asStateFlow()

    val remoteSurfaceView = MutableStateFlow<SurfaceViewRenderer?>(null)
    val localSurfaceView = MutableStateFlow<SurfaceViewRenderer?>(null)

    fun init(targetId: String, isCaller: Boolean, isVideoCall: Boolean) {
        CallService.remoteSurfaceView = remoteSurfaceView.value
        CallService.localSurfaceView = localSurfaceView.value
        localSurfaceView.let {
            serviceRepository.setupViews(
                videoCall = isVideoCall,
                caller = isCaller,
                target = targetId
            )
        }
    }

    fun onEndCallClicked() {
        serviceRepository.sendEndCall()
    }

    fun onToggleMicClicked() {
        _isMicrophoneMuted.value = !isMicrophoneMuted.value
        serviceRepository.toggleAudio(isMicrophoneMuted.value)
    }

    fun onToggleCameraClicked() {
        _isCameraMuted.value = !isCameraMuted.value
        serviceRepository.toggleVideo(isCameraMuted.value)
    }

    fun onSwitchCameraClicked() {
        serviceRepository.switchCamera()
    }

    fun onToggleAudioDeviceClicked() {
        _isSpeakerPhoneOn.value = !isSpeakerPhoneOn.value
        val deviceType =
            if (isSpeakerPhoneOn.value) CallServiceConstants.SPEAKER_PHONE.value
            else CallServiceConstants.EARPIECE.value
        serviceRepository.toggleAudioDevice(deviceType)
    }

    fun onToggleScreenShareClicked() {
        _isScreenSharing.value = !isScreenSharing.value
        serviceRepository.toggleScreenShare(isScreenSharing.value)
    }
}