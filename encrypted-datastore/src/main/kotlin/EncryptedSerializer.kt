package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.Serializer
import com.google.crypto.tink.Aead
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

internal class EncryptedSerializer<T>(
    private val aead: Aead,
    private val delegate: Serializer<T>,
) : Serializer<T> by delegate {

    override suspend fun readFrom(input: InputStream): T {
        val encryptedBytes = input.readBytes()
        val bytes = if (encryptedBytes.isEmpty()) encryptedBytes else aead.decrypt(encryptedBytes, null)

        return delegate.readFrom(bytes.inputStream())
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        val bytes = ByteArrayOutputStream().use { stream ->
            delegate.writeTo(t, stream)
            stream.toByteArray()
        }
        val encryptedBytes = aead.encrypt(bytes, null)

        @Suppress("BlockingMethodInNonBlockingContext")
        output.write(encryptedBytes)
    }
}

public fun <T> Serializer<T>.encrypted(aead: Aead): Serializer<T> = EncryptedSerializer(aead, this)
