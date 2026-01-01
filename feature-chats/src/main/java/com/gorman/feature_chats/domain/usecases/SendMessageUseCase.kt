package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String, currentUserId: String, getterId: String, text: String) {
        firebaseRepository.sendMessage(chatId, currentUserId, getterId, text)
    }
}
