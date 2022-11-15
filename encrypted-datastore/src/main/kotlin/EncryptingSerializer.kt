package io.github.osipxd.datastore.encrypted

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.crypto.tink.Aead
import com.google.crypto.tink.StreamingAead
import io.github.osipxd.datastore.encrypted.migration.isProbablyEncryptedWithAeadException
import java.io.*
import java.security.GeneralSecurityException

/** Interface for [Serializer] supporting encryption. */
public sealed interface EncryptingSerializer<T> : Serializer<T>

internal abstract class WrappingEncryptingSerializer<T> : EncryptingSerializer<T> {

    protected abstract val delegate: Serializer<T>

    final override val defaultValue: T
        get() = delegate.defaultValue

    final override suspend fun readFrom(input: InputStream): T {
        return try {
            readEncryptedFrom(input)
        } catch (e: GeneralSecurityException) {
            throw CorruptionException("DataStore decryption failed", e)
        }
    }

    /** Reads encrypted data from the given [input]. */
    protected abstract suspend fun readEncryptedFrom(input: InputStream): T
}

internal class AeadEncryptingSerializer<T>(
    private val aead: Aead,
    override val delegate: Serializer<T>,
) : WrappingEncryptingSerializer<T>() {

    override suspend fun readEncryptedFrom(input: InputStream): T {
        return delegate.readFrom(aead.newDecryptedStream(input))
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        val bytes = ByteArrayOutputStream().use { stream ->
            delegate.writeTo(t, stream)
            stream.toByteArray()
        }
        val encryptedBytes = aead.encrypt(bytes, null)

        // This method is called on Dispatchers.IO by default
        @Suppress("BlockingMethodInNonBlockingContext")
        output.write(encryptedBytes)
    }
}

@Deprecated(
    "Use version of this method with StreamingAead instead of Aead",
    ReplaceWith(
        "this.encrypted(streamingAead.withDecryptionFallback(aead))",
        "io.github.osipxd.datastore.encrypted.migration.withDecryptionFallback"
    ),
)
public fun <T> Serializer<T>.encrypted(aead: Aead): EncryptingSerializer<T> =
    AeadEncryptingSerializer(aead, delegate = this)

internal class StreamingAeadEncryptingSerializer<T>(
    private val streamingAead: StreamingAead,
    private val associatedData: ByteArray,
    override val delegate: Serializer<T>,
) : WrappingEncryptingSerializer<T>() {

    override suspend fun readEncryptedFrom(input: InputStream): T {
        return try {
            delegate.readFrom(streamingAead.newDecryptingStream(input, associatedData))
        } catch (e: IOException) {
            throw e.toFriendlyException()
        }
    }

    private fun IOException.toFriendlyException(): Exception {
        return if (isProbablyEncryptedWithAeadException()) {
            CorruptionException(
                "Can not decrypt DataStore using StreamingAead.\n" +
                        "Probably you should add decryption fallback to Aead:\n" +
                        "https://github.com/osipxd/encrypted-datastore#migration-to-streamingaead",
                cause = this,
            )
        } else {
            this
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        delegate.writeTo(t, streamingAead.newEncryptingStream(output, associatedData))
    }
}

public fun <T> Serializer<T>.encrypted(
    streamingAead: StreamingAead,
    associatedData: ByteArray = byteArrayOf(),
): EncryptingSerializer<T> = StreamingAeadEncryptingSerializer(streamingAead, associatedData, delegate = this)
