## [Unreleased]

#### Streaming serializer

Introduced new extension-function `Serializer.encrypted(StreamingAead)` to encrypt DataStore in streaming manneer.
Old extension-function with `Aead` is not planned to be removed yet, but for all new code it is recommended to use the new function.
You can obtain `StreamingAead` similar to `Aead`:

```kotlin
// Remember to initialize Tink
//AeadConfig.register()
StreamingAeadConfig.register()

val handle = AndroidKeysetManager.Builder()
    .withSharedPref(context, "master_keyset", "master_key_preference")
    // Change key template AES256_GCM -> AES256_GCM_HKDF_4KB
    //.withKeyTemplate(KeyTemplates.get("AES256_GCM"))
    .withKeyTemplate(KeyTemplates.get("AES256_GCM_HKDF_4KB"))
    .withMasterKeyUri("android-keystore://master_key")
    .build()
    .keysetHandle

// Get StreamingAead instead of Aead
//val aead = handle.getPrimitive(Aead::class.java)
val streamingAead = handle.getPrimitive(StreamingAead::class.java)
```

> **ATTENTION!**
> You can not use `StreamingAead` to decrypt data encrypted with `Aead`,
> so you can not just replace `Aead` with `StreamingAead` without migration.
> To not lose your previously encrypted data, you have three options:
> 1. **Migration** - add fallback for `StreamingAead` using function `StreamingAead.withDecryptionFallback(Aead)`
> 2. **Do nothing** - continue to use `Aead`
> 3. **Destructive migration** - specify `CorruptionHandler` to replace old content with something else

#### New module `encrypted-datastore-preferences`

:warning: **Breaking change:** 

All stuff related to Preference DataStore was moved to `io.github.osipxd:encrypted-datastore-preferences`.
To continue use it, change the dependency module in your build script:

```diff
-implmentation("io.github.osipxd:encrypted-datastore:...")
+implmentation("io.github.osipxd:encrypted-datastore-preferences:...")
```

### Fixed

- Fixed crash when DataStore can not be decrypted (#1)

### Dependencies

- Kotlin `1.5.30` → `1.7.21`
- [Tink](https://github.com/google/tink/releases/tag/v1.7.0) `1.6.1` → `1.7.0`

### Housekeeping

- Gradle `7.2` → `7.5.1`
- gradle-infrastructure `0.12.1` → `0.17`
- Migrate dependencies to version catalogs

[unreleased]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-alpha02...main
