package com.example.passwordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.data.AppDatabase
import com.example.passwordmanager.data.Site
import com.example.passwordmanager.repository.SiteRepository
import kotlinx.coroutines.launch

class SiteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SiteRepository

    val allSites: LiveData<List<Site>>

    init {
        val siteDao = AppDatabase.getDatabase(application).siteDao()
        repository = SiteRepository(siteDao)
        allSites = repository.allSites
    }

    fun insert(site: Site) = viewModelScope.launch {
        repository.insert(site)
    }

    fun getSiteById(id: Int): LiveData<Site> {
        return repository.getSiteById(id)
    }

    fun update(site: Site) = viewModelScope.launch {
        repository.update(site)
    }

    fun delete(site: Site) = viewModelScope.launch {
        repository.delete(site)
    }

}