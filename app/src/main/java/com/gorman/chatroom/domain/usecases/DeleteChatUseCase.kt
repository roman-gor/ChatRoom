package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String) {
        firebaseRepository.deleteChat(chatId = chatId)
    }
}