## [Unreleased]

:warning: **Breaking change:** `PreferenceDataStoreFactory.createEncrypted` extension has been moved to separated module. To continue use it, change the dependency module in your build script:

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
