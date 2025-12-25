package com.gorman.chatroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.domain.models.ChatPreviewData
import com.gorman.chatroom.domain.usecases.DeleteChatUseCase
import com.gorman.chatroom.domain.usecases.FindUserByChatIdUseCase
import com.gorman.chatroom.domain.usecases.GetLastMessageUseCase
import com.gorman.chatroom.domain.usecases.GetUnreadMessagesQuantityUseCase
import com.gorman.chatroom.domain.usecases.GetUserChatsUseCase
import com.gorman.chatroom.ui.states.ChatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _chatsUiState = MutableStateFlow<ChatsUiState>(ChatsUiState.Idle)
    val chatsUiState = _chatsUiState.asStateFlow()

    fun loadAllChats(userId: String) {
        viewModelScope.launch {
            _chatsUiState.value = ChatsUiState.Loading
            try {
                getUserChatsUseCase(userId).collect { chatsDataList ->
                    if (chatsDataList.isEmpty()) {
                        _chatsUiState.value = ChatsUiState.Success(emptyList())
                        return@collect
                    }
                    val previewFlows = chatsDataList.filterNotNull().map { chat ->
                        val getterUserData = findUserByChatIdUseCase(chat.chatId!!, userId)

                        combine(
                            getLastMessageUseCase(chat.chatId),
                            getUnreadMessagesQuantityUseCase(chat.chatId, userId)
                        ) { lastMessage, quantity ->
                            chat.chatId to ChatPreviewData(
                                chatId = chat.chatId,
                                user = getterUserData,
                                lastMessage = if (lastMessage?.timestamp != "") lastMessage else null,
                                unreadQuantity = quantity
                            )
                        }
                    }
                    combine(previewFlows) { previews ->
                        previews.map { it.second }
                    }.collect { fullPreviewList ->
                        _chatsUiState.value = ChatsUiState.Success(fullPreviewList)
                    }
                }
            } catch (e: Exception) {
                _chatsUiState.value = ChatsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            deleteChatUseCase(chatId = chatId)
        }
    }
}