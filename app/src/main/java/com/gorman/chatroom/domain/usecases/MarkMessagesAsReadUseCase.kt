package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class MarkMessagesAsReadUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String, currentUserId: String){
        firebaseRepository.markMessageAsRead(chatId, currentUserId)
    }
}