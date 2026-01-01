package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String) {
        firebaseRepository.deleteChat(chatId = chatId)
    }
}
