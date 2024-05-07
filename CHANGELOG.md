## [Unreleased]

### Fixed

- Fixed Preference DataStore saving (#38)

### Housekeeping

- Added sample application

## [1.1.1-beta01] - 2024.05.04

### DataStore 1.1.0+

> [!NOTE]
> Ensure that the version of this library aligns with the DataStore library version used in your project.
> This version should only be used with [`datastore:1.1.1`](https://developer.android.com/jetpack/androidx/releases/datastore#1.1.1), as it depends on new APIs introduced in that release.

Starting with version 1.1.0, DataStore has become a multiplatform library.
However, encrypted datastore is currently limited to JVM and Android targets due to dependencies on JVM-specific libraries like Tink and `security-crypto`.
I am actively exploring ways to add support for more targets to the library.
The research will take some time, so I've released a beta version compatible with DataStore 1.1.0 for developers not requiring multiplatform targets support.

### Changes

- **Potentially breaking change:** The field `io.github.osipxd.datastore.encrypted.PreferencesSerializer` has been hidden from the public API.
  Instead, directly use the object `PreferencesSerializer`, which is now publicly available starting with the `datastore:1.1.0` release.
- Added the `@RestrictTo(LIBRARY_GROUP)` annotation to public members that are intended for internal use only, ensuring they are not used externally.

### Housekeeping

- Added a binary compatibility validator to prevent unintentional breaks in binary compatibility

## [1.0.0] - 2024.04.17

### Delegates to create encrypted DataStores

Delegates `encryptedDataStore` and `encryptedPreferencesDataStore` were added to simplify DataStore creation.
If you have the following code:

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

You can simplify it using delegate for DataStore creation.

```kotlin
// 1. Move the field to top level of you Kotlin file and turn it to an extension on Context
// 2. Replace `DataStoreFactory.createEncrypted` with `encryptedDataStore`
val Context.dataStore by encryptedDataStore(
    fileName = "filename", // Keep file the same
    serializer = serializer,
)
```

> [!NOTE]
> This only will be interchangeable if you used `context.dataStoreFile(...)` to create datastore file.
> In case you have custom logic for master key creation, pass the created master key as a parameter `masterKey` to the delegate.

### Dependencies

- Target JVM `1.8` → `11`
- Kotlin `1.7.21` → `1.9.23`
- [Tink](https://github.com/tink-crypto/tink-java/releases) `1.7.0` → `1.13.0`

### Housekeeping

- Update Gradle to 8.7
- Update AGP to 8.3.2
- Move `PreferenceDataStoreHack.java` into `encrypted-datastore-preferences` module

## [1.0.0-beta01] - 2023.02.26

### Fixed

- Fixed decryption fallback from StreamingAead to Aead (#15)
- Change min SDK 21 → 23 to align it with security-crypto 1.0.0

### Housekeeping

- Update Gradle to 7.6.1

## [1.0.0-alpha04] - 2022.12.19

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

[unreleased]: https://github.com/osipxd/encrypted-datastore/compare/1.1.1-beta01...main
[1.1.1-beta01]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0...1.1.1-beta01
[1.0.0]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-beta01...v1.0.0
[1.0.0-beta01]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-alpha04...v1.0.0-beta01
[1.0.0-alpha04]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-alpha03...v1.0.0-alpha04
[1.0.0-alpha03]: https://github.com/osipxd/encrypted-datastore/compare/v1.0.0-alpha02...v1.0.0-alpha03
