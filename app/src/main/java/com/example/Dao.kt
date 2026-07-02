package com.example

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Inspection Reports
    @Query("SELECT * FROM inspections ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<InspectionReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(report: InspectionReport): Long

    @Query("DELETE FROM inspections WHERE id = :id")
    suspend fun deleteInspectionById(id: Int)

    @Query("DELETE FROM inspections")
    suspend fun clearAllInspections()

    @Query("SELECT * FROM inspections WHERE isSynced = 0")
    suspend fun getUnsyncedInspections(): List<InspectionReport>

    @Query("UPDATE inspections SET isSynced = 1 WHERE id = :id")
    suspend fun markInspectionSynced(id: Int)

    // Settings
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettings?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)

    // Cached Students
    @Query("SELECT * FROM cached_students WHERE LOWER(idpps) = LOWER(:idpps) LIMIT 1")
    suspend fun getCachedStudent(idpps: String): CachedStudent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedStudent(student: CachedStudent)

    @Query("DELETE FROM cached_students")
    suspend fun clearCachedStudents()

    // Cached Pembimbing
    @Query("SELECT * FROM cached_pembimbing WHERE LOWER(nama) LIKE '%' || LOWER(:searchQuery) || '%' LIMIT 1")
    suspend fun getCachedPembimbing(searchQuery: String): CachedPembimbing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedPembimbing(pembimbing: CachedPembimbing)

    @Query("DELETE FROM cached_pembimbing")
    suspend fun clearCachedPembimbing()
}
