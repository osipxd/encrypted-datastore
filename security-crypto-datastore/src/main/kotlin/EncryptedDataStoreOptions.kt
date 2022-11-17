package io.github.osipxd.security.crypto

import com.google.crypto.tink.Aead

/** A class holding DataStore encryption options. */
public class EncryptedDataStoreOptions internal constructor() {

    /** The associated data. By default, will be used data store file name. */
    public var associatedData: ByteArray? = null

    /** The fallback [Aead]. You should provide it only if te datastore was previously encrypted with [Aead]. */
    public var fallbackAead: Aead? = null
}
