package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class FindUserByGroupIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(groupId: String, currentUserId: String): List<UsersData?> {
        return firebaseRepository.findUserByGroupId(groupId, currentUserId)
    }
}