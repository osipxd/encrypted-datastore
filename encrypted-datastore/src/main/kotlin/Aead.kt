package io.github.osipxd.datastore.encrypted

import com.google.crypto.tink.Aead
import java.io.InputStream

internal fun Aead.newDecryptedStream(inputStream: InputStream): InputStream {
    return if (inputStream.available() > 0) {
        decrypt(inputStream.readBytes(), null).inputStream()
    } else {
        inputStream
    }
}
