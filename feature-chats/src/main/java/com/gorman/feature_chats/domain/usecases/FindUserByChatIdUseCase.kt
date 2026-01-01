package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class FindUserByChatIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(chatId: String, currentUserId: String): UsersData? {
        return firebaseRepository.findUserByChatId(chatId, currentUserId)
    }
}
