package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class UpdateUserDataUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(userId: String, user: UsersData?) {
        firebaseRepository.updateUserData(userId, user)
    }
}