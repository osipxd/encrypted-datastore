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
 * **Deprecated** in favor of version with [StreamingAead].
 * You can not use to decrypt data encrypted with `Aead`,
 * so you can not just replace `Aead` with `StreamingAead` without migration.
 * To not lose your previously encrypted data, you have three options:
 *  1. **Migration** - add fallback for `StreamingAead` using function [StreamingAead.withDecryptionFallback]
 *  2. **Do nothing** - continue to use this method `Aead`
 *  3. **Destructive migration** - specify [ReplaceFileCorruptionHandler] to replace old content with something else
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
