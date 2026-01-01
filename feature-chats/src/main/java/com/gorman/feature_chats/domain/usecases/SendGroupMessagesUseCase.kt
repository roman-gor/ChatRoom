package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendGroupMessagesUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String) {
        firebaseRepository.sendGroupMessages(groupId, currentUserId, getterUsers, text)
    }
}
