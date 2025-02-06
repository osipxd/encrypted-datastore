package com.dayanruben.datastore.encrypted

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object StringSerializer : Serializer<String> {
    override val defaultValue: String = ""
    override suspend fun readFrom(input: InputStream): String = String(input.readBytes())
    override suspend fun writeTo(t: String, output: OutputStream) = output.write(t.encodeToByteArray())
}
