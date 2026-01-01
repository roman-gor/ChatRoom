package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrentUserDataUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(): Flow<UsersData?> {
        return firebaseRepository.currentUserData
    }
}
