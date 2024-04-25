package io.github.osipxd.datastore.encrypted

import com.google.crypto.tink.Aead
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.StreamingAead
import com.google.crypto.tink.TinkJsonProtoKeysetFormat
import com.google.crypto.tink.proto.Keyset
import java.nio.file.Path
import kotlin.io.path.*

object TestAssets {
    private val testAssetsDir = Path("../testAssets") // Path relative to subproject root

    private val keyPath = path("key.json")
    private val streamingKeyPath = path("stearmingKey.json")

    fun path(name: String): Path = testAssetsDir / name

    fun generateAead(saveToDisk: Boolean = false): Aead {
        val keyset = generateKeyset(KeyTemplates.get("AES256_GCM"), keyPath.takeIf { saveToDisk })
        return keyset.getPrimitive(Aead::class.java)
    }

    fun generateStreamingAead(saveToDisk: Boolean = false): StreamingAead {
        val keyset = generateKeyset(KeyTemplates.get("AES256_GCM_HKDF_4KB"), streamingKeyPath.takeIf { saveToDisk })
        return keyset.getPrimitive(StreamingAead::class.java)
    }

    private fun generateKeyset(keyTemplate: KeyTemplate, path: Path? = null): KeysetHandle {
        val keyset = KeysetHandle.generateNew(keyTemplate)
        val serializedKeyset = TinkJsonProtoKeysetFormat.serializeKeyset(keyset, InsecureSecretKeyAccess.get())

        if (path != null) {
            if (path.notExists()) {
                path.createParentDirectories()
                path.createFile()
            }
            path.writeText(serializedKeyset)
        }

        return keyset
    }

    fun loadAead(): Aead = loadKeyset(keyPath).getPrimitive(Aead::class.java)
    fun loadStreamingAead(): StreamingAead = loadKeyset(streamingKeyPath).getPrimitive(StreamingAead::class.java)

    private fun loadKeyset(path: Path): KeysetHandle {
        check(path.isRegularFile()) {
            "Can't load keyset from '${path.absolutePathString()}'. Make sure you've generated keyset before"
        }
        return TinkJsonProtoKeysetFormat.parseKeyset(path.readText(), InsecureSecretKeyAccess.get())
    }
}
