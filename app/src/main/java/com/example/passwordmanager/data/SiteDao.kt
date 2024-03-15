package com.example.passwordmanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites")
    fun getAll(): LiveData<List<Site>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(site: Site)

    @Update
    suspend fun update(site: Site)

    @Delete
    suspend fun delete(site: Site)

    @Query("SELECT * FROM sites WHERE id = :id")
    fun getSiteById(id: Int): LiveData<Site>
}