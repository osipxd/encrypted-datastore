package io.github.osipxd.security.crypto

import android.content.Context
import androidx.annotation.GuardedBy
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
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
 * val Context.settingsDataStore by encryptedDataStore(
 *     fileName = "settings.pb",
 *     serializer = SettingsSerializer,
 * )
 *
 * class SomeClass(val context: Context) {
 *    suspend fun update() = context.settingsDataStore.updateData {...}
 * }
 * ```
 *
 * @param fileName the filename relative to Context.applicationContext.filesDir that DataStore
 * acts on. The File is obtained from [dataStoreFile]. It is created in the "/datastore"
 * subdirectory.
 * @param serializer The serializer for `T`.
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
 * @see DataStoreFactory.createEncrypted
 * @see EncryptedFile
 */
public fun <T> encryptedDataStore(
    fileName: String,
    serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    produceMigrations: (Context) -> List<DataMigration<T>> = { emptyList() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    masterKey: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    fileEncryptionScheme: FileEncryptionScheme = FileEncryptionScheme.AES256_GCM_HKDF_4KB,
    encryptionOptions: EncryptedDataStoreOptions.() -> Unit = {},
): ReadOnlyProperty<Context, DataStore<T>> {
    return EncryptedDataStoreSingletonDelegate(
        fileName = fileName,
        serializer = serializer,
        corruptionHandler = corruptionHandler,
        produceMigrations = produceMigrations,
        scope = scope,
        masterKey = masterKey,
        fileEncryptionScheme = fileEncryptionScheme,
        encryptionOptions = encryptionOptions,
    )
}

/**
 * Delegate class to manage DataStore as a singleton.
 * Copied from [androidx.datastore.DataStoreSingletonDelegate]
 */
internal class EncryptedDataStoreSingletonDelegate<T> internal constructor(
    private val fileName: String,
    private val serializer: Serializer<T>,
    private val corruptionHandler: ReplaceFileCorruptionHandler<T>?,
    private val produceMigrations: (Context) -> List<DataMigration<T>>,
    private val scope: CoroutineScope,
    private val masterKey: String,
    private val fileEncryptionScheme: FileEncryptionScheme,
    private val encryptionOptions: EncryptedDataStoreOptions.() -> Unit
) : ReadOnlyProperty<Context, DataStore<T>> {

    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var INSTANCE: DataStore<T>? = null

    /**
     * Gets the instance of the DataStore.
     *
     * @param thisRef must be an instance of [Context]
     * @param property not used
     */
    override fun getValue(thisRef: Context, property: KProperty<*>): DataStore<T> {
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {
                val applicationContext = thisRef.applicationContext
                INSTANCE = DataStoreFactory.createEncrypted(
                    corruptionHandler = corruptionHandler,
                    serializer = serializer,
                    migrations = produceMigrations(applicationContext),
                    scope = scope,
                    encryptionOptions = encryptionOptions,
                ) {
                    EncryptedFile.Builder(
                        applicationContext.dataStoreFile(fileName),
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
