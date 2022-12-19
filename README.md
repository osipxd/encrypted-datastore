# Encrypted DataStore
[![Version](https://img.shields.io/maven-central/v/io.github.osipxd/encrypted-datastore?style=flat-square)][mavenCentral] [![License](https://img.shields.io/github/license/osipxd/encrypted-datastore?style=flat-square)][license]

Extensions to store DataStore in `EncryptedFile`.

> :warning: This tiny library will be maintained until an official solution for DataStore encryption will be released by Google. \
> Vote for this feature on issue tracker: [b/167697691](https://issuetracker.google.com/issues/167697691)

---

## Installation

Add the dependency:

```kotlin
repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.github.osipxd:security-crypto-datastore:1.0.0-alpha03")
    // Or, if you want to use Preferences DataStore:
    implementation("io.github.osipxd:security-crypto-datastore-preferences:1.0.0-alpha03")
}
```

> **Dependencies:**
> - `security-crypto` [1.0.0](https://developer.android.com/jetpack/androidx/releases/security#1.0.0)
> - `datastore` [1.0.0](https://developer.android.com/jetpack/androidx/releases/datastore#1.0.0)
> - `tink` [1.7.0](https://github.com/google/tink/releases/tag/v1.7.0)

## Usage

To create encrypted DataStore, just use method `DataStoreFactory.createEncryptred` instead of `create` and
provide `EncryptedFile` instead of `File`:

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

<details>
<summary>Or even simpler, if you use <code>security-crypto-ktx:1.1.0</code></summary>

> :warning: `security-crypto-ktx 1.1.0` is in alpha at the moment this library released, so use it at your own risk

```kotlin
val dataStore = DataStoreFactory.createEncrypted(serializer) {
    EncryptedFile(
        context = context,
        file = context.dataStoreFile("filename"),
        masterKey = MasterKey(context)
    )
}
```
</details>

Similarly, you can create Preferences DataStore:

```kotlin
val dataStore = PreferenceDataStoreFactory.createEncrypted {
    EncryptedFile.Builder(
        // The file should have extension .preferences_pb
        context.dataStoreFile("filename.preferences_pb"),
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}
```

<details>
<summary>Or even simpler, if you use <code>security-crypto-ktx:1.1.0</code></summary>

> :warning: `security-crypto-ktx 1.1.0` is in alpha at the moment this library released, so use it at your own risk

```kotlin
val dataStore = PreferenceDataStoreFactory.createEncrypted {
    EncryptedFile(
        context = context,
        // The file should have extension .preferences_pb
        file = context.dataStoreFile("filename.preferences_pb"),
        masterKey = MasterKey(context)
    )
}
```
</details>

## Migration

### Migrate from `encrypted-datastore` to `security-crypto-datastore`

Change the dependency in build script:

```diff
 dependencies {
-    implementation("io.github.osipxd:encrypted-datastore:...")
+    implementation("io.github.osipxd:security-crypto-datastore:...")
 }
```

New library uses `StreamingAead` instead of `Aead` under the hood, so to not lose the previously encrypted data you should specify `fallbackAead`:

```kotlin
// This AEAD was used to encrypt DataStore previously, we will use it as fallback
val aead = AndroidKeysetManager.Builder()
    .withSharedPref(context, "master_keyset", "master_key_preference")
    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
    .withMasterKeyUri("android-keystore://master_key")
    .build()
    .keysetHandle
    .getPrimitive(Aead::class.java)
```

The old code to create DataStore was looking like this:
```kotlin
val dataStore = DataStoreFactory.create(serializer.encrypted(aead)) {
    context.dataStoreFile("filename")
}
```

The new code will look like this:
```kotlin
val dataStore = DataStoreFactory.createEncrypted(
    serializer,
    encryptionOptions = {
        // Specify fallback Aead to make it possible to decrypt data encrypted with it
        fallbackAead = aead
    }
) {
    EncryptedFile.Builder(
        context.dataStoreFile("filename"), // Keep the same file
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}
```

<details>
<summary>Or even simpler, if you use <code>security-crypto-ktx:1.1.0</code></summary>

> :warning: `security-crypto-ktx 1.1.0` is in alpha at the moment this library released, so use it at your own risk

```kotlin
val dataStore = DataStoreFactory.createEncrypted(
    serializer,
    encryptionOptions = { fallbackAead = aead }
) {
    EncryptedFile(
        context = context,
        file = context.dataStoreFile("filename"), // Keep the same file
        masterKey = MasterKey(context)
    )
}
```
</details>

### Thanks

- Artem Kulakov ([Fi5t]), for his [example][secured-datastore] of DataStore encryption.
- Gods of Kotlin, for posibility to [hack] `internal` visibility modifier 

## License

[MIT][license]


[mavenCentral]: https://search.maven.org/artifact/io.github.osipxd/encrypted-datastore
[license]: LICENSE

[tink]: https://github.com/google/tink
[secured-datastore]: https://github.com/Fi5t/secured-datastore
[fi5t]: https://github.com/Fi5t
[hack]: encrypted-datastore-internal-visibility-hack/src/main/java/io/github/osipxd/datastore/encrypted/PreferenceDataStoreHack.java
