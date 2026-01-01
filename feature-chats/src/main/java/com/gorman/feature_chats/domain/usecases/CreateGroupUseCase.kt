package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(currentUserId: String, getterUsers: List<String?>, groupName: String): String? {
        return firebaseRepository.createGroup(currentUserId, getterUsers, groupName)
    }
}
