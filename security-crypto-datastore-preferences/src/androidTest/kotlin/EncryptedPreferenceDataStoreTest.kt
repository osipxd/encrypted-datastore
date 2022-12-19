package io.github.osipxd.security.crypto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals

internal class EncryptedPreferenceDataStoreTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val file = context.dataStoreFile("test.preferences_pb")

    @Test
    fun encryptAndDecrypt(): Unit = runBlocking {
        val dataKey = stringPreferencesKey("testKey")
        val plaintext = "Plaintext"

        // Write data and close DataStore
        coroutineScope {
            val dataStore = createDataStore(this)
            dataStore.edit { it[dataKey] = plaintext }
        }

        // Read data
        val dataStore = createDataStore(this)
        val preferences = dataStore.data.first()

        assertEquals(plaintext, preferences[dataKey])
    }

    @After
    fun tearDown() {
        file.delete()
    }

    private fun createDataStore(scope: CoroutineScope): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createEncrypted(scope = scope) {
            EncryptedFile.Builder(
                file,
                context,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }
}
