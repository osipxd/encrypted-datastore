@file:Suppress("FunctionName")

package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.google.crypto.tink.Aead
import com.google.crypto.tink.StreamingAead
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

/**
 * Creates preference DataStore encrypted using the given [aead].
 *
 * **Deprecated.**
 * It is recommended to migrate to `security-crypto-datastore` library:
 * [Migration guide](https://github.com/osipxd/encrypted-datastore#migration)
 */
@Deprecated("Use version of this method with StreamingAead instead of Aead")
@Suppress("UnusedReceiverParameter")
public fun PreferenceDataStoreFactory.createEncrypted(
    aead: Aead,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    migrations: List<DataMigration<Preferences>> = listOf(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    produceFile: () -> File,
): DataStore<Preferences> {
    @Suppress("DEPRECATION")
    val delegate = DataStoreFactory.create(
        serializer = PreferencesSerializer.encrypted(aead),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        scope = scope,
        produceFile = produceFile()::checkPreferenceDataStoreFileExtension,
    )

    return PreferenceDataStore(delegate)
}

/**
 * Proto based serializer for Preferences, encrypted using th given [StreamingAead] and [associatedData].
 *
 * Can be used to manually create [DataStore][androidx.datastore.core.DataStore] using the
 * [DataStoreFactory#create][androidx.datastore.core.DataStoreFactory.create] function.
 */
public val PreferencesSerializer: Serializer<Preferences> = PreferenceDataStoreHack.serializer

/** Exposes PreferenceDataStore constructor. */
public fun PreferenceDataStore(delegate: DataStore<Preferences>): DataStore<Preferences> =
    PreferenceDataStoreHack.wrap(delegate)

// Equals to androidx.datastore.preferences.PreferencesSerializer.fileExtension
private const val FILE_EXTENSION = "preferences_pb"

/** Checks that [this] file have valid extension for Preference DataStore */
public fun File.checkPreferenceDataStoreFileExtension(): File = apply {
    check(extension == FILE_EXTENSION) {
        "File extension for file: $this does not match required extension for Preferences file: $FILE_EXTENSION"
    }
}
