@file:Suppress("UnusedReceiverParameter")

package io.github.osipxd.security.crypto

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.file
import androidx.security.crypto.streamingAead
import com.google.crypto.tink.Aead
import com.google.crypto.tink.StreamingAead
import io.github.osipxd.datastore.encrypted.encrypted
import io.github.osipxd.datastore.encrypted.migration.withDecryptionFallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Create an instance of SingleProcessDataStore stored in [EncryptedFile]. Never create more
 * than one instance of DataStore for a given file; doing so can break all DataStore functionality.
 * You should consider managing your DataStore instance as a singleton. If there are multiple
 * DataStores active, DataStore will throw IllegalStateException when reading or updating data.
 * A DataStore is considered active as long as its scope is active.
 *
 * T is the type DataStore acts on. The type T must be immutable. Mutating a type used in
 * DataStore invalidates any guarantees that DataStore provides and will result in
 * potentially serious, hard-to-catch bugs.
 *
 * Example usage:
 * ```
 * val dataStore = DataStoreFactory.createEncrypted(serializer) {
 *     EncryptedFile.Builder(
 *          context.dataStoreFile("filename"),
 *          context,
 *          MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
 *          EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
 *     ).build()
 * }
 * ```
 *
 * Or even simpler, if you use `security-crypto-ktx:1.1.0`:
 * ```
 * val dataStore = DataStoreFactory.createEncrypted(serializer) {
 *     EncryptedFile(
 *         context = context,
 *         file = context.dataStoreFile("filename"),
 *         masterKey = MasterKey(context)
 *     )
 * }
 * ```
 * @param serializer Serializer for the type `T` used with DataStore. The type `T` must be immutable.
 * @param corruptionHandler The corruptionHandler is invoked if DataStore encounters a
 * [CorruptionException] when attempting to read data. CorruptionExceptions are thrown by
 * serializers when data can not be de-serialized.
 * @param migrations Migrations are run before any access to data can occur. Migrations must
 * be idempotent.
 * @param scope The scope in which IO operations and transform functions will execute.
 * @param encryptionOptions Additional encryption options.
 * @param produceFile Function which returns the [EncryptedFile] that the new DataStore will act on.
 * The function must return the same path every time. No two instances of DataStore should act on
 * the same file at the same time.
 *
 * @see encryptedDataStore
 * @see DataStoreFactory.create
 */
public fun <T> DataStoreFactory.createEncrypted(
    serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    migrations: List<DataMigration<T>> = listOf(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    encryptionOptions: EncryptedDataStoreOptions.() -> Unit = {},
    produceFile: () -> EncryptedFile
): DataStore<T> {
    val options = EncryptedDataStoreOptions().also(encryptionOptions)
    val encryptedFile = produceFile()

    val streamingAead = encryptedFile.streamingAead.withDecryptionFallbackIfNotNull(options.fallbackAead)
    val file = encryptedFile.file
    val associatedData = options.associatedData ?: file.name.toByteArray()

    return create(
        serializer = serializer.encrypted(streamingAead, associatedData),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        scope = scope,
        produceFile = { file },
    )
}

private fun StreamingAead.withDecryptionFallbackIfNotNull(fallbackAead: Aead?): StreamingAead {
    return if (fallbackAead != null) this.withDecryptionFallback(fallbackAead) else this
}
