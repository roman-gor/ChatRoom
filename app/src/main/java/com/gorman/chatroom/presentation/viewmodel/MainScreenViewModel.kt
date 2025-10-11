package com.gorman.chatroom.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.presentation.MessengerUiState
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.domain.repository.SettingsRepository
import com.gorman.chatroom.domain.usecases.FindUserByPhoneNumberUseCase
import com.gorman.chatroom.domain.usecases.LoadNewUserUseCase
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
    private val loadNewUserUseCase: LoadNewUserUseCase,
    private val findUserByPhoneNumberUseCase: FindUserByPhoneNumberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MessengerUiState>(MessengerUiState.Loading)

    private val _isUserIdLoaded = MutableStateFlow(false)
    val isUserIdLoaded: StateFlow<Boolean> = _isUserIdLoaded.asStateFlow()

    private val _searchState = mutableStateOf("")
    val searchState: State<String> = _searchState

    private val _userData = MutableStateFlow<UsersData?>(UsersData())

    private val _isPhoneNumberExist = MutableStateFlow<Boolean?>(null)
    val isPhoneNumberExist: StateFlow<Boolean?> = _isPhoneNumberExist

    private val _isUserDataLoaded = MutableStateFlow(false)

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

    fun loadNewUser(user: UsersData) {
        viewModelScope.launch {
            val loadingResult = loadNewUserUseCase(user)
            if (loadingResult && user.userId != null) {
                _isUserDataLoaded.value = true
                setUserId(user.userId)
            }
        }
    }

    fun setUserId(id: String) {
        viewModelScope.launch {
            settingsRepository.setUserId(id)
        }
    }

    fun onSearchValueChanged(value: String) { _searchState.value = value }

    fun findUserByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            findUserByPhoneNumberUseCase(phoneNumber).collect { user ->
                if (user != null) {
                    _userData.value = user
                    user.userId?.let {
                        setUserId(it)
                    }
                    _isPhoneNumberExist.value = true
                }
                else {
                    Log.d("ViewModel", "false")
                    _isPhoneNumberExist.value = false
                }
            }
        }
    }

}