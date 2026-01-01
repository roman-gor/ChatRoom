package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class FindUserByGroupIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(groupId: String, currentUserId: String): List<UsersData?> {
        return firebaseRepository.findUserByGroupId(groupId, currentUserId)
    }
}
