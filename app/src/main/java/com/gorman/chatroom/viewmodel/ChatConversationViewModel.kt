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
import kotlinx.coroutines.flow.Flow
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

    fun initializeChat(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            firebaseRepository.getMessages(chatId).collect { messagesList ->
                _messages.value = messagesList
            }
        }
        viewModelScope.launch {
            try {
                firebaseRepository.markMessageAsRead(chatId, currentUserId)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при обработке сообщение ${e.message}")
            }
        }
        viewModelScope.launch {
            _getterUserData.value = firebaseRepository.findUserByChatId(chatId, currentUserId)
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