package io.github.osipxd.datastore.encrypted.migration

import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.StreamingAead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.streamingaead.StreamingAeadConfig
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StreamingAeadWithFallbackTest {

    private var aead: Aead
    private var streamingAead: StreamingAead

    init {
        AeadConfig.register()
        StreamingAeadConfig.register()

        aead = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
            .getPrimitive(Aead::class.java)
        streamingAead = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM_HKDF_4KB"))
            .getPrimitive(StreamingAead::class.java)
    }

    @Test
    fun `decrypt encrypted stream without decryption fallback`() {
        val plaintext = "Plaintext"
        val encryptedStream = aead.encrypt(plaintext.toByteArray(), null).inputStream()
        val decryptingStream = streamingAead.newDecryptingStream(encryptedStream, byteArrayOf())

        val throwable = assertThrows<IOException> { decryptingStream.readBytes() }
        assertEquals("No matching key found for the ciphertext in the stream.", throwable.message)
    }

    @Test
    fun `decrypt encrypted stream with decryption fallback`() {
        val plaintext = "Plaintext"
        val encryptedStream = aead.encrypt(plaintext.toByteArray(), null).inputStream()
        val decryptingStream = streamingAead.withDecryptionFallback(aead)
            .newDecryptingStream(encryptedStream, byteArrayOf())

        val decrypted = decryptingStream.readBytes().decodeToString()
        assertEquals(plaintext, decrypted)
    }
}
