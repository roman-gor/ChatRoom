package com.gorman.chatroom.viewmodel

import androidx.lifecycle.ViewModel
import com.gorman.chatroom.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupConversationViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {

}