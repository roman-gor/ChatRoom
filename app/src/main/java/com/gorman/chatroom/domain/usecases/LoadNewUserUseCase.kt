package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import javax.inject.Inject

class LoadNewUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(user: UsersData?): Boolean {
        return firebaseRepository.loadNewUser(user)
    }
}