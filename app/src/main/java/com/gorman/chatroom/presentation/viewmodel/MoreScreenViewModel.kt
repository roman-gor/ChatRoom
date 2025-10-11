package com.gorman.chatroom.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.chatroom.R
import com.gorman.chatroom.domain.models.MoreScreenData
import com.gorman.chatroom.domain.repository.SettingsRepository
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

    val hideChatHistory: StateFlow<Boolean> = settingsRepository.hideChatHistoryFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val security: StateFlow<Boolean> = settingsRepository.securityFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

     val items = listOf(
         MoreScreenData(0, R.drawable.language, R.string.language_item, false),
         MoreScreenData(1, R.drawable.dark_mode, R.string.dark_mode, false),
         MoreScreenData(2, R.drawable.mute_notification, R.string.mute_notification, false),
         MoreScreenData(3, R.drawable.invite_friend, R.string.invite_friend, true),
         MoreScreenData(4, R.drawable.joined_groups, R.string.joined_groups, true),
         MoreScreenData(5, R.drawable.hide_chat_history, R.string.hide_chat_history, false),
         MoreScreenData(6, R.drawable.security, R.string.security, false),
         MoreScreenData(7, R.drawable.term_of_services, R.string.term_of_service, true),
         MoreScreenData(8, R.drawable.about_app, R.string.about_app, true),
         MoreScreenData(9, R.drawable.help_center, R.string.help, true),
         MoreScreenData(10, R.drawable.logout_icon, R.string.logout, true)
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

    fun setHideChatHistory(hided: Boolean){
        viewModelScope.launch {
            settingsRepository.setHideChatHistory(hided)
        }
    }

    fun setSecurity(secure: Boolean){
        viewModelScope.launch {
            settingsRepository.setSecurity(secure)
        }
    }
}