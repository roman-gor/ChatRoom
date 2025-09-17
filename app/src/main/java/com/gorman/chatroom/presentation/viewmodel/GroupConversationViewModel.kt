package com.gorman.chatroom.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.entities.MessagesData
import com.gorman.chatroom.domain.entities.UsersData
import com.gorman.chatroom.domain.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupConversationViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _messages = MutableStateFlow<List<MessagesData>>(emptyList())
    val messages: StateFlow<List<MessagesData>> = _messages.asStateFlow()

    private val _getterUsersData = mutableStateOf<List<UsersData?>>(emptyList())
    val getterUsersData: State<List<UsersData?>> = _getterUsersData

    private val _groupId = mutableStateOf<String?>("")
    val groupId: State<String?> = _groupId

    fun setupNewConversation(currentUserId: String, getterUsers: List<String?>, groupName: String) {
        viewModelScope.launch {
            val groupId = firebaseRepository.createGroup(currentUserId, getterUsers, groupName)
            if (groupId != null) {
                initializeGroup(groupId, currentUserId)
                _groupId.value = groupId
            }
        }
    }

    fun initializeGroup(groupId: String, currentUserId: String) {
        viewModelScope.launch {
            launch {
                firebaseRepository.getMessages(groupId).collect { messagesList ->
                    _messages.value = messagesList
                }
            }
            val getterUsers = firebaseRepository.findUserByGroupId(groupId, currentUserId)
            _getterUsersData.value = getterUsers
            try {
                firebaseRepository.markMessageAsRead(groupId, currentUserId)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отметке сообщения как прочитанного: ${e.message}")
            }
        }
    }

    fun sendMessage(groupId: String,
                    currentUserId: String,
                    getterUsers: List<UsersData?>,
                    text: String) {
        viewModelScope.launch {
            try {
                firebaseRepository.sendGroupMessages(groupId, currentUserId, getterUsers, text)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отправке сообщения ${e.message}")
            }
        }
    }

}