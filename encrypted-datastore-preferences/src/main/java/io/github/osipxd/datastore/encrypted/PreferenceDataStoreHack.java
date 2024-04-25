package io.github.osipxd.datastore.encrypted;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.PreferenceDataStore;
import androidx.datastore.preferences.core.Preferences;

import org.jetbrains.annotations.NotNull;

// We want to wrap our DataStore with PreferenceDataStore, but can't access it from Kotlin
// because it has internal visibility modifier.
// We still can access it from Java, so we do this dirty hack to bypass internal visibility.
// Package-private visibility used to make this hack visible only to our own Kotlin code in the same package.
@SuppressWarnings({"KotlinInternalInJava", "unused"})
class PreferenceDataStoreHack {

    private PreferenceDataStoreHack() {
        // Shouldn't be instantiated
    }

    @NotNull
    static DataStore<Preferences> wrap(DataStore<Preferences> delegate) {
        return new PreferenceDataStore(delegate);
    }
}
