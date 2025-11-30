package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.CallRepository
import javax.inject.Inject

class AcceptCallUseCase @Inject constructor(
    private val callRepository: CallRepository
) {
    operator fun invoke(targetId: String) {
        callRepository.setTarget(targetId)
        callRepository.startCall()
    }
}