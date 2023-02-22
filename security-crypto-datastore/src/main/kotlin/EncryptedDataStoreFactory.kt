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
 * Creates DataStore instance stored in [EncryptedFile].
 *
 * Basic usage:
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
 *
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
