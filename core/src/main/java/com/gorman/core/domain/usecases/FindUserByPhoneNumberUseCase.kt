package com.gorman.core.domain.usecases

import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindUserByPhoneNumberUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(phoneNumber: String): Flow<UsersData?> {
        return firebaseRepository.findUserByPhoneNumber(phoneNumber)
    }
}
