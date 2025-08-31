package com.gorman.chatroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {
    private val _userData = MutableStateFlow<UsersData?>(UsersData())
    val usersData: StateFlow<UsersData?> = _userData

    private var searchJob: Job? = null

    fun findUserByPhoneNumber(phoneNumber: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            firebaseRepository.findUserByPhoneNumber(phoneNumber).collect { usersData ->
                _userData.value = usersData
            }
        }
    }
}