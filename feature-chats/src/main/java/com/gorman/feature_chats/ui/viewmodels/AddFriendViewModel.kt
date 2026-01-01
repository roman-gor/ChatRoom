package com.gorman.feature_chats.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.usecases.FindUserByPhoneNumberUseCase
import com.gorman.feature_chats.domain.usecases.CurrentUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    currentUserDataUseCase: CurrentUserDataUseCase,
    private val findUserByPhoneNumberUseCase: FindUserByPhoneNumberUseCase
): ViewModel() {
    private val _userData = MutableStateFlow<UsersData?>(UsersData())
    val usersData: StateFlow<UsersData?> = _userData

    private var searchJob: Job? = null

    val currentUserData: StateFlow<UsersData?> = currentUserDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun findUserByPhoneNumber(phoneNumber: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            findUserByPhoneNumberUseCase(phoneNumber).collect { usersData ->
                _userData.value = usersData
            }
        }
    }
}
