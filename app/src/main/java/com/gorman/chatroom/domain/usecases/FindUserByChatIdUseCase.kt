package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class FindUserByChatIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String, currentUserId: String): UsersData? {
        return firebaseRepository.findUserByChatId(chatId, currentUserId)
    }
}