package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.CallRepository
import javax.inject.Inject

class StartCallUseCase @Inject constructor(
    private val callRepository: CallRepository
) {
    suspend operator fun invoke(targetId: String, isVideoCall: Boolean): Boolean =
        callRepository.sendConnectionRequest(targetId, isVideoCall)
}