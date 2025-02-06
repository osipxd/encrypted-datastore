package com.dayanruben.security.crypto

import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.file
import java.io.File

/** Exposes the underlying file. */
public val EncryptedFile.file: File
    get() = file
