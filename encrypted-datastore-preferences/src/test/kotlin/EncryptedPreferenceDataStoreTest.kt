package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.io.path.name
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EncryptedPreferenceDataStoreTest {

    private val dataStorePath = TestAssets.path("encryptedDataStore.preferences_pb")
    private val dataKey = stringPreferencesKey("testKey")
    private val dataValue = "Decrypt me if you can!"

    init {
        AeadConfig.register()
    }

    @Test
    fun `decrypt data ecrypted with previous version`() {
        val dataStore = createDataStore(TestAssets.loadAead())
        val decryptedData = runBlocking { dataStore.data.first() }

        assertEquals(expected = dataValue, actual = decryptedData.get(dataKey))
    }

    @Test
    @Ignore("Run this test only when you really need to generate new key or encrypt new data")
    fun `generate key and encrypt data`() {
        val dataStore = createDataStore(TestAssets.generateAead(saveToDisk = true))
        runBlocking { dataStore.edit { it[dataKey] = dataValue } }
    }

    private fun createDataStore(aead: Aead): DataStore<Preferences> {
        @Suppress("DEPRECATION")
        return PreferenceDataStoreFactory.createEncrypted(
            aead = aead,
            produceFile = { dataStorePath.toFile() }
        )
    }
}
