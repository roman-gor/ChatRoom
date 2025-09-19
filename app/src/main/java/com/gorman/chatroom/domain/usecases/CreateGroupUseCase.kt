package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(currentUserId: String, getterUsers: List<String?>, groupName: String): String? {
        return firebaseRepository.createGroup(currentUserId, getterUsers, groupName)
    }
}