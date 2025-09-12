package com.gorman.chatroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.data.ChatPreviewData
import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _chatsList = MutableStateFlow<List<ChatsData?>>(emptyList())
    val chatsList: StateFlow<List<ChatsData?>> = _chatsList.asStateFlow()

    private val _chatPreviews = MutableStateFlow<Map<String, ChatPreviewData>>(emptyMap())
    val chatPreviews: StateFlow<Map<String, ChatPreviewData>> = _chatPreviews.asStateFlow()

    fun initChatPreview(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            Log.d("ViewModel", "Init viewmodel")
            val getterUserData = firebaseRepository.findUserByChatId(chatId, currentUserId)
            val flow = combine(
                firebaseRepository.getMessages(chatId),
                firebaseRepository.getUnreadMessagesQuantity(chatId, currentUserId)
            ) { messagesList, quantity ->
                val lastMessage = messagesList.maxByOrNull {
                    runCatching { Instant.parse(it.timestamp).toEpochMilli() }.getOrDefault(0L)
                }
                Log.d("Last message", "$lastMessage")
                if (lastMessage?.timestamp != "") {
                    ChatPreviewData(
                        user = getterUserData,
                        lastMessage = lastMessage,
                        unreadQuantity = quantity
                    )
                }
                else {
                    ChatPreviewData(
                        user = getterUserData,
                        lastMessage = null,
                        unreadQuantity = quantity
                    )
                }
            }
            flow.collect { data ->
                _chatPreviews.value = _chatPreviews.value.toMutableMap().apply {
                    this[chatId] = data
                }
            }
        }
    }

    fun getUserChats(userId: String) {
        viewModelScope.launch {
            firebaseRepository.getUserChats(userId).collect { chatsList ->
                _chatsList.value = chatsList
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            firebaseRepository.deleteChat(chatId)
        }
    }
}