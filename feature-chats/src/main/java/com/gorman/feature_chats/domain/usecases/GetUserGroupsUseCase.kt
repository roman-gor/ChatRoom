package com.gorman.feature_chats.domain.usecases

import com.gorman.core.domain.models.GroupsData
import com.gorman.core.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserGroupsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
){
    operator fun invoke(userId: String): Flow<List<GroupsData?>> {
        return firebaseRepository.getUserGroups(userId)
    }
}
