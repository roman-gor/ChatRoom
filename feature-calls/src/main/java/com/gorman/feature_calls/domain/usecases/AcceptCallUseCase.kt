package com.gorman.feature_calls.domain.usecases

import com.gorman.feature_calls.service.CallServiceRepository
import javax.inject.Inject

class AcceptCallUseCase @Inject constructor(
    private val serviceRepository: CallServiceRepository
) {
    operator fun invoke() {
        serviceRepository.acceptCall()
    }
}
