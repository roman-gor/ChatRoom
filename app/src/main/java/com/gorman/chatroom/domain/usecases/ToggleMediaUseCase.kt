package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.models.CallModelType
import com.gorman.chatroom.domain.repository.CallRepository
import javax.inject.Inject

class ToggleMediaUseCase @Inject constructor(
    private val callRepository: CallRepository
) {
    operator fun invoke(type: String, muted: Boolean) {
        when (type) {
            "audio" -> callRepository.toggleAudio(muted)
            "video" -> callRepository.toggleVideo(muted)
        }
    }
}