## [Unreleased]

### Fixed

- Fixed the case when data can not be read if output stream was not closed in serializer (#10)
- Added default value for parameter `encryptionOptions` in `PreferenceDataStoreFactory.createEncrypted` (#12)

## [1.0.0-alpha03] - 2022.11.18

#### More high-level library `security-crypto-datastore`

New library provides more simple and less error-prone API to create encrypted DataStores.
All Tink-related stuff hidden from you in `security-crypto` library, and all you should do is wrap `File` with `EncryptedFile`:

```kotlin
val dataStore = DataStoreFactory.createEncrypted(serializer) {
    EncryptedFile.Builder(
        context.dataStoreFile("filename"),
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}
```

Or even simpler, if you use `security-crypto-ktx:1.1.0`:

```kotlin
val dataStore = DataStoreFactory.createEncrypted(serializer) {
    EncryptedFile(
        context = context,
        file = context.dataStoreFile("filename"),
        masterKey = MasterKey(context)
    )
}
```

See the [Migration guide](README.md#migration).

#### Streaming serializer

Introduced new extension-function `Serializer.encrypted(StreamingAead)` to encrypt DataStore in streaming manner.
Old extension-function with `Aead` is not planned to be removed yet, but for all new code it is recommended to use the new function or migrate to the `security-crypto-datastore`.

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

[unreleased]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-alpha03...main
[1.0.0-alpha03]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-alpha02...v1.0.0-alpha03
