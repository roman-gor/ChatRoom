package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class MarkMessagesAsReadUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String, currentUserId: String){
        firebaseRepository.markMessageAsRead(chatId, currentUserId)
    }
}
