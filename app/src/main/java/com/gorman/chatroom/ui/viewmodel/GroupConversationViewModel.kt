package com.gorman.chatroom.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.domain.usecases.CreateGroupUseCase
import com.gorman.chatroom.domain.usecases.FindUserByGroupIdUseCase
import com.gorman.chatroom.domain.usecases.GetMessagesUseCase
import com.gorman.chatroom.domain.usecases.MarkMessagesAsReadUseCase
import com.gorman.chatroom.domain.usecases.SendGroupMessagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupConversationViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val findUserByGroupIdUseCase: FindUserByGroupIdUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase,
    private val sendGroupMessagesUseCase: SendGroupMessagesUseCase
): ViewModel() {

    private val _messages = MutableStateFlow<List<MessagesData>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _getterUsersData = mutableStateOf<List<UsersData?>>(emptyList())
    val getterUsersData: State<List<UsersData?>> = _getterUsersData

    private val _groupId = mutableStateOf<String?>("")
    val groupId: State<String?> = _groupId

    fun setupNewConversation(currentUserId: String, getterUsers: List<String?>, groupName: String) {
        viewModelScope.launch {
            val groupId = createGroupUseCase(currentUserId, getterUsers, groupName)
            if (groupId != null) {
                initializeGroup(groupId, currentUserId)
                _groupId.value = groupId
            }
        }
    }

    fun initializeGroup(groupId: String, currentUserId: String) {
        viewModelScope.launch {
            launch {
                getMessagesUseCase(groupId).collect { messagesList ->
                    _messages.value = messagesList
                }
            }
            val getterUsers = findUserByGroupIdUseCase(groupId, currentUserId)
            _getterUsersData.value = getterUsers
            try {
                markMessagesAsReadUseCase(groupId, currentUserId)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отметке сообщения как прочитанного: ${e.message}")
            }
        }
    }

    fun sendMessage(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String) {
        viewModelScope.launch {
            try {
                sendGroupMessagesUseCase(groupId, currentUserId, getterUsers, text)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отправке сообщения ${e.message}")
            }
        }
    }

}