package com.example.passwordmanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.R
import com.example.passwordmanager.data.AppDatabase
import com.example.passwordmanager.viewmodel.SiteViewModel
import com.example.passwordmanager.data.Site
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var siteViewModel: SiteViewModel
    private lateinit var adapter: SitesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        siteViewModel = ViewModelProvider(this)[SiteViewModel::class.java]

        adapter = SitesAdapter(this, emptyList()) { site, action ->
            adapterOnClick(site, action)
        }
        binding.rvSites.adapter = adapter
        binding.rvSites.layoutManager = LinearLayoutManager(this)

        binding.progressBar.visibility = View.VISIBLE


        siteViewModel.allSites.observe(this) { sites ->
            sites?.let {
                adapter.setSites(it)
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, EditSiteActivity::class.java)
            startActivity(intent)
        }


    }


    private fun adapterOnClick(site: Site, action: String) {
        when (action) {
            "edit" -> {
                val intent = Intent(this, EditSiteActivity::class.java)
                intent.putExtra("site_id", site.id)
                startActivity(intent)
            }

            "delete" -> {
                siteViewModel.delete(site)
            }
        }
    }
}