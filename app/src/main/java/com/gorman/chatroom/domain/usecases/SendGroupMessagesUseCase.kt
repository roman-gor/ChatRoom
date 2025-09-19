package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class SendGroupMessagesUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String) {
        firebaseRepository.sendGroupMessages(groupId, currentUserId, getterUsers, text)
    }
}