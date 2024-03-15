package com.example.passwordmanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.utils.EncryptUtils
import com.example.passwordmanager.data.EncryptedData
import com.example.passwordmanager.R
import com.example.passwordmanager.viewmodel.SiteViewModel
import com.example.passwordmanager.data.Site
import com.example.passwordmanager.databinding.ActivityEditSiteBinding


class EditSiteActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditSiteBinding.inflate(layoutInflater)
    }

    private var currentSiteId: Int = 0

    private lateinit var siteViewModel: SiteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        siteViewModel = ViewModelProvider(this)[SiteViewModel::class.java]

        var isPasswordVisible = false

        binding.togglePasswordVisibilityEdit.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.tilPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibilityEdit.setImageResource(R.drawable.ic_visibility_off)
            } else {
                binding.tilPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibilityEdit.setImageResource(R.drawable.ic_visibility)
            }
            binding.tilPassword.text?.let { text -> binding.tilPassword.setSelection(text.length) }
        }

        currentSiteId = intent.getIntExtra("site_id", 0)

        if (currentSiteId != 0) {
            siteViewModel.getSiteById(currentSiteId).observe(this) { site ->
                site?.let {
                    binding.tilSiteName.setText(site.siteName)
                    binding.tilUrl.setText(site.url.replaceFirst(Regex("https?://"), ""))
                    binding.tilUserName.setText(site.username)

                    val decryptUtils = EncryptUtils()
                    val decryptedData = decryptUtils.decryptData(EncryptedData(Base64.decode(it.password, Base64.DEFAULT), Base64.decode(it.iv, Base64.DEFAULT)))
                    binding.tilPassword.setText(decryptedData)
                }
            }
        }

        binding.tilUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    val siteName = s.toString()
                        .removePrefix("https://")
                        .removePrefix("http://")
                        .split('/')[0]
                        .split('.')[0]
                    binding.tilSiteName.setText(siteName)
                }
            }
        })

        binding.btnSave.setOnClickListener {
            saveSite()
        }
    }

    private fun saveSite() {
        val url = binding.tilUrl.text.toString()
        val siteName = binding.tilSiteName.text.toString()
        val username = binding.tilUserName.text.toString()
        val password = binding.tilPassword.text.toString()

        if (validateInput(url, siteName, username, password)) {
            val encryptUtils = EncryptUtils()
            val encryptedData = encryptUtils.encryptText(password)

            val site = Site(
                id = currentSiteId,
                siteName = siteName,
                _url = url,
                username = username,
                password = Base64.encodeToString(encryptedData.encryptedText, Base64.DEFAULT),
                iv = Base64.encodeToString(encryptedData.iv, Base64.DEFAULT)
            )
            if (currentSiteId == 0) {
                siteViewModel.insert(site)
            } else {
                siteViewModel.update(site)
            }
            finish()
        }
    }

        private fun validateInput(url: String, siteName: String, username: String, password: String): Boolean {
            if (url.isEmpty()) {
                binding.tilUrl.error = "URL cannot be empty"
                return false
            }
            if (siteName.isEmpty()) {
                binding.tilSiteName.error = "Sitename cannot be empty"
                return false
            }
            if (username.isEmpty()) {
                binding.tilUserName.error = "Username cannot be empty"
                return false
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Password cannot be empty"
                return false
            }
            return true
        }
    }