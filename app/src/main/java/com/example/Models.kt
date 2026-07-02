package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspections")
data class InspectionReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val daerah: String,
    val kelas: String,
    val telatMenit: Int,
    val statusGuru: String, // "Aktif" or "Tidak Aktif"
    val statusMurid: String, // "Aktif" or "Tidak Aktif"
    val kekondusifan: String, // "Sangat Baik", "Baik", "Cukup", "Kurang"
    val kerapian: String, // "Sangat Baik", "Baik", "Cukup", "Kurang"
    val catatan: String,
    val isSynced: Boolean = false
)

@Entity(tableName = "settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val webAppUrl: String = "https://script.google.com/macros/s/AKfycbzZ1c7xHXel-7GKe0T9cjNqyhtvTepFpGnjCXecJZY6vDOEBwgkgrha8m1KDTjBY_J98Q/exec"
)

@Entity(tableName = "cached_students")
data class CachedStudent(
    @PrimaryKey val idpps: String,
    val nama: String,
    val domisili: String,
    val kelas: String,
    val pembimbing: String,
    val ruangPagi: String,
    val ruangMalam: String,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_pembimbing")
data class CachedPembimbing(
    @PrimaryKey val nama: String,
    val idpps: String = "",
    val guru: String = "",
    val alamat: String = "",
    val ruangPagi: String,
    val ruangMalam: String,
    val cachedAt: Long = System.currentTimeMillis()
)
