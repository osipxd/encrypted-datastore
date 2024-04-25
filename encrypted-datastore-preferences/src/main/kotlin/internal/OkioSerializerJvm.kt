package io.github.osipxd.datastore.encrypted.internal

import androidx.annotation.RestrictTo
import androidx.datastore.core.Serializer
import androidx.datastore.core.okio.OkioSerializer
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

private class OkioToJvmSerializerAdapter<T>(
    private val delegate: OkioSerializer<T>,
) : Serializer<T> {

    override val defaultValue: T
        get() = delegate.defaultValue

    override suspend fun readFrom(input: InputStream): T {
        return delegate.readFrom(input.source().buffer())
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        delegate.writeTo(t, output.sink().buffer())
    }
}

public fun <T> OkioSerializer<T>.asJvmSerialiser(): Serializer<T> = OkioToJvmSerializerAdapter(this)
