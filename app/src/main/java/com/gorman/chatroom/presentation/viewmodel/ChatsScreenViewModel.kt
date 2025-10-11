package com.gorman.chatroom.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.models.ChatPreviewData
import com.gorman.chatroom.domain.models.ChatsData
import com.gorman.chatroom.domain.usecases.DeleteChatUseCase
import com.gorman.chatroom.domain.usecases.FindUserByChatIdUseCase
import com.gorman.chatroom.domain.usecases.GetLastMessageUseCase
import com.gorman.chatroom.domain.usecases.GetUnreadMessagesQuantityUseCase
import com.gorman.chatroom.domain.usecases.GetUserChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val getUserChatsUseCase: GetUserChatsUseCase,
    private val findUserByChatIdUseCase: FindUserByChatIdUseCase,
    private val getLastMessageUseCase: GetLastMessageUseCase,
    private val getUnreadMessagesQuantityUseCase: GetUnreadMessagesQuantityUseCase,
    private val deleteChatUseCase: DeleteChatUseCase
): ViewModel() {

    private val _chatsList = MutableStateFlow<List<ChatsData?>>(emptyList())
    val chatsList: StateFlow<List<ChatsData?>> = _chatsList.asStateFlow()

    private val _chatPreviews = MutableStateFlow<Map<String, ChatPreviewData>>(emptyMap())
    val chatPreviews: StateFlow<Map<String, ChatPreviewData>> = _chatPreviews.asStateFlow()

    fun initChatPreview(chatId: String, currentUserId: String) {
        viewModelScope.launch {
            Log.d("ViewModel", "Init viewmodel")
            val getterUserData = findUserByChatIdUseCase(chatId, currentUserId)
            val flow = combine(
                getLastMessageUseCase(chatId),
                getUnreadMessagesQuantityUseCase(chatId, currentUserId)
            ) { lastMessage, quantity ->
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
            getUserChatsUseCase(userId).collect { chatsList ->
                _chatsList.value = chatsList
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            deleteChatUseCase(chatId = chatId)
        }
    }
}