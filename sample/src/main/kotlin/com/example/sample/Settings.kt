@file:OptIn(ExperimentalSerializationApi::class)

package com.example.sample

import androidx.datastore.core.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Settings(
    val theme: ThemeMode = ThemeMode.Auto,
    val likes: Int = 0,
) {
    override fun toString(): String {
        return "Settings(\n  theme = $theme,\n  likes = $likes\n)"
    }
}

enum class ThemeMode {
    Light,
    Dark,
    Auto,
}

object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings()

    override suspend fun readFrom(input: InputStream): Settings {
        return Json.decodeFromStream<Settings>(input)
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        Json.encodeToStream(t, output)
    }
}
