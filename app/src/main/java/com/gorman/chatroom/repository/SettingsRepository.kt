package com.gorman.chatroom.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.gorman.chatroom.data.SettingsKeys
import com.gorman.chatroom.data.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {

    val languageFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.LANGUAGE] ?: "ru" }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.DARK_MODE] ?: false }

    val muteNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SettingsKeys.MUTE_NOTIFICATIONS] ?: false }

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
}
