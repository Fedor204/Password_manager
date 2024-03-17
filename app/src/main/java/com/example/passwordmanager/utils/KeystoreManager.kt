package com.example.passwordmanager.utils

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.nio.charset.StandardCharsets

class KeystoreManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val file = File(context.filesDir, "master_password.txt")
    private val encryptedFile = EncryptedFile.Builder(
        context,
        file,
        masterKey,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    fun saveMasterPassword(password: String) {
        encryptedFile.openFileOutput().use { outputStream ->
            outputStream.write(password.toByteArray(StandardCharsets.UTF_8))
        }
    }

    fun getMasterPassword(): String? {
        return if (file.exists()) {
            encryptedFile.openFileInput().use { inputStream ->
                inputStream.readBytes().toString(StandardCharsets.UTF_8)
            }
        } else {
            null
        }
    }
}
