package com.gorman.chatroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.core.R
import com.gorman.chatroom.domain.usecases.UpdateUserDataUseCase
import com.gorman.chatroom.ui.states.ProfileUiState
import com.gorman.core.domain.models.UsersData
import com.gorman.feature_chats.domain.usecases.CurrentUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    currentUserDataUseCase: CurrentUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
): ViewModel() {

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState = _profileUiState.asStateFlow()

    val currentUserData: StateFlow<UsersData?> = currentUserDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading
            try {
                currentUserData.filterNotNull().collect { user ->
                    _profileUiState.value = ProfileUiState.Success(
                        usersData = user,
                        profileItems = getProfileItemsFromObject(user)
                    )
                }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private fun getProfileItemsFromObject(obj: UsersData?): Map<Int, String?>{
        return mapOf(
            R.string.phone to obj?.phone,
            R.string.gender to obj?.gender,
            R.string.birthday to obj?.birthday,
            R.string.email to obj?.email
        )
    }

    fun updateUserData(userId: String, user: UsersData?) {
        viewModelScope.launch {
            updateUserDataUseCase(userId, user)
        }
    }
}
