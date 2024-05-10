# Encrypted DataStore
[![Version](https://img.shields.io/maven-central/v/io.github.osipxd/encrypted-datastore?style=flat-square)][mavenCentral] [![License](https://img.shields.io/github/license/osipxd/encrypted-datastore?style=flat-square)][license]

Extensions to store DataStore into `EncryptedFile`.

> [!WARNING]
> This library will continue to be maintained, but active development will continue until an official DataStore encryption solution is released by Google.
> Support the development of this feature by voting for it on the issue tracker: [b/167697691](https://issuetracker.google.com/issues/167697691).

---

## Installation

Add the dependency:

```kotlin
repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.github.osipxd:security-crypto-datastore:1.1.1-beta03")
    // Or, if you want to use Preferences DataStore:
    implementation("io.github.osipxd:security-crypto-datastore-preferences:1.1.1-beta03")
}
```

> **Dependencies:**
> - `security-crypto` [1.0.0](https://developer.android.com/jetpack/androidx/releases/security#1.0.0)
> - `datastore` [1.1.1](https://developer.android.com/jetpack/androidx/releases/datastore#1.1.1)
> - `tink` [1.13.0](https://github.com/tink-crypto/tink-java/releases/tag/v1.13.0)

> [!NOTE]
> Ensure that the version of this library aligns with the DataStore library version used in your project.

## Usage

To create an encrypted DataStore, simply replace the `dataStore` method with `encryptedDataStore` when setting up your delegate:

```kotlin
// At the top level of your Kotlin file:
val Context.settingsDataStore: DataStore<Settings> by encryptedDataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)
```

<details>
<summary>Or, if you want full control over <code>EncryptedFile</code> creation</summary>

```kotlin
val settingsDataStore: DataStore<Settings> = DataStoreFactory.createEncrypted(SettingsSerializer) {
    EncryptedFile.Builder(
        context.dataStoreFile("settings.pb"),
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}
```
</details>

Similarly, you can create Preferences DataStore:

```kotlin
// At the top level of your Kotlin file:
val Context.dataStore by encryptedPreferencesDataStore(name = "settings")
```

<details>
<summary>Or, if you want full control over <code>EncryptedFile</code> creation</summary>

```kotlin
val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createEncrypted {
    EncryptedFile.Builder(
        context.preferencesDataStoreFile("settings"),
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}
```
</details>

Once your encrypted DataStore is configured, you can use it in the same manner as a regular DataStore.
For usage guides and examples, refer to the [DataStore documentation](https://developer.android.com/topic/libraries/architecture/datastore).

## Migration

### Migrating from DataStoreFactory to delegate usage

If you are starting with the following code:

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

To simplify the creation of DataStore using a delegate, follow these steps:

1. Move the field to the top level of your Kotlin file and convert it into an extension on `Context`.
2. Replace `DataStoreFactory.createEncrypted` with `encryptedDataStore`.

```kotlin
val Context.dataStore by encryptedDataStore(
    fileName = "filename", // Keep file the same
    serializer = serializer,
)
```

> [!NOTE]
> This only will be interchangeable if you have used `context.dataStoreFile(...)` to create the datastore file.
> If you have custom logic for master key creation, ensure to pass the created master key as the `masterKey` parameter to the delegate.

### Migrating from `encrypted-datastore` to `security-crypto-datastore`

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
// At the top level of your Kotlin file:
val Context.dataStore by encryptedDataStore(
    fileName = "filename", // Keep file the same
    serializer = serializer,
    encryptionOptions = {
        // Specify fallback Aead to make it possible to decrypt data encrypted with it
        fallbackAead = aead
    }
)
```

<details>
<summary>Or, if you want full control over <code>EncryptedFile</code> creation</summary>

```kotlin
val dataStore = DataStoreFactory.createEncrypted(
    serializer = serializer,
    encryptionOptions = { fallbackAead = aead }
) {
    EncryptedFile.Builder(
        context.dataStoreFile("filename"), // Keep file the same
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
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

[tink]: https://github.com/tink-crypto/tink-java
[secured-datastore]: https://github.com/Fi5t/secured-datastore
[fi5t]: https://github.com/Fi5t
[hack]: encrypted-datastore-preferences/src/main/java/io/github/osipxd/datastore/encrypted/PreferenceDataStoreHack.java
