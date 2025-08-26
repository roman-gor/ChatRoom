package com.gorman.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainScreenViewModel: ViewModel() {

    private val _searchState = mutableStateOf("")
    val searchState: State<String> = _searchState

    fun onSearchValueChanged(value: String) { _searchState.value = value }

}