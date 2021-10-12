package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.google.crypto.tink.Aead
import io.github.osipxd.datastore.encrypted.PreferenceDataStoreHack.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

@Suppress("unused")
public fun PreferenceDataStoreFactory.createEncrypted(
    aead: Aead,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    migrations: List<DataMigration<Preferences>> = listOf(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    produceFile: () -> File,
): DataStore<Preferences> {
    val delegate = DataStoreFactory.create(
        serializer = serializer.encrypted(aead),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        scope = scope
    ) {
        val file = produceFile()
        check(file.extension == fileExtension) {
            "File extension for file: $file does not match required extension for" +
                    " Preferences file: $fileExtension"
        }
        file
    }

    return wrap(delegate)
}
