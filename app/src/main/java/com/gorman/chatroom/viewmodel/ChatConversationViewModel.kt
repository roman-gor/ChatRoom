package com.gorman.chatroom.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ChatConversationViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _messages = MutableStateFlow<List<MessagesData>>(emptyList())
    val messages: StateFlow<List<MessagesData>> = _messages.asStateFlow()

    private val _getterUserData = mutableStateOf<UsersData?>(UsersData())
    val getterUserData: State<UsersData?> = _getterUserData

    fun setupNewConversation(currentUserId: String, getterUserId: String) {
        viewModelScope.launch {
            val chatId = firebaseRepository.setupNewConversation(currentUserId, getterUserId)
            if (chatId != null)
                initializeChat(chatId, currentUserId)
        }
    }

    fun initializeChat(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            launch {
                firebaseRepository.getMessages(chatId).collect { messagesList ->
                    _messages.value = messagesList
                }
            }
            val getterUser = firebaseRepository.findUserByChatId(chatId, currentUserId)
            _getterUserData.value = getterUser
            try {
                firebaseRepository.markMessageAsRead(chatId, currentUserId)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отметке сообщения как прочитанного: ${e.message}")
            }
        }
    }

    fun sendMessage(chatId: String,
                            currentUserId: String,
                            getterId: String,
                            text: String) {
        viewModelScope.launch {
            try {
                firebaseRepository.sendMessage(chatId, currentUserId, getterId, text)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отправке сообщения ${e.message}")
            }
        }
    }
}