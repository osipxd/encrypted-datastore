package com.example.sample

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dayanruben.security.crypto.encryptedDataStore
import com.dayanruben.security.crypto.encryptedPreferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Stable
class MainViewModel private constructor(
    application: Application,
    private val typedStorage: SettingsStorage,
    private val preferencesStorage: SettingsStorage,
) : AndroidViewModel(application) {

    @Suppress("unused") // Used by default ViewModel factory
    constructor(application: Application) : this(
        application,
        TypedSettingStorage(application.settingsDataStore),
        PreferencesSettingsStorage(application.preferencesDataStore),
    )

    private val stateFlow = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = stateFlow.asStateFlow()

    val theme: Flow<ThemeMode> = state.map { it.theme }

    private val selectedStorage
        get() = if (state.value.useTypedStorage) typedStorage else preferencesStorage

    init {
        typedStorage.settingsWithDump
            .onEach { (settings, dump) ->
                stateFlow.update { it.copy(typedStorage = createStorageState(settings, dump)) }
            }
            .launchIn(viewModelScope)

        preferencesStorage.settingsWithDump
            .onEach { (settings, dump) ->
                stateFlow.update { it.copy(preferencesStorage = createStorageState(settings, dump)) }
            }
            .launchIn(viewModelScope)
    }

    private fun createStorageState(settings: Settings, dump: String): StorageState {
        return StorageState(
            theme = settings.theme,
            likes = settings.likes,
            dataDump = dump,
        )
    }

    fun onThemeChange(theme: ThemeMode) {
        viewModelScope.launch { selectedStorage.setTheme(theme) }
    }

    fun onLikeClick() {
        viewModelScope.launch { selectedStorage.updateLikes { it + 1 } }
    }

    fun onResetLikesClick() {
        viewModelScope.launch { selectedStorage.updateLikes { 0 } }
    }

    fun onSelectStorage(useTypedStorage: Boolean) {
        stateFlow.update { it.copy(useTypedStorage = useTypedStorage) }
    }

    fun onClearClick() {
        viewModelScope.launch { selectedStorage.clear() }
    }
}

data class MainScreenState(
    val useTypedStorage: Boolean = true,
    val typedStorage: StorageState = StorageState(),
    val preferencesStorage: StorageState = StorageState(),
) {
    private val selectedStorage: StorageState
        get() = if (useTypedStorage) typedStorage else preferencesStorage

    val theme: ThemeMode get() = selectedStorage.theme
    val likes: Int get() = selectedStorage.likes
}

data class StorageState(
    val theme: ThemeMode = ThemeMode.Auto,
    val likes: Int = 0,
    val dataDump: String = "",
)

private val Context.settingsDataStore by encryptedDataStore("settings", SettingsSerializer)
private val Context.preferencesDataStore by encryptedPreferencesDataStore("prefs")
