package com.gorman.chatroom.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsKeys {
    val LANGUAGE = stringPreferencesKey("language")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val MUTE_NOTIFICATIONS = booleanPreferencesKey("mute_notifications")
}
