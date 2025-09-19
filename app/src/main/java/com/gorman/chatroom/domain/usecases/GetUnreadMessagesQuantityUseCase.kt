package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadMessagesQuantityUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(conversationId: String, userId: String): Flow<Int> {
        return firebaseRepository.getUnreadMessagesQuantity(conversationId, userId)
    }
}