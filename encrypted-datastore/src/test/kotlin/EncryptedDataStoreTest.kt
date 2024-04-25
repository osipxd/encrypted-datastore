package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.google.crypto.tink.StreamingAead
import com.google.crypto.tink.streamingaead.StreamingAeadConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.name
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EncryptedDataStoreTest {

    private val dataStorePath = TestAssets.path("encryptedDataStore")
    private val data = "Decrypt me if you can!"

    init {
        StreamingAeadConfig.register()
    }

    @Test
    fun `decrypt data ecrypted with previous version`() {
        val dataStore = createDataStore(TestAssets.loadStreamingAead())
        val decryptedData = runBlocking { dataStore.data.first() }

        assertEquals(expected = data, actual = decryptedData)
    }

    @Test
    @Ignore("Run this test only when you really need to generate new key or encrypt new data")
    fun `generate key and encrypt data`() {
        val dataStore = createDataStore(TestAssets.generateStreamingAead(saveToDisk = true))
        runBlocking { dataStore.updateData { data } }
    }

    private fun createDataStore(streamingAead: StreamingAead): DataStore<String> {
        val associatedData = dataStorePath.name.toByteArray()
        return DataStoreFactory.create(
            serializer = StringSerializer.encrypted(streamingAead, associatedData),
            produceFile = { dataStorePath.toFile() }
        )
    }
}