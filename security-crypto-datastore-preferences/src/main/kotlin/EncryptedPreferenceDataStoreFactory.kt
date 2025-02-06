@file:Suppress("UnusedReceiverParameter")

package com.dayanruben.security.crypto

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.security.crypto.EncryptedFile
import com.dayanruben.datastore.encrypted.PreferenceDataStore
import com.dayanruben.datastore.encrypted.checkPreferenceDataStoreFileExtension
import com.dayanruben.datastore.encrypted.internal.asJvmSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Creates Preferences DataStore instance stored in [EncryptedFile]. Never create more than one
 * instance of DataStore for a given file; doing so can break all DataStore functionality.
 * You should consider managing your DataStore instance as a singleton.
 *
 * Example usage:
 * ```
 * val dataStore = PreferenceDataStoreFactory.createEncrypted {
 *     EncryptedFile.Builder(
 *          context.preferencesDataStoreFile("filename"),
 *          context,
 *          MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
 *          EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
 *     ).build()
 * }
 * ```
 *
 * Or even simpler, if you use `security-crypto-ktx:1.1.0`:
 * ```
 * val dataStore = PreferenceDataStoreFactory.createEncrypted {
 *     EncryptedFile(
 *         context = context,
 *         file = context.preferencesDataStoreFile("filename"),
 *         masterKey = MasterKey(context)
 *     )
 * }
 * ```
 *
 * @param corruptionHandler The corruptionHandler is invoked if DataStore encounters a
 * [CorruptionException] when attempting to read data. CorruptionExceptions are thrown by
 * serializers when data cannot be de-serialized.
 * @param migrations are run before any access to data can occur. Each producer and migration
 * may be run more than once whether or not it already succeeded (potentially because another
 * migration failed or a write to disk failed.)
 * @param scope The scope in which IO operations and transform functions will execute.
 * @param encryptionOptions Additional encryption options.
 * @param produceFile Function which returns the [EncryptedFile] that the new DataStore will act on.
 * The function must return the same path every time. No two instances of PreferenceDataStore
 * should act on the same file at the same time. The file must have the extension
 * preferences_pb.
 *
 * @see encryptedPreferencesDataStore
 * @see PreferenceDataStoreFactory.create
 */
public fun PreferenceDataStoreFactory.createEncrypted(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    migrations: List<DataMigration<Preferences>> = listOf(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    encryptionOptions: EncryptedDataStoreOptions.() -> Unit = {},
    produceFile: () -> EncryptedFile,
): DataStore<Preferences> {
    val delegate = DataStoreFactory.createEncrypted(
        serializer = PreferencesSerializer.asJvmSerializer(),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        scope = scope,
        encryptionOptions = encryptionOptions,
        produceFile = produceFile()::checkPreferenceDataStoreFileExtension,
    )

    return PreferenceDataStore(delegate)
}

private fun EncryptedFile.checkPreferenceDataStoreFileExtension(): EncryptedFile = apply {
    file.checkPreferenceDataStoreFileExtension()
}
