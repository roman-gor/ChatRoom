package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(conversationId: String): Flow<List<MessagesData>> {
        return firebaseRepository.getMessages(conversationId)
    }
}