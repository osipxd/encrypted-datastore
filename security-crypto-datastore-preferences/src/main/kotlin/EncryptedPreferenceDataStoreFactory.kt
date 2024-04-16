@file:Suppress("UnusedReceiverParameter")

package io.github.osipxd.security.crypto

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedFile
import io.github.osipxd.datastore.encrypted.PreferenceDataStore
import io.github.osipxd.datastore.encrypted.PreferencesSerializer
import io.github.osipxd.datastore.encrypted.checkPreferenceDataStoreFileExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Creates Preferences DataStore instance stored in [EncryptedFile].
 * The file must have the extension "preferences_pb".
 *
 * Basic usage:
 * ```
 * val dataStore = PreferenceDataStoreFactory.createEncrypted {
 *     EncryptedFile.Builder(
 *          context.dataStoreFile("filename.preferences_pb"),
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
 *         file = context.dataStoreFile("filename.preferences_pb"),
 *         masterKey = MasterKey(context)
 *     )
 * }
 * ```
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
        serializer = PreferencesSerializer,
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
