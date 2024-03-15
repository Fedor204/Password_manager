package com.example.passwordmanager.utils

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.passwordmanager.data.EncryptedData

class SharedPreferencesManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveMasterPassword(encryptedData: EncryptedData) {
        sharedPreferences.edit().putString("master_password", Base64.encodeToString(encryptedData.encryptedText, Base64.DEFAULT))
            .putString("iv", Base64.encodeToString(encryptedData.iv, Base64.DEFAULT)).apply()
    }

    fun getMasterPassword(): EncryptedData? {
        val encryptedText = sharedPreferences.getString("master_password", null)
        val iv = sharedPreferences.getString("iv", null)
        return if (encryptedText != null && iv != null) {
            EncryptedData(Base64.decode(encryptedText, Base64.DEFAULT), Base64.decode(iv, Base64.DEFAULT))
        } else {
            null
        }
    }

    fun hasMasterPassword(): Boolean {
        return sharedPreferences.contains("master_password") && sharedPreferences.contains("iv")
    }
}
