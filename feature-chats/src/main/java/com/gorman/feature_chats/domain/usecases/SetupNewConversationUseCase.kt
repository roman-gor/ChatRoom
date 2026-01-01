package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetupNewConversationUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(currentUserId: String, getterUserId: String): String? {
        return firebaseRepository.setupNewConversation(currentUserId, getterUserId)
    }
}
