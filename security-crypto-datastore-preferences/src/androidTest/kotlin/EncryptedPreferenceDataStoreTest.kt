package io.github.osipxd.security.crypto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

internal class EncryptedPreferenceDataStoreTest {

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val file = context.preferencesDataStoreFile(DATASTORE_NAME)

    @Test
    fun encryptAndDecrypt(): Unit = runTest {
        val dataKey = stringPreferencesKey("testKey")
        val plaintext = "Plaintext"

        // Write data and close DataStore
        coroutineScope {
            val dataStore = createDataStore(this)
            dataStore.edit { it[dataKey] = plaintext }
        }

        // Read data
        val dataStore = createDataStore(dataStoreScope)
        val preferences = dataStore.data.first()

        assertEquals(plaintext, preferences[dataKey])
    }

    private val Context.dataStore by encryptedPreferencesDataStore(DATASTORE_NAME, scope = dataStoreScope)

    @Test
    fun encryptUsingFactoryAndDecryptUsingDelegate() = runTest {
        val dataKey = stringPreferencesKey("testKey")
        val plaintext = "I can use both factory and delegate"

        // Write data and close DataStore
        coroutineScope {
            // Create DataStore using factory
            val dataStore = createDataStore(this)
            dataStore.edit { it[dataKey] = plaintext }
        }

        // Read data from delegate
        val preferences = context.dataStore.data.first()

        assertEquals(plaintext, preferences[dataKey])
    }

    private fun runTest(block: suspend CoroutineScope.() -> Unit) {
        runBlocking { withTimeout(5.seconds, block) }
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
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

    private companion object {
        const val DATASTORE_NAME = "test"
    }
}
