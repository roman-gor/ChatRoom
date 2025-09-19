package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrentUserDataUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(): Flow<UsersData?> {
        return firebaseRepository.currentUserData
    }
}