package com.gorman.chatroom.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.gorman.chatroom.data.SettingsKeys
import com.gorman.chatroom.data.dataStore
import com.gorman.chatroom.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override val userIdFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.USER_ID] ?: "" }

    override val languageFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.LANGUAGE] ?: getCurrentLanguage(context) }

    override val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.DARK_MODE] ?: false }

    override val muteNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.MUTE_NOTIFICATIONS] ?: false }

    override val hideChatHistoryFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.HIDE_CHAT_HISTORY] ?: true }

    override val securityFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.SECURITY] ?: false }

    override suspend fun setUserId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.USER_ID] = id
        }
    }

    override suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.LANGUAGE] = lang
        }
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.DARK_MODE] = enabled
        }
    }

    override suspend fun setMuteNotifications(muted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.MUTE_NOTIFICATIONS] = muted
        }
    }

    override suspend fun setHideChatHistory(hided: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.HIDE_CHAT_HISTORY] = hided
        }
    }

    override suspend fun setSecurity(secure: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.SECURITY] = secure
        }
    }

    override fun getCurrentLanguage(context: Context): String {
        return context.resources.configuration.locales[0].language
    }
}