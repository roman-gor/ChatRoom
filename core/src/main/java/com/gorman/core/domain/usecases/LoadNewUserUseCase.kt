package com.gorman.core.domain.usecases

import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import javax.inject.Inject

class LoadNewUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(user: UsersData?): Boolean {
        return firebaseRepository.loadNewUser(user)
    }
}
