package com.gorman.chatroom.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userIdFlow: Flow<String>
    val languageFlow: Flow<String>
    val darkModeFlow: Flow<Boolean>
    val muteNotificationsFlow: Flow<Boolean>
    val hideChatHistoryFlow: Flow<Boolean>
    val securityFlow: Flow<Boolean>
    suspend fun setUserId(id: String)
    suspend fun setLanguage(lang: String)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setMuteNotifications(muted: Boolean)
    suspend fun setHideChatHistory(hided: Boolean)
    suspend fun setSecurity(secure: Boolean)
    fun getCurrentLanguage(context: Context): String
}