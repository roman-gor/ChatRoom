package com.gorman.chatroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.R
import com.gorman.chatroom.data.MoreScreenData
import com.gorman.chatroom.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    val language: StateFlow<String> = settingsRepository.languageFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "en"
    )

    val darkMode: StateFlow<Boolean> = settingsRepository.darkModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val notificationsMute: StateFlow<Boolean> = settingsRepository.muteNotificationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

     val items = listOf(
        MoreScreenData(0, R.drawable.language, R.string.language_item),
        MoreScreenData(1, R.drawable.dark_mode, R.string.dark_mode),
        MoreScreenData(2, R.drawable.mute_notification, R.string.mute_notification)
    )

    fun changeLanguage(value: String){
        viewModelScope.launch {
            settingsRepository.setLanguage(value)
        }
    }

    fun changeDarkModeState(enabled: Boolean){
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }

    fun changeNotificationsState(muted: Boolean){
        viewModelScope.launch {
            settingsRepository.setMuteNotifications(muted)
        }
    }
}