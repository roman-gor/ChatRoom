package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.CallRepository
import com.gorman.chatroom.service.CallServiceRepository
import javax.inject.Inject

class AcceptCallUseCase @Inject constructor(
    private val serviceRepository: CallServiceRepository
) {
    operator fun invoke() {
        serviceRepository.acceptCall()
    }
}