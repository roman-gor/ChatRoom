package com.gorman.chatroom.domain.usecases

import com.gorman.chatroom.domain.entities.GroupsData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserGroupsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
){
    operator fun invoke(userId: String): Flow<List<GroupsData?>> {
        return firebaseRepository.getUserGroups(userId)
    }
}