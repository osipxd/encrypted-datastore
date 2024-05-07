package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.Serializer
import com.google.crypto.tink.StreamingAead
import com.google.crypto.tink.streamingaead.StreamingAeadConfig
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EncryptingSerializerTest {

    private var streamingAead: StreamingAead

    init {
        StreamingAeadConfig.register()

        streamingAead = TestAssets.generateStreamingAead()
    }

    @Test
    fun `encrypt and decrypt using serializer not closing stream`() {
        val plaintext = "Plaintext".toByteArray()
        val encryptingSerializer = AlwaysOpenStreamSerializer.encrypted(streamingAead)

        runBlocking {
            val bytesOutputStream = ByteArrayOutputStream()
            encryptingSerializer.writeTo(plaintext, bytesOutputStream)

            val bytes = bytesOutputStream.toByteArray()
            val decrypted = encryptingSerializer.readFrom(bytes.inputStream())

            assertEquals(plaintext.contentToString(), decrypted.contentToString())
        }
    }

    private object AlwaysOpenStreamSerializer : Serializer<ByteArray> {
        override val defaultValue: ByteArray = byteArrayOf()
        override suspend fun readFrom(input: InputStream): ByteArray = input.readBytes()
        override suspend fun writeTo(t: ByteArray, output: OutputStream) = output.write(t)
    }
}
