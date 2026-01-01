package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.models.MessagesData
import com.gorman.core.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(conversationId: String): Flow<MessagesData?> {
        return firebaseRepository.getLastMessage(conversationId)
    }
}
