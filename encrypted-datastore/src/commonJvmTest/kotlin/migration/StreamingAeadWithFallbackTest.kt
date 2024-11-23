package io.github.osipxd.datastore.encrypted.migration

import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.StreamingAead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.streamingaead.StreamingAeadConfig
import io.github.osipxd.datastore.encrypted.TestAssets
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StreamingAeadWithFallbackTest {

    private var aead: Aead
    private var streamingAead: StreamingAead

    @field:TempDir
    lateinit var tempDir: Path

    init {
        AeadConfig.register()
        StreamingAeadConfig.register()

        aead = TestAssets.generateAead()
        streamingAead = TestAssets.generateStreamingAead()
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
    fun `decrypt encrypted bytes stream with decryption fallback`() {
        val plaintext = "Plaintext"
        val encryptedStream = aead.encrypt(plaintext.toByteArray(), null).inputStream()
        val decryptingStream = streamingAead.withDecryptionFallback(aead)
            .newDecryptingStream(encryptedStream, byteArrayOf())

        val decrypted = decryptingStream.readBytes().decodeToString()
        assertEquals(plaintext, decrypted)
    }

    @Test
    fun `decrypt encrypted file stream with decryption fallback`() {
        val plaintext = "Plaintext"

        val file = tempDir / "encrypted-file"
        file.writeBytes(aead.encrypt(plaintext.toByteArray(), null))

        val encryptedStream = file.inputStream()
        val decryptingStream = streamingAead.withDecryptionFallback(aead)
            .newDecryptingStream(encryptedStream, byteArrayOf())

        val decrypted = decryptingStream.readBytes().decodeToString()
        assertEquals(plaintext, decrypted)
    }
}
