package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.service.CallServiceConstants
import com.gorman.chatroom.service.CallServiceRepository
import javax.inject.Inject

class ToggleMediaUseCase @Inject constructor(
    private val serviceRepository: CallServiceRepository
) {
    operator fun invoke(type: String, muted: Boolean) {
        when (type) {
            CallServiceConstants.AUDIO.value -> serviceRepository.toggleAudio(muted)
            CallServiceConstants.VIDEO.value -> serviceRepository.toggleVideo(muted)
        }
    }
}
