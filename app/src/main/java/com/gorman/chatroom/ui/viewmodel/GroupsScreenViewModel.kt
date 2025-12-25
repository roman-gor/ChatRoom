package com.gorman.chatroom.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.models.GroupPreviewData
import com.gorman.chatroom.domain.models.GroupsData
import com.gorman.chatroom.domain.usecases.FindUserByGroupIdUseCase
import com.gorman.chatroom.domain.usecases.GetMessagesUseCase
import com.gorman.chatroom.domain.usecases.GetUnreadMessagesQuantityUseCase
import com.gorman.chatroom.domain.usecases.GetUserGroupsUseCase
import com.gorman.chatroom.ui.states.GroupsUiState
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
    private val getUserGroupsUseCase: GetUserGroupsUseCase
): ViewModel() {

    private val _groupUiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Idle)
    val groupUiState = _groupUiState.asStateFlow()

    fun loadAllGroups(userId: String) {
        viewModelScope.launch {
            _groupUiState.value = GroupsUiState.Loading
            try {
                getUserGroupsUseCase(userId).collect { groupsDataList ->
                    if (groupsDataList.isEmpty()) {
                        _groupUiState.value = GroupsUiState.Success(emptyList())
                        return@collect
                    }
                    val previewsFlow = groupsDataList.filterNotNull().map { group ->
                        val getterUsersData = findUserByGroupIdUseCase(group.groupId!!, userId)
                        combine(
                            getMessagesUseCase(group.groupId),
                            getUnreadMessagesQuantityUseCase(group.groupId, userId)
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