package com.gorman.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.R
import com.gorman.chatroom.data.ProfileItems
import com.gorman.chatroom.data.UsersData
import com.gorman.chatroom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _userData = mutableStateOf(UsersData())
    val userData: State<UsersData> = _userData

    fun getUserByID(userId: String) {
        viewModelScope.launch {
            _userData.value = firebaseRepository.getUserById(userId)
        }
    }

    fun getProfileItemsFromObject(obj: UsersData): Map<Int, String?>{
        return mapOf(
            R.string.phone to obj.phone,
            R.string.gender to obj.gender,
            R.string.birthday to obj.birthday,
            R.string.email to obj.email
        )
    }
}