package com.gorman.chatroom.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.gorman.chatroom.data.SettingsKeys
import com.gorman.chatroom.data.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {

    val userIdFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.USER_ID] ?: "S3BYeApReBhWlgMW4b9BrYpfCXH3" }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.LANGUAGE] ?: getCurrentLanguage(context) }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.DARK_MODE] ?: false }

    val muteNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.MUTE_NOTIFICATIONS] ?: false }

    val hideChatHistoryFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.HIDE_CHAT_HISTORY] ?: true }

    val securityFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.SECURITY] ?: false }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.USER_ID] = id
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.LANGUAGE] = lang
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.DARK_MODE] = enabled
        }
    }

    suspend fun setMuteNotifications(muted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.MUTE_NOTIFICATIONS] = muted
        }
    }

    suspend fun setHideChatHistory(hided: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.HIDE_CHAT_HISTORY] = hided
        }
    }

    suspend fun setSecurity(secure: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.SECURITY] = secure
        }
    }

    fun getCurrentLanguage(context: Context): String {
        return context.resources.configuration.locales[0].language
    }
}
