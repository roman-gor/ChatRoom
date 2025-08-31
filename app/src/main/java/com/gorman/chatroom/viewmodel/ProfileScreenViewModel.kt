package com.gorman.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.R
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.repository.FirebaseRepository
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
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _userData = MutableStateFlow(UsersData())
    val userData: StateFlow<UsersData> = _userData

    private val _profileItems = mutableStateOf<Map<Int, String?>>(emptyMap())
    val profileItems: State<Map<Int, String?>> = _profileItems

    val currentUserData: StateFlow<UsersData?> = firebaseRepository.currentUserData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            currentUserData.filterNotNull().collect { user ->
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
}