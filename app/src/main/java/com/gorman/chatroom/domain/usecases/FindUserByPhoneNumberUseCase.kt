package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindUserByPhoneNumberUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(phoneNumber: String): Flow<UsersData?> {
        return firebaseRepository.findUserByPhoneNumber(phoneNumber)
    }
}