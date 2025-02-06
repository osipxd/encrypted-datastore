package com.dayanruben.security.crypto

import android.content.Context
import androidx.annotation.GuardedBy
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedFile.FileEncryptionScheme
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Creates a property delegate for a single process DataStore with encryption. This should only
 * be called once in a file (at the top level), and all usages of the DataStore should use
 * a reference the same Instance. The receiver type for the property delegate must be an instance
 * of [Context].
 *
 * This should only be used from a single application in a single classloader in a single process.
 *
 * Example usage:
 * ```
 * val Context.settingsDataStore by encryptedPreferencesDataStore(name = "settings")
 *
 * class SomeClass(val context: Context) {
 *    suspend fun update() = context.settingsDataStore.edit {...}
 * }
 * ```
 *
 * @param name The name of the preferences. The preferences will be stored in a file in the
 * "datastore/" subdirectory in the application context's files directory and is generated using
 * [preferencesDataStoreFile].
 * @param corruptionHandler The corruptionHandler is invoked if DataStore encounters a
 * [androidx.datastore.core.CorruptionException] when attempting to read data. CorruptionExceptions
 * are thrown by serializers when data can not be de-serialized.
 * @param produceMigrations produce the migrations. The ApplicationContext is passed in to these
 * callbacks as a parameter. DataMigrations are run before any access to data can occur. Each
 * producer and migration may be run more than once whether or not it already succeeded
 * (potentially because another migration failed or a write to disk failed.)
 * @param scope The scope in which IO operations and transform functions will execute.
 * @param masterKey The master key to use, defaults to `AES256-GCM` key with default alias
 * [MasterKeys.MASTER_KEY_ALIAS].
 * @param fileEncryptionScheme The [FileEncryptionScheme] to use, defaulting to
 * [FileEncryptionScheme.AES256_GCM_HKDF_4KB].
 * @param encryptionOptions Additional encryption options.
 *
 * @return a property delegate that manages a datastore as a singleton.
 *
 * @see PreferenceDataStoreFactory.createEncrypted
 * @see EncryptedFile
 */
public fun encryptedPreferencesDataStore(
    name: String,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    produceMigrations: (Context) -> List<DataMigration<Preferences>> = { emptyList() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    masterKey: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    fileEncryptionScheme: FileEncryptionScheme = FileEncryptionScheme.AES256_GCM_HKDF_4KB,
    encryptionOptions: EncryptedDataStoreOptions.() -> Unit = {},
): ReadOnlyProperty<Context, DataStore<Preferences>> {
    return EncryptedPreferenceDataStoreSingletonDelegate(
        name = name,
        corruptionHandler = corruptionHandler,
        produceMigrations = produceMigrations,
        scope = scope,
        masterKey = masterKey,
        fileEncryptionScheme = fileEncryptionScheme,
        encryptionOptions = encryptionOptions,
    )
}

/**
 * Delegate class to manage Preferences DataStore as a singleton.
 * Copied from [androidx.datastore.preferences.PreferenceDataStoreSingletonDelegate]
 */
internal class EncryptedPreferenceDataStoreSingletonDelegate internal constructor(
    private val name: String,
    private val corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    private val produceMigrations: (Context) -> List<DataMigration<Preferences>>,
    private val scope: CoroutineScope,
    private val masterKey: String,
    private val fileEncryptionScheme: FileEncryptionScheme,
    private val encryptionOptions: EncryptedDataStoreOptions.() -> Unit
) : ReadOnlyProperty<Context, DataStore<Preferences>> {

    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var INSTANCE: DataStore<Preferences>? = null

    /**
     * Gets the instance of the DataStore.
     *
     * @param thisRef must be an instance of [Context]
     * @param property not used
     */
    override fun getValue(thisRef: Context, property: KProperty<*>): DataStore<Preferences> {
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {
                val applicationContext = thisRef.applicationContext

                INSTANCE = PreferenceDataStoreFactory.createEncrypted(
                    corruptionHandler = corruptionHandler,
                    migrations = produceMigrations(applicationContext),
                    scope = scope,
                    encryptionOptions = encryptionOptions,
                ) {
                    EncryptedFile.Builder(
                        applicationContext.preferencesDataStoreFile(name),
                        applicationContext,
                        masterKey,
                        fileEncryptionScheme,
                    ).build()
                }
            }
            INSTANCE!!
        }
    }
}