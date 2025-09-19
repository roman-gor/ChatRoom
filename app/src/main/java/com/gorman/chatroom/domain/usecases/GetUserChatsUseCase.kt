package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.ChatsData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserChatsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(userId: String): Flow<List<ChatsData?>> {
        return firebaseRepository.getUserChats(userId)
    }
}