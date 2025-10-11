package com.gorman.chatroom.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.UsersData
import com.gorman.chatroom.domain.usecases.CurrentUserDataUseCase
import com.gorman.chatroom.domain.usecases.UpdateUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    currentUserDataUseCase: CurrentUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
): ViewModel() {

    private val _userData = MutableStateFlow(UsersData())
    val userData: StateFlow<UsersData> = _userData

    private val _profileItems = mutableStateOf<Map<Int, String?>>(emptyMap())
    val profileItems: State<Map<Int, String?>> = _profileItems

    val currentUserData: StateFlow<UsersData?> = currentUserDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            currentUserData.filterNotNull().collect { user ->
                _userData.value = user
                _profileItems.value = getProfileItemsFromObject(user)
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