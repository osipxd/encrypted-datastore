package com.example.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsStorage {
    val settingsWithDump: Flow<Pair<Settings, String>>

    suspend fun setTheme(theme: ThemeMode)
    suspend fun updateLikes(transform: (Int) -> Int)
    suspend fun clear()
}

class TypedSettingStorage(private val dataStore: DataStore<Settings>) : SettingsStorage {
    override val settingsWithDump = dataStore.data.map { it to it.toString() }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.updateData { it.copy(theme = theme) }
    }

    override suspend fun updateLikes(transform: (Int) -> Int) {
        dataStore.updateData { it.copy(likes = transform(it.likes)) }
    }

    override suspend fun clear() {
        dataStore.updateData { Settings() }
    }
}

class PreferencesSettingsStorage(private val dataStore: DataStore<Preferences>) : SettingsStorage {
    override val settingsWithDump = dataStore.data.map { preferences ->
        val settings = Settings(
            theme = preferences[themeKey]?.let(ThemeMode::valueOf) ?: ThemeMode.Auto,
            likes = preferences[likesKey] ?: 0,
        )
        settings to preferences.toString()
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { it[themeKey] = theme.name }
    }

    override suspend fun updateLikes(transform: (Int) -> Int) {
        dataStore.edit { it[likesKey] = transform(it[likesKey] ?: 0) }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private companion object {
        val themeKey = stringPreferencesKey("theme")
        val likesKey = intPreferencesKey("likes")
    }
}
