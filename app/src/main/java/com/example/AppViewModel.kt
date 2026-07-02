package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    // Tab Selection
    private val _currentTab = MutableStateFlow("inspeksi")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Theme Mode: "light", "dark", or "system"
    private val sharedPrefs = application.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    private val _themeMode = MutableStateFlow(sharedPrefs.getString("theme_mode", "system") ?: "system")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        sharedPrefs.edit().putString("theme_mode", mode).apply()
    }

    // Clock and Calendar States (WIS is local time + 31 minutes)
    private val _wisClock = MutableStateFlow("--:--:--")
    val wisClock: StateFlow<String> = _wisClock.asStateFlow()

    private val _wisDate = MutableStateFlow("Memuat Falak...")
    val wisDate: StateFlow<String> = _wisDate.asStateFlow()

    private val _nextSholatNama = MutableStateFlow("...")
    val nextSholatNama: StateFlow<String> = _nextSholatNama.asStateFlow()

    private val _nextSholatTimer = MutableStateFlow("--:--:--")
    val nextSholatTimer: StateFlow<String> = _nextSholatTimer.asStateFlow()

    // Configuration / WebApp URL
    val appSettings: StateFlow<AppSettings?> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Form states
    val selectedDaerah = MutableStateFlow("")
    val isPosLocked = MutableStateFlow(false)
    val lockedDaerah = MutableStateFlow("")

    val selectedKelas = MutableStateFlow("")
    val telatMenit = MutableStateFlow(0)
    val guruAktif = MutableStateFlow(false)
    val muridAktif = MutableStateFlow(false)
    val kekondusifan = MutableStateFlow("") // "Sangat Baik", "Baik", "Cukup", "Kurang"
    val kerapian = MutableStateFlow("") // "Sangat Baik", "Baik", "Cukup", "Kurang"
    val catatan = MutableStateFlow("")

    // Statuses
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<Pair<String, Boolean>?>(null) // Pair(Message, IsSuccess)
    val toastMessage: StateFlow<Pair<String, Boolean>?> = _toastMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Inspection reports (Antrean)
    val inspections: StateFlow<List<InspectionReport>> = repository.allInspections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search Students states
    val studentSearchQuery = MutableStateFlow("")
    private val _studentResult = MutableStateFlow<StudentData?>(null)
    val studentResult: StateFlow<StudentData?> = _studentResult.asStateFlow()

    // Search Pembimbing states
    val pembimbingSearchQuery = MutableStateFlow("")
    private val _pembimbingResult = MutableStateFlow<PembimbingData?>(null)
    val pembimbingResult: StateFlow<PembimbingData?> = _pembimbingResult.asStateFlow()

    init {
        // Start Clock Ticker
        startClockTicker()
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
        clearSearchStates()
    }

    private fun clearSearchStates() {
        _errorMessage.value = null
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                updateFalak()
                delay(1000)
            }
        }
    }

    private fun updateFalak() {
        val now = Calendar.getInstance()
        
        // WIS (Waktu Istiwak) = Current time + 31 minutes
        val wisCalendar = now.clone() as Calendar
        wisCalendar.add(Calendar.MINUTE, 31)

        val hourStr = String.format("%02d", wisCalendar.get(Calendar.HOUR_OF_DAY))
        val minStr = String.format("%02d", wisCalendar.get(Calendar.MINUTE))
        val secStr = String.format("%02d", wisCalendar.get(Calendar.SECOND))
        _wisClock.value = "$hourStr:$minStr:$secStr"

        // check if it's malam (after 17:25 solar/standard time)
        val standardHour = now.get(Calendar.HOUR_OF_DAY)
        val standardMin = now.get(Calendar.MINUTE)
        val isMalam = (standardHour * 60 + standardMin) >= (17 * 60 + 25)

        val hariArr = arrayOf("Ahad", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        var dIdx = (now.get(Calendar.DAY_OF_WEEK) - 1) % 7 // SUNDAY is 1 -> index 0 (Ahad)
        if (isMalam) {
            dIdx = (dIdx + 1) % 7
        }
        val prefix = if (isMalam) "Malam " else "Hari "
        val dayName = prefix + hariArr[dIdx]

        // Get Hijri Calendar date
        val hijriStr = getHijriDateString(isMalam)
        _wisDate.value = "$dayName, $hijriStr"

        // Calculate next sholat countdown
        val sholatList = listOf(
            SholatTime("Imsak", 4, 5),
            SholatTime("Subuh", 4, 15),
            SholatTime("Terbit", 5, 35),
            SholatTime("Dhuha", 6, 0),
            SholatTime("Dzuhur", 12, 0),
            SholatTime("Asar", 15, 15),
            SholatTime("Magrib", 18, 0),
            SholatTime("Isya'", 19, 15)
        )

        var nextS: SholatTime? = null
        var minDiff = Long.MAX_VALUE

        for (s in sholatList) {
            val target = wisCalendar.clone() as Calendar
            target.set(Calendar.HOUR_OF_DAY, s.jam)
            target.set(Calendar.MINUTE, s.mnt)
            target.set(Calendar.SECOND, 0)
            target.set(Calendar.MILLISECOND, 0)

            if (target.before(wisCalendar) || target.equals(wisCalendar)) {
                target.add(Calendar.DAY_OF_MONTH, 1)
            }

            val diff = target.timeInMillis - wisCalendar.timeInMillis
            if (diff < minDiff) {
                minDiff = diff
                nextS = s
            }
        }

        if (nextS != null) {
            val totalSecs = minDiff / 1000
            val h = totalSecs / 3600
            val m = (totalSecs % 3600) / 60
            val s = totalSecs % 60
            _nextSholatNama.value = nextS.nama
            _nextSholatTimer.value = String.format("-%02d:%02d:%02d", h, m, s)
        }
    }

    private fun getHijriDateString(isMalam: Boolean): String {
        try {
            val icClass = Class.forName("android.icu.util.IslamicCalendar")
            val icInstance = icClass.getDeclaredConstructor().newInstance()
            
            val addMethod = icClass.getMethod("add", Int::class.java, Int::class.java)
            val getMethod = icClass.getMethod("get", Int::class.java)
            
            // IslamicCalendar.DAY_OF_MONTH (field value is 5)
            // IslamicCalendar.YEAR (1), MONTH (2)
            if (isMalam) {
                addMethod.invoke(icInstance, 5, 1)
            }
            
            val year = getMethod.invoke(icInstance, 1) as Int
            val month = getMethod.invoke(icInstance, 2) as Int
            val day = getMethod.invoke(icInstance, 5) as Int

            val months = arrayOf(
                "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
                "Jumadil Awwal", "Jumadil Akhir", "Rajab", "Sya'ban",
                "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
            )
            val monthName = if (month in 0..11) months[month] else ""
            return "$day $monthName $year H"
        } catch (e: Exception) {
            // Fallback: simple text
            return "17 Muharram 1448 H"
        }
    }

    private data class SholatTime(val nama: String, val jam: Int, val mnt: Int)

    // Form action
    fun saveSettingsUrl(url: String) {
        viewModelScope.launch {
            repository.saveAppSettings(url)
            showToast("Web App URL berhasil disimpan!", true)
        }
    }

    fun submitInspection() {
        val daerah = if (isPosLocked.value) lockedDaerah.value else selectedDaerah.value
        val kelas = selectedKelas.value
        val telat = telatMenit.value
        val cond = kekondusifan.value
        val rap = kerapian.value

        if (daerah.isBlank()) {
            showToast("Silakan pilih Pos/Daerah!", false)
            return
        }
        if (kelas.isBlank()) {
            showToast("Silakan pilih Ruang Kelas!", false)
            return
        }
        if (cond.isBlank() || rap.isBlank()) {
            showToast("Pilih parameter Kekondusifan & Kerapian!", false)
            return
        }

        // Lock POS if not locked
        if (!isPosLocked.value) {
            isPosLocked.value = true
            lockedDaerah.value = daerah
        }

        val report = InspectionReport(
            daerah = daerah,
            kelas = kelas,
            telatMenit = telat,
            statusGuru = if (guruAktif.value) "Aktif" else "Tidak Aktif",
            statusMurid = if (muridAktif.value) "Aktif" else "Tidak Aktif",
            kekondusifan = cond,
            kerapian = rap,
            catatan = catatan.value
        )

        _isLoading.value = true
        viewModelScope.launch {
            val savedReport = repository.insertInspection(report)
            _isLoading.value = false
            
            if (savedReport.isSynced) {
                showToast("Terekam online di Google Sheet!", true)
            } else {
                showToast("Terekam offline di antrean lokal!", true)
            }

            // Reset part of form
            selectedKelas.value = ""
            telatMenit.value = 0
            guruAktif.value = false
            muridAktif.value = false
            kekondusifan.value = ""
            kerapian.value = ""
            catatan.value = ""
        }
    }

    fun unlockPos() {
        isPosLocked.value = false
        lockedDaerah.value = ""
        selectedDaerah.value = ""
        selectedKelas.value = ""
    }

    fun deleteInspection(id: Int) {
        viewModelScope.launch {
            repository.deleteInspection(id)
            showToast("Rekaman dihapus.", true)
        }
    }

    fun clearAllInspections() {
        viewModelScope.launch {
            repository.clearAllInspections()
            unlockPos()
            showToast("Semua antrean dikosongkan.", true)
        }
    }

    fun syncDataWithCloud() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val result = repository.syncUnsyncedInspections()
            _isLoading.value = false
            when (result) {
                is SyncResult.Success -> showToast(result.message, true)
                is SyncResult.Failure -> {
                    _errorMessage.value = result.message
                    showToast(result.message, false)
                }
            }
        }
    }

    fun checkCloudConnection() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            val result = repository.testConnection()
            _isLoading.value = false
            when (result) {
                is SyncResult.Success -> showToast(result.message, true)
                is SyncResult.Failure -> {
                    _errorMessage.value = result.message
                    showToast(result.message, false)
                }
            }
        }
    }

    // Search Student
    fun searchStudent(id: String) {
        if (id.isBlank()) return
        _isLoading.value = true
        _errorMessage.value = null
        _studentResult.value = null
        viewModelScope.launch {
            val res = repository.searchStudent(id)
            _isLoading.value = false
            when (res) {
                is SearchResult.Success -> {
                    _studentResult.value = res.data
                    if (res.isFromCache) {
                        showToast("Menampilkan data cache offline", true)
                    } else {
                        showToast("Data ditarik dari Cloud!", true)
                    }
                }
                is SearchResult.Failure -> {
                    _errorMessage.value = res.message
                    showToast(res.message, false)
                }
            }
        }
    }

    // Search Pembimbing
    fun searchPembimbing(nama: String) {
        if (nama.isBlank()) return
        _isLoading.value = true
        _errorMessage.value = null
        _pembimbingResult.value = null
        viewModelScope.launch {
            val res = repository.searchPembimbing(nama)
            _isLoading.value = false
            when (res) {
                is SearchResult.Success -> {
                    _pembimbingResult.value = res.data
                    if (res.isFromCache) {
                        showToast("Menampilkan data cache offline", true)
                    } else {
                        showToast("Data ditarik dari Cloud!", true)
                    }
                }
                is SearchResult.Failure -> {
                    _errorMessage.value = res.message
                    showToast(res.message, false)
                }
            }
        }
    }

    fun selectPembimbingAndSearch(nama: String) {
        if (nama.isBlank() || nama == "-") return
        pembimbingSearchQuery.value = nama
        setTab("pembimbing")
        searchPembimbing(nama)
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _studentResult.value = null
            _pembimbingResult.value = null
            showToast("Cache lokal santri & pembimbing berhasil dihapus!", true)
        }
    }

    private fun showToast(msg: String, isSuccess: Boolean) {
        _toastMessage.value = Pair(msg, isSuccess)
        viewModelScope.launch {
            delay(3000)
            if (_toastMessage.value?.first == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
