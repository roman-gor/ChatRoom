package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadMessagesQuantityUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(conversationId: String, userId: String): Flow<Int> {
        return firebaseRepository.getUnreadMessagesQuantity(conversationId, userId)
    }
}
