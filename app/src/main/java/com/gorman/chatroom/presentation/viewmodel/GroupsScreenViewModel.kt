package com.gorman.chatroom.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.entities.GroupPreviewData
import com.gorman.chatroom.domain.entities.GroupsData
import com.gorman.chatroom.domain.usecases.FindUserByGroupIdUseCase
import com.gorman.chatroom.domain.usecases.GetMessagesUseCase
import com.gorman.chatroom.domain.usecases.GetUnreadMessagesQuantityUseCase
import com.gorman.chatroom.domain.usecases.GetUserGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _groupsState = MutableStateFlow<List<GroupsData?>>(emptyList())
    val groupsState: StateFlow<List<GroupsData?>> = _groupsState

    private val _groupPreview = MutableStateFlow<Map<String, GroupPreviewData>>(emptyMap())
    val groupPreview: StateFlow<Map<String, GroupPreviewData>> = _groupPreview

    fun initGroupPreview(userId: String,
                         groupId: String) {
        viewModelScope.launch {
            Log.d("GroupsViewModel", "Start init")
            val getterUsersData = findUserByGroupIdUseCase(groupId, userId)
            val flow = combine(
                getMessagesUseCase(groupId),
                getUnreadMessagesQuantityUseCase(groupId, userId)
            ) { messagesList, quantity->
                val lastMessage = messagesList.maxByOrNull {
                    runCatching { Instant.parse(it.timestamp).toEpochMilli() }.getOrDefault(0L)
                }
                Log.d("Last message", "$lastMessage")
                if (lastMessage?.timestamp != "") {
                    GroupPreviewData(
                        users = getterUsersData,
                        lastMessage = lastMessage,
                        unreadQuantity = quantity
                    )
                }
                else {
                    GroupPreviewData(
                        users = getterUsersData,
                        lastMessage = null,
                        unreadQuantity = quantity
                    )
                }
            }
            flow.collect { data->
                Log.d("Last message", "$data")
                _groupPreview.value = _groupPreview.value.toMutableMap().apply {
                    this[groupId] = data
                }
            }
        }
    }

    fun getUserGroups(userId: String) {
        viewModelScope.launch {
            getUserGroupsUseCase(userId).collect {
                _groupsState.value = it
            }
        }
    }
}