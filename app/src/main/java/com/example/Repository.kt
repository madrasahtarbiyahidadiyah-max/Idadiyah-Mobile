package com.example

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AppRepository(
    private val dao: AppDao,
    private val api: ApiService
) {
    // Settings
    private val defaultUrl = "https://script.google.com/macros/s/AKfycbzZ1c7xHXel-7GKe0T9cjNqyhtvTepFpGnjCXecJZY6vDOEBwgkgrha8m1KDTjBY_J98Q/exec"

    val settingsFlow: Flow<AppSettings?> = dao.getSettingsFlow().map { settings ->
        val oldUrl = "https://script.google.com/macros/s/AKfycbxfxqkyTMC2_GzwFr-M_hAiTo--9pizp609iYcP6dKwHy8-4JPpXDilsru0Dkaz-XSltw/exec"
        if (settings == null || settings.webAppUrl.isBlank() || settings.webAppUrl == oldUrl) {
            AppSettings(webAppUrl = defaultUrl)
        } else {
            settings
        }
    }

    suspend fun getAppSettings(): AppSettings? {
        val settings = dao.getSettings()
        val oldUrl = "https://script.google.com/macros/s/AKfycbxfxqkyTMC2_GzwFr-M_hAiTo--9pizp609iYcP6dKwHy8-4JPpXDilsru0Dkaz-XSltw/exec"
        return if (settings == null || settings.webAppUrl.isBlank() || settings.webAppUrl == oldUrl) {
            val updated = AppSettings(webAppUrl = defaultUrl)
            dao.saveSettings(updated)
            updated
        } else {
            settings
        }
    }

    suspend fun saveAppSettings(url: String) {
        dao.saveSettings(AppSettings(webAppUrl = url))
    }

    // Inspections
    val allInspections: Flow<List<InspectionReport>> = dao.getAllInspections()

    suspend fun insertInspection(report: InspectionReport): InspectionReport {
        // Save locally first
        val id = dao.insertInspection(report).toInt()
        val savedReport = report.copy(id = id)

        // Try to sync with Google Sheet if URL is configured
        val settings = getAppSettings()
        if (settings != null && settings.webAppUrl.isNotBlank()) {
            try {
                val response = api.simpanInspeksi(
                    url = settings.webAppUrl,
                    daerah = report.daerah,
                    kelas = report.kelas,
                    telatMenit = report.telatMenit,
                    guruAktif = report.statusGuru == "Aktif",
                    muridAktif = report.statusMurid == "Aktif",
                    kekondusifan = report.kekondusifan,
                    kerapian = report.kerapian,
                    catatan = report.catatan
                )
                if (response.sukses) {
                    dao.markInspectionSynced(id)
                    return savedReport.copy(isSynced = true)
                }
            } catch (e: Exception) {
                // Ignore network errors; let it remain unsynced for later manual or automatic sync
                e.printStackTrace()
            }
        }
        return savedReport
    }

    suspend fun deleteInspection(id: Int) {
        dao.deleteInspectionById(id)
    }

    suspend fun clearAllInspections() {
        dao.clearAllInspections()
    }

    suspend fun syncUnsyncedInspections(): SyncResult {
        val settings = getAppSettings()
        if (settings == null || settings.webAppUrl.isBlank()) {
            return SyncResult.Failure("Google Sheet Web App URL belum dikonfigurasi di Pengaturan!")
        }

        val unsynced = dao.getUnsyncedInspections()
        if (unsynced.isEmpty()) {
            return SyncResult.Success("Semua data sudah disinkronkan!")
        }

        var successCount = 0
        var failCount = 0

        for (report in unsynced) {
            try {
                val response = api.simpanInspeksi(
                    url = settings.webAppUrl,
                    daerah = report.daerah,
                    kelas = report.kelas,
                    telatMenit = report.telatMenit,
                    guruAktif = report.statusGuru == "Aktif",
                    muridAktif = report.statusMurid == "Aktif",
                    kekondusifan = report.kekondusifan,
                    kerapian = report.kerapian,
                    catatan = report.catatan
                )
                if (response.sukses) {
                    dao.markInspectionSynced(report.id)
                    successCount++
                } else {
                    failCount++
                }
            } catch (e: Exception) {
                failCount++
                e.printStackTrace()
            }
        }

        return if (failCount == 0) {
            SyncResult.Success("Berhasil menyinkronkan $successCount data!")
        } else {
            SyncResult.Success("Sinkronisasi sebagian: $successCount berhasil, $failCount gagal.")
        }
    }

    suspend fun testConnection(): SyncResult {
        val settings = getAppSettings()
        if (settings == null || settings.webAppUrl.isBlank()) {
            return SyncResult.Failure("URL Web App belum dikonfigurasi!")
        }

        return try {
            val response = api.sinkronDatabase(settings.webAppUrl)
            if (!response.error) {
                SyncResult.Success(response.pesan)
            } else {
                SyncResult.Failure(response.pesan)
            }
        } catch (e: Exception) {
            SyncResult.Failure("Gagal menghubungi cloud: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    // Student Search
    suspend fun searchStudent(id: String): SearchResult<StudentData> {
        val cleanId = id.trim()
        val settings = getAppSettings()

        // 1. Try to query API first if URL is configured
        if (settings != null && settings.webAppUrl.isNotBlank()) {
            try {
                val response = api.cariSantri(settings.webAppUrl, cleanId)
                if (!response.error && response.data != null) {
                    val sData = response.data
                    // Cache results locally
                    dao.insertCachedStudent(
                        CachedStudent(
                            idpps = sData.idpps,
                            nama = sData.nama,
                            domisili = sData.domisili,
                            kelas = sData.kelas,
                            pembimbing = sData.pembimbing,
                            ruangPagi = sData.ruang_pagi,
                            ruangMalam = sData.ruang_malam
                        )
                    )
                    return SearchResult.Success(sData)
                } else {
                    return SearchResult.Failure(response.pesan)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Network failed, fall back to Room cache
            }
        }

        // 2. Fallback: Search local cache
        val cached = dao.getCachedStudent(cleanId)
        return if (cached != null) {
            SearchResult.Success(
                StudentData(
                    idpps = cached.idpps,
                    nama = cached.nama,
                    domisili = cached.domisili,
                    kelas = cached.kelas,
                    pembimbing = cached.pembimbing,
                    ruang_pagi = cached.ruangPagi,
                    ruang_malam = cached.ruangMalam
                ),
                isFromCache = true
            )
        } else {
            SearchResult.Failure(
                if (settings?.webAppUrl.isNullOrBlank()) {
                    "Offline: IDPPS tidak ada di cache lokal. Hubungkan Web App URL di Pengaturan untuk mencari online."
                } else {
                    "Koneksi gagal & IDPPS tidak ditemukan di cache lokal."
                }
            )
        }
    }

    // Pembimbing Search
    suspend fun searchPembimbing(nama: String): SearchResult<PembimbingData> {
        val cleanNama = nama.trim()
        val settings = getAppSettings()

        // 1. Try to query API first if URL is configured
        if (settings != null && settings.webAppUrl.isNotBlank()) {
            try {
                val response = api.cariPembimbing(settings.webAppUrl, cleanNama)
                if (!response.error && response.data != null) {
                    val pData = response.data
                    // Cache results locally
                    dao.insertCachedPembimbing(
                        CachedPembimbing(
                            nama = pData.nama,
                            idpps = pData.idpps,
                            guru = pData.guru,
                            alamat = pData.alamat,
                            ruangPagi = pData.ruang_pagi,
                            ruangMalam = pData.ruang_malam
                        )
                    )
                    return SearchResult.Success(pData)
                } else {
                    return SearchResult.Failure(response.pesan)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Network failed, fall back to Room cache
            }
        }

        // 2. Fallback: Search local cache
        val cached = dao.getCachedPembimbing(cleanNama)
        return if (cached != null) {
            SearchResult.Success(
                PembimbingData(
                    nama = cached.nama,
                    idpps = cached.idpps,
                    guru = cached.guru,
                    alamat = cached.alamat,
                    ruang_pagi = cached.ruangPagi,
                    ruang_malam = cached.ruangMalam
                ),
                isFromCache = true
            )
        } else {
            SearchResult.Failure(
                if (settings?.webAppUrl.isNullOrBlank()) {
                    "Offline: Nama tidak ada di cache lokal. Hubungkan Web App URL di Pengaturan untuk mencari online."
                } else {
                    "Koneksi gagal & Nama tidak ditemukan di cache lokal."
                }
            )
        }
    }

    suspend fun clearCache() {
        dao.clearCachedStudents()
        dao.clearCachedPembimbing()
    }
}

sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class Failure(val message: String) : SyncResult()
}

sealed class SearchResult<out T> {
    data class Success<out T>(val data: T, val isFromCache: Boolean = false) : SearchResult<T>()
    data class Failure(val message: String) : SearchResult<Nothing>()
}
