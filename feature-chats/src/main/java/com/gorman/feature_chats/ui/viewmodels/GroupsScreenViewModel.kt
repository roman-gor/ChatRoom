package com.gorman.feature_chats.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.core.domain.models.GroupPreviewData
import com.gorman.core.domain.repository.SettingsRepository
import com.gorman.feature_chats.domain.usecases.FindUserByGroupIdUseCase
import com.gorman.feature_chats.domain.usecases.GetMessagesUseCase
import com.gorman.feature_chats.domain.usecases.GetUnreadMessagesQuantityUseCase
import com.gorman.feature_chats.domain.usecases.GetUserGroupsUseCase
import com.gorman.feature_chats.ui.states.GroupsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class GroupsScreenViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val findUserByGroupIdUseCase: FindUserByGroupIdUseCase,
    private val getUnreadMessagesQuantityUseCase: GetUnreadMessagesQuantityUseCase,
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _groupUiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Idle)
    val groupUiState = _groupUiState.asStateFlow()

    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.userIdFlow.collect {
                _userId.value = it
            }
        }
    }

    fun loadAllGroups(userId: String) {
        viewModelScope.launch {
            _groupUiState.value = GroupsUiState.Loading
            try {
                getUserGroupsUseCase(userId).collect { groupsDataList ->
                    if (groupsDataList.isEmpty()) {
                        _groupUiState.value = GroupsUiState.Success(emptyList<GroupPreviewData>())
                        return@collect
                    }
                    val previewsFlow = groupsDataList.filterNotNull().map { group ->
                        val groupId = group.groupId ?: ""
                        val getterUsersData = findUserByGroupIdUseCase(groupId, userId)
                        combine(
                            getMessagesUseCase(groupId),
                            getUnreadMessagesQuantityUseCase(groupId, userId)
                        ) { messagesList, quantity ->
                            val lastMessage = messagesList.maxByOrNull {
                                runCatching {
                                    Instant.parse(it.timestamp).toEpochMilli()
                                }.getOrDefault(0L)
                            }
                            group.groupId to GroupPreviewData(
                                groupId = group.groupId,
                                groupName = group.groupName,
                                users = getterUsersData,
                                lastMessage = lastMessage,
                                unreadQuantity = quantity
                            )
                        }
                    }
                    combine(previewsFlow) { previews ->
                        previews.map { it.second }
                    }.collect { fullPreviewList ->
                        _groupUiState.value = GroupsUiState.Success(fullPreviewList)
                    }
                }
            } catch (e: Exception) {
                _groupUiState.value = GroupsUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
