package com.gorman.chatroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.data.GroupsData
import com.gorman.chatroom.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupsScreenViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

    private val _groupsState = MutableStateFlow<List<GroupsData?>>(emptyList())
    val groupsState: StateFlow<List<GroupsData?>> = _groupsState

    fun initGroupPreview(userId: String) {

    }

    fun getUserGroups(userId: String) {
        viewModelScope.launch {
            firebaseRepository.getUserGroups(userId).collect {
                _groupsState.value = it
            }
        }
    }
}