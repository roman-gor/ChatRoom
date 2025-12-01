package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.service.CallServiceRepository
import javax.inject.Inject

class ToggleMediaUseCase @Inject constructor(
    private val serviceRepository: CallServiceRepository // <-- Заменяем зависимость
) {
    operator fun invoke(type: String, muted: Boolean) {
        when (type) {
            "audio" -> serviceRepository.toggleAudio(muted) // <-- Вызываем метод serviceRepository
            "video" -> serviceRepository.toggleVideo(muted) // <-- Вызываем метод serviceRepository
        }
    }
}