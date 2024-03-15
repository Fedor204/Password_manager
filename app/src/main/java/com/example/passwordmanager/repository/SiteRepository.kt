package com.example.passwordmanager.repository

import androidx.lifecycle.LiveData
import com.example.passwordmanager.data.Site
import com.example.passwordmanager.data.SiteDao

class SiteRepository(private val siteDao: SiteDao) {
    val allSites: LiveData<List<Site>> = siteDao.getAll()

    suspend fun insert(site: Site) {
        siteDao.insert(site)
    }

    suspend fun update(site: Site) {
        siteDao.update(site)
    }

    suspend fun delete(site: Site) {
        siteDao.delete(site)
    }

    fun getSiteById(id: Int): LiveData<Site> {
        return siteDao.getSiteById(id)
    }
}