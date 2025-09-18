package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class SetupNewConversationUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(currentUserId: String, getterUserId: String): String? {
        return firebaseRepository.setupNewConversation(currentUserId, getterUserId)
    }
}