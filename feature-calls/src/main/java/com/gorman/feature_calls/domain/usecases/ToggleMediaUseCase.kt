package com.gorman.feature_calls.domain.usecases

import com.gorman.feature_calls.service.CallServiceConstants
import com.gorman.feature_calls.service.CallServiceRepository
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
