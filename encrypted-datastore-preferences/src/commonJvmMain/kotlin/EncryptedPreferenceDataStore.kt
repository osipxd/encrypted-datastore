@file:Suppress("FunctionName")

package com.dayanruben.datastore.encrypted

import androidx.annotation.RestrictTo
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.google.crypto.tink.Aead
import com.dayanruben.datastore.encrypted.internal.asJvmSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

/**
 * Creates preference DataStore encrypted using the given [aead].
 *
 * **Deprecated.**
 * It is recommended to migrate to `security-crypto-datastore` library:
 * [Migration guide](https://github.com/dayanruben/encrypted-datastore#migration)
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
        serializer = PreferencesSerializer.asJvmSerializer().encrypted(aead),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        scope = scope,
        produceFile = produceFile()::checkPreferenceDataStoreFileExtension,
    )

    return PreferenceDataStore(delegate)
}

@Deprecated("Use PreferencesSerializer directly", level = DeprecationLevel.HIDDEN)
public val PreferencesSerializer: Serializer<Preferences> = PreferencesSerializer.asJvmSerializer()

/** Exposes PreferenceDataStore constructor. */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public fun PreferenceDataStore(delegate: DataStore<Preferences>): DataStore<Preferences> =
    androidx.datastore.preferences.core.PreferenceDataStore(delegate)

// Equals to androidx.datastore.preferences.PreferencesSerializer.fileExtension
private const val FILE_EXTENSION = "preferences_pb"

/** Checks that [this] file have valid extension for Preference DataStore */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public fun File.checkPreferenceDataStoreFileExtension(): File = apply {
    check(extension == FILE_EXTENSION) {
        "File extension for file: $this does not match required extension for Preferences file: $FILE_EXTENSION"
    }
}
