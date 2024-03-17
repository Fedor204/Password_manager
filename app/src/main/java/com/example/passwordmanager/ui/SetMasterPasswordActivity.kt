package com.example.passwordmanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.passwordmanager.R
import com.example.passwordmanager.data.AppDatabase
import com.example.passwordmanager.databinding.ActivitySetMasterPasswordBinding
import com.example.passwordmanager.utils.KeystoreManager

class SetMasterPasswordActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySetMasterPasswordBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        if (KeystoreManager(this).getMasterPassword() != null) {
            setupForPasswordCheck()
            checkBiometricSupport()
        } else {
            setupForNewPassword()
        }

        setupPasswordVisibilityToggle()

        binding.buttonFingerprintLogin.setOnClickListener {
            authenticateUser()
        }
    }

    private fun setupPasswordVisibilityToggle() {

        binding.passwordVisibilityMasterEnter.setOnClickListener {
            togglePasswordVisibility(binding.editTextMasterPassword, binding.passwordVisibilityMasterEnter)
        }

        binding.passwordVisibilityMasterConfirm.setOnClickListener {
            togglePasswordVisibility(binding.editTextConfirmMasterPassword, binding.passwordVisibilityMasterConfirm)
        }
    }

    private fun togglePasswordVisibility(editText: EditText, toggleButton: ImageButton) {
        if (editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleButton.setImageResource(R.drawable.ic_visibility)
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleButton.setImageResource(R.drawable.ic_visibility_off)
        }
        editText.setSelection(editText.text.length)
    }

    private fun setupForPasswordCheck() {
        binding.tvTitleWelcome.text = getString(R.string.welcome_second_password)
        binding.buttonSetMasterPassword.text = getString(R.string.unlock)
        binding.editTextConfirmMasterPassword.visibility = View.GONE
        binding.passwordVisibilityMasterConfirm.visibility = View.GONE

        binding.buttonSetMasterPassword.setOnClickListener {
            val enteredPassword = binding.editTextMasterPassword.text.toString()
            validateMasterPassword(enteredPassword)
        }

        binding.buttonFingerprintLogin.setOnClickListener {
            authenticateUser()
        }
    }


    private fun setupForNewPassword() {
        binding.tvTitleWelcome.text = getString(R.string.welcome_first_password)
        binding.buttonSetMasterPassword.text = getString(R.string.set_password)
        binding.editTextConfirmMasterPassword.visibility = View.VISIBLE
        binding.passwordVisibilityMasterConfirm.visibility = View.VISIBLE

        binding.buttonSetMasterPassword.setOnClickListener {
            val masterPassword = binding.editTextMasterPassword.text.toString()
            val confirmMasterPassword = binding.editTextConfirmMasterPassword.text.toString()

            if (masterPassword == confirmMasterPassword && masterPassword.isNotEmpty()) {
                KeystoreManager(this).saveMasterPassword(masterPassword)
                launchMainActivity()
            } else {
                Toast.makeText(this, "The passwords do not match or are empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateMasterPassword(enteredPassword: String) {
        val storedPassword = KeystoreManager(this).getMasterPassword()
        if (enteredPassword == storedPassword) {
            Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show()
            launchMainActivity()
        } else {
            Toast.makeText(this, "Incorrect Master Password", Toast.LENGTH_SHORT).show()
            binding.editTextMasterPassword.text.clear()
        }
    }

    private fun authenticateUser() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                launchMainActivity()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login using biometric")
            .setSubtitle("Place use your biometric to join")
            .setNegativeButtonText("Use Master Password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun checkBiometricSupport() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.buttonFingerprintLogin.visibility = View.VISIBLE
            }
            else -> {
                binding.buttonFingerprintLogin.visibility = View.GONE
            }
        }
    }

    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}