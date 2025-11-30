package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.CallRepository
import javax.inject.Inject

class EndCallUseCase @Inject constructor(
    private val callRepository: CallRepository
) {
    operator fun invoke() {
        callRepository.sendEndCall()
        callRepository.endCall()
    }
}