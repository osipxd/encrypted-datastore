# encrypted-datastore
[![Version](https://img.shields.io/maven-central/v/io.github.osipxd/encrypted-datastore?style=flat-square)][mavenCentral] [![License](https://img.shields.io/github/license/osipxd/encrypted-datastore?style=flat-square)][license]

Extensions to encrypt DataStore using [Tink].

> :warning: This tiny library will be maintained until an official solution for DataStore encryption will be released by Google.

---

## Installation

Add the dependency:

```kotlin
repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.github.osipxd:encrypted-datastore:1.0.0-alpha02")
}
```

## Usage

First, you need `Aead` object to encrypt DataStore or you may use already created one:

```kotlin
val aead = AndroidKeysetManager.Builder()
    .withSharedPref(context, "master_keyset", "master_key_preference")
    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
    .withMasterKeyUri("android-keystore://master_key")
    .build()
    .keysetHandle
    .getPrimitive(Aead::class.java)
```

Then you can make any DataStore Serializer encrypted using extension-function:

```kotlin
object ProtoProfileSerializer : Serializer<Profile> {
    // serializer implementation here
}

val dataStore = DataStoreFactory.create(ProtoProfileSerializer.encrypted(aead)) {
    context.dataStoreFile("proto_profile")
}
```

If you need to create encrypted `PreferenceDataStore`, use function `createEncrypted` instead of `create`:

```kotlin
val prefsDataStore = PreferenceDataStoreFactory.createEncrypted(aead) {
    context.preferencesDataStoreFile("user_preferences")
}
```

### Thanks

Artem Kulakov (@Fi5t) for his [example][secured-datastore] of DataStore encryption.

## License

[MIT][license]


[mavenCentral]: https://search.maven.org/artifact/io.github.osipxd/encrypted-datastore
[license]: LICENSE

[tink]: https://github.com/google/tink
[secured-datastore]: https://github.com/Fi5t/secured-datastore
