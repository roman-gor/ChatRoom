package com.gorman.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.data.MessengerUiState
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.repository.FirebaseRepository
import com.gorman.chatroom.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MessengerUiState>(MessengerUiState.Loading)
    val uiState: StateFlow<MessengerUiState> = _uiState.asStateFlow()

    private val _isUserIdLoaded = MutableStateFlow(false)
    val isUserIdLoaded: StateFlow<Boolean> = _isUserIdLoaded.asStateFlow()

    private val _searchState = mutableStateOf("")
    val searchState: State<String> = _searchState

    private val _userData = MutableStateFlow<UsersData?>(UsersData())

    val userId: StateFlow<String> = settingsRepository.userIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    init {
        viewModelScope.launch {
            _uiState.value = MessengerUiState.Loading

            val currentUserId = settingsRepository.userIdFlow.first()

            if (currentUserId.isNotEmpty()) {
                _uiState.value = MessengerUiState.Success
            } else {
                _uiState.value = MessengerUiState.Login
            }
            _isUserIdLoaded.value = true
        }
    }

    private fun setUserId(id: String) {
        viewModelScope.launch {
            settingsRepository.setUserId(id)
        }
    }

    fun onSearchValueChanged(value: String) { _searchState.value = value }

    fun findUserByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            firebaseRepository.findUserByPhoneNumber(phoneNumber).collect { user ->
                _userData.value = user
                user?.userId?.let {
                    setUserId(it)
                }
            }
        }
    }

}