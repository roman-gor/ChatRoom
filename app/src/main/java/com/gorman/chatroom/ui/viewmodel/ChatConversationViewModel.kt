package com.gorman.chatroom.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.domain.usecases.FindUserByChatIdUseCase
import com.gorman.chatroom.domain.usecases.GetMessagesUseCase
import com.gorman.chatroom.domain.usecases.MarkMessagesAsReadUseCase
import com.gorman.chatroom.domain.usecases.SendMessageUseCase
import com.gorman.chatroom.domain.usecases.SetupNewConversationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatConversationViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val findUserByChatIdUseCase: FindUserByChatIdUseCase,
    private val setupNewConversationUseCase: SetupNewConversationUseCase,
    private val markMessagesAsReadUseCase: MarkMessagesAsReadUseCase
): ViewModel() {

    private val _messages = MutableStateFlow<List<MessagesData>>(emptyList())
    val messages: StateFlow<List<MessagesData>> = _messages.asStateFlow()

    private val _getterUserData = mutableStateOf<UsersData?>(null)
    val getterUserData: State<UsersData?> = _getterUserData

    private val _chatId = mutableStateOf<String?>("")
    val chatId: State<String?> = _chatId

    fun setupNewConversation(currentUserId: String, getterUserId: String) {
        viewModelScope.launch {
            val chatId = setupNewConversationUseCase(currentUserId, getterUserId)
            if (chatId != null) {
                initializeChat(chatId, currentUserId)
                _chatId.value = chatId
            }
        }
    }

    fun initializeChat(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            launch {
                getMessagesUseCase(chatId).collect { messagesList ->
                    _messages.value = messagesList
                }
            }
            val getterUser = findUserByChatIdUseCase(chatId, currentUserId)
            _getterUserData.value = getterUser
            try {
                markMessagesAsReadUseCase(chatId, currentUserId)
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
                sendMessageUseCase(chatId, currentUserId, getterId, text)
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Ошибка при отправке сообщения ${e.message}")
            }
        }
    }
}