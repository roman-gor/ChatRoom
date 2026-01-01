package com.gorman.feature_chats.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.core.domain.models.ChatPreviewData
import com.gorman.core.domain.repository.SettingsRepository
import com.gorman.feature_chats.domain.usecases.DeleteChatUseCase
import com.gorman.feature_chats.domain.usecases.FindUserByChatIdUseCase
import com.gorman.feature_chats.domain.usecases.GetLastMessageUseCase
import com.gorman.feature_chats.domain.usecases.GetUnreadMessagesQuantityUseCase
import com.gorman.feature_chats.domain.usecases.GetUserChatsUseCase
import com.gorman.feature_chats.ui.states.ChatsUiState
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
    private val deleteChatUseCase: DeleteChatUseCase,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _chatsUiState = MutableStateFlow<ChatsUiState>(ChatsUiState.Idle)
    val chatsUiState = _chatsUiState.asStateFlow()

    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.userIdFlow.collect {
                _userId.value = it
            }
        }
    }

    fun loadAllChats(userId: String) {
        viewModelScope.launch {
            _chatsUiState.value = ChatsUiState.Loading
            try {
                getUserChatsUseCase(userId).collect { chatsDataList ->
                    if (chatsDataList.isEmpty()) {
                        _chatsUiState.value = ChatsUiState.Success(emptyList<ChatPreviewData>())
                        return@collect
                    }
                    val previewFlows = chatsDataList.filterNotNull().map { chat ->
                        val chatId = chat.chatId ?: ""
                        val getterUserData = findUserByChatIdUseCase(chatId, userId)
                        combine(
                            getLastMessageUseCase(chatId),
                            getUnreadMessagesQuantityUseCase(chatId, userId)
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
