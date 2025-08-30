package com.gorman.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.data.MessagesData
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _chatsList = MutableStateFlow<List<ChatsData?>>(emptyList())
    val chatsList: StateFlow<List<ChatsData?>> = _chatsList.asStateFlow()

    private val _getterUserData = mutableStateOf<UsersData?>(UsersData())
    val getterUserData: State<UsersData?> = _getterUserData

    private val _messageData = mutableStateOf<MessagesData?>(MessagesData())
    val messageData: State<MessagesData?> = _messageData

    private val _unreadQuantity = mutableIntStateOf(0)
    val unreadQuantity: State<Int> = _unreadQuantity

    fun getUserChats(userId: String) {
        viewModelScope.launch {
            firebaseRepository.getUserChats(userId).collect { chatsList ->
                _chatsList.value = chatsList
            }
        }
    }

    fun initChatPreview(chatId: String, currentUserId: String, lastMessageId: String) {
        viewModelScope.launch {
            _getterUserData.value = firebaseRepository.findUserByChatId(chatId, currentUserId)
        }
        viewModelScope.launch {
            firebaseRepository.getMessages(chatId).collect { messagesList ->
                _messageData.value = messagesList.find { it.messageId == lastMessageId }
            }
        }
        viewModelScope.launch {
            firebaseRepository.getUnreadMessagesQuantity(chatId, currentUserId).collect { quantity ->
                _unreadQuantity.intValue = quantity
            }
        }
    }
}