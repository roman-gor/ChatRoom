package com.gorman.chatroom.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.data.ChatsData
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

    fun getUserChats(userId: String) {
        viewModelScope.launch {
            firebaseRepository.getUserChats(userId).collect { chatsList ->
                _chatsList.value = chatsList
            }
        }
    }
}