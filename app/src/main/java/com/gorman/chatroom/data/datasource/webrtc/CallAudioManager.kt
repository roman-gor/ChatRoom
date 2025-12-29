package com.gorman.chatroom.data.datasource.webrtc

import android.content.Context
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CallAudioManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val audioSwitch by lazy {
        AudioSwitch(
            context = context.applicationContext,
            preferredDeviceList = listOf(
                AudioDevice.BluetoothHeadset::class.java,
                AudioDevice.WiredHeadset::class.java,
                AudioDevice.Speakerphone::class.java,
                AudioDevice.Earpiece::class.java
            )
        )
    }

    fun start() {
        audioSwitch.start { _, _ -> }
        audioSwitch.activate()
    }
    fun stop() {
        audioSwitch.deactivate()
        audioSwitch.stop()
    }

    fun setSpeakerphoneOn(enable: Boolean) {
        val devices = audioSwitch.availableAudioDevices
        val speaker = devices.find { it is AudioDevice.Speakerphone }
        val earpiece = devices.find { it is AudioDevice.Earpiece }

        if (enable) {
            speaker?.let { audioSwitch.selectDevice(it) }
        } else {
            earpiece?.let { audioSwitch.selectDevice(it) }
        }
    }
}
