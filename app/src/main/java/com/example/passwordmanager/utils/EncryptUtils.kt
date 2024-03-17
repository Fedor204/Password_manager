package com.example.passwordmanager.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.passwordmanager.data.EncryptedData
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptUtils {
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val ALIAS = "MyKeyAlias"

    fun encryptText(text: String): EncryptedData? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val encryptedText = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
            EncryptedData(encryptedText, iv)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decryptData(encryptedData: EncryptedData): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, encryptedData.iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            String(cipher.doFinal(encryptedData.encryptedText), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (!keyStore.containsAlias(ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
            keyStore.getKey(ALIAS, null) as SecretKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
