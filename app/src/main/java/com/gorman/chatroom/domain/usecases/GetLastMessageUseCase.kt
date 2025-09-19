package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.MessagesData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(conversationId: String): Flow<MessagesData?> {
        return firebaseRepository.getLastMessage(conversationId)
    }
}