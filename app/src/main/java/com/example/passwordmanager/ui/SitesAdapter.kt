package com.example.passwordmanager.ui

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordmanager.utils.EncryptUtils
import com.example.passwordmanager.data.EncryptedData
import com.example.passwordmanager.R
import com.example.passwordmanager.data.Site
import com.example.passwordmanager.databinding.SiteItemBinding


class SitesAdapter(private val context: Context,
                   private var sites: List<Site>,
                   private val onItemClicked: (Site, String) -> Unit
) : RecyclerView.Adapter<SitesAdapter.SiteViewHolder>(){

    inner class SiteViewHolder(val binding: SiteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val binding = SiteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SiteViewHolder(binding)
    }



    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        with(holder) {
            val site = sites[position]
            with(binding) {

                var isPasswordVisible = false

                siteName.text = site.siteName
                siteUrl.text = site.url.removePrefix("https://").removePrefix("http://")
                val decryptUtils = EncryptUtils()

                sitePassword.text = context.getString(R.string.password_placeholder)

                siteLogin.text = "${site.username}"

                togglePasswordVisibility.setOnClickListener {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        val encryptedData = EncryptedData(
                            Base64.decode(site.password, Base64.DEFAULT),
                            Base64.decode(site.iv, Base64.DEFAULT)
                        )
                        val decryptedPassword = decryptUtils.decryptData(encryptedData)
                        sitePassword.text = "$decryptedPassword"
                        togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
                    } else {
                        sitePassword.text = context.getString(R.string.password_placeholder) // Скрываем пароль
                        togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
                    }
                }

                Glide.with(context)
                    .load(site.iconUrl)
                    .error(R.drawable.ic_website)
                    .into(siteIcon)

                siteCardView.setOnClickListener {
                        if (credentialsCardView.visibility == View.VISIBLE) {
                            credentialsCardView.visibility = View.GONE
                            arrowIndicator.animate().rotation(0f).setDuration(300).start()
                        } else {
                            credentialsCardView.visibility = View.VISIBLE
                            arrowIndicator.animate().rotation(90f).setDuration(300).start()
                        }
                }

                menuButton.setOnClickListener { view ->
                    showPopupMenu(view, site)
                }
            }
        }
    }

    override fun getItemCount() = sites.size

    fun setSites(sites: List<Site>) {
        this.sites = sites
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, site: Site) {
        PopupMenu(context, view).apply {
            menuInflater.inflate(R.menu.site_item_menu, menu)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onItemClicked(site, "edit")
                        true
                    }
                    R.id.action_delete -> {
                        onItemClicked(site, "delete")
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }
}