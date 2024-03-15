package com.example.passwordmanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "sites")
data class Site(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "siteName") val siteName: String,
    @ColumnInfo(name = "url") private val _url: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "iv") val iv: String
) {
    val url: String
        get() = if (!_url.startsWith("http://") && !_url.startsWith("https://")) {
            "https://$_url"
        } else {
            _url
        }

    val iconUrl: String
        get() = "https://www.google.com/s2/favicons?sz=64&domain_url=$url"
}
