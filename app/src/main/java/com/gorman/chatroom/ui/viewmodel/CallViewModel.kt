package com.gorman.chatroom.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gorman.chatroom.data.datasource.webrtc.RTCAudioManager
import com.gorman.chatroom.service.CallService
import com.gorman.chatroom.service.CallServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val serviceRepository: CallServiceRepository
): ViewModel() {
    val isMicrophoneMuted = mutableStateOf(false)
    val isCameraMuted = mutableStateOf(false)
    val isScreenSharing = mutableStateOf(false)
    val currentAudioDevice = mutableStateOf(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
    val remoteSurfaceView = mutableStateOf<SurfaceViewRenderer?>(null)
    val localSurfaceView = mutableStateOf<SurfaceViewRenderer?>(null)

    fun init(targetId: String, isCaller: Boolean, isVideoCall: Boolean) {
        CallService.remoteSurfaceView = remoteSurfaceView.value
        CallService.localSurfaceView = localSurfaceView.value

        serviceRepository.setupViews(
            videoCall = isVideoCall,
            caller = isCaller,
            target = targetId
        )
    }

    fun onEndCallClicked() {
        serviceRepository.sendEndCall()
    }

    fun onToggleMicClicked() {
        isMicrophoneMuted.value = !isMicrophoneMuted.value
        serviceRepository.toggleAudio(isMicrophoneMuted.value)
    }

    fun onToggleCameraClicked() {
        isCameraMuted.value = !isCameraMuted.value
        serviceRepository.toggleVideo(isCameraMuted.value)
    }

    fun onSwitchCameraClicked() {
        serviceRepository.switchCamera()
    }

    fun onToggleAudioDeviceClicked() {
        if (currentAudioDevice.value == RTCAudioManager.AudioDevice.SPEAKER_PHONE) {
            currentAudioDevice.value = RTCAudioManager.AudioDevice.EARPIECE
        } else {
            currentAudioDevice.value = RTCAudioManager.AudioDevice.SPEAKER_PHONE
        }
        serviceRepository.toggleAudioDevice(currentAudioDevice.value.name)
    }

    fun onToggleScreenShareClicked() {
        isScreenSharing.value = !isScreenSharing.value
        serviceRepository.toggleScreenShare(isScreenSharing.value)
    }
}