package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KampusFixDatabase
import com.example.data.local.ReportRepository
import com.example.data.model.CampusData
import com.example.data.model.DemoAccounts
import com.example.data.model.FacilityCategory
import com.example.data.model.FacilityReport
import com.example.data.model.NotificationItem
import com.example.data.model.ReportStatus
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppThemeMode(val label: String) {
    LIGHT("Mode Terang"),
    DARK("Mode Gelap"),
    SYSTEM("Ikuti Sistem")
}

data class NotificationPreferences(
    val statusUpdates: Boolean = true,
    val technicianAssigned: Boolean = true,
    val reportCompleted: Boolean = true,
    val campusAnnouncements: Boolean = true,
    val soundAndVibrate: Boolean = true
)

class KampusFixViewModel(application: Application) : AndroidViewModel(application) {

    private val database = KampusFixDatabase.getDatabase(application, viewModelScope)
    private val repository = ReportRepository(database.reportDao(), database.notificationDao())

    // Authentication State: null = not logged in (shows Splash -> Login)
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    // Saved Account on Login Screen (dapat dihapus dengan tombol X)
    private val _savedAccount = MutableStateFlow<UserAccount?>(DemoAccounts.MAHASISWA)
    val savedAccount: StateFlow<UserAccount?> = _savedAccount.asStateFlow()

    fun removeSavedAccount() {
        _savedAccount.value = null
    }

    // App Theme / Dark Mode State
    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    // Notification Preferences
    private val _notifPrefs = MutableStateFlow(NotificationPreferences())
    val notifPrefs: StateFlow<NotificationPreferences> = _notifPrefs.asStateFlow()

    fun updateNotifPrefs(prefs: NotificationPreferences) {
        _notifPrefs.value = prefs
    }

    // Edit Profil Mahasiswa
    fun updateProfile(name: String, email: String, major: String) {
        _currentUser.value?.let { current ->
            val updated = current.copy(
                name = name,
                email = email,
                major = major
            )
            _currentUser.value = updated
            if (_savedAccount.value?.role == updated.role) {
                _savedAccount.value = updated
            }
        }
    }

    fun login(usernameInput: String, passwordInput: String): Boolean {
        val trimmedUser = usernameInput.trim()
        val trimmedPass = passwordInput.trim()

        // Cek Akun Mahasiswa: G1Z030099 dengan Soneto123
        val isMahasiswaUser = trimmedUser.equals("G1Z030099", ignoreCase = true) ||
                trimmedUser.equals("G1Z03099", ignoreCase = true) ||
                trimmedUser.equals("sonetto@kampus.ac.id", ignoreCase = true) ||
                trimmedUser.equals("sonetto", ignoreCase = true)

        val isMahasiswaPass = trimmedPass == "Soneto123" || trimmedPass.equals("soneto123", ignoreCase = true) ||
                trimmedPass == "Sonetto123" || trimmedPass.equals("sonetto123", ignoreCase = true)

        if (isMahasiswaUser && isMahasiswaPass) {
            _currentUser.value = DemoAccounts.MAHASISWA
            _isAdminMode.value = false
            return true
        }

        // Cek Akun Admin: adminr dengan Admin123
        val isAdminUser = trimmedUser.equals("adminr", ignoreCase = true) ||
                trimmedUser.equals("admin_r", ignoreCase = true) ||
                trimmedUser.equals("admin.r@kampus.ac.id", ignoreCase = true) ||
                trimmedUser.equals("admin", ignoreCase = true)

        val isAdminPass = trimmedPass == "Admin123" || trimmedPass.equals("admin123", ignoreCase = true)

        if (isAdminUser && isAdminPass) {
            _currentUser.value = DemoAccounts.ADMIN
            _isAdminMode.value = true
            return true
        }

        // Fallback matching DemoAccounts
        val match = DemoAccounts.ALL.find { account ->
            (account.username.equals(trimmedUser, ignoreCase = true) ||
             account.email.equals(trimmedUser, ignoreCase = true) ||
             (account.nim != null && account.nim.equals(trimmedUser, ignoreCase = true))) &&
            (account.password.equals(trimmedPass, ignoreCase = true))
        }

        return if (match != null) {
            _currentUser.value = match
            _isAdminMode.value = (match.role == UserRole.ADMIN)
            true
        } else {
            false
        }
    }

    fun loginAs(account: UserAccount) {
        _currentUser.value = account
        _isAdminMode.value = (account.role == UserRole.ADMIN)
    }

    fun logout() {
        _currentUser.value = null
        _isAdminMode.value = false
    }

    val allReports: StateFlow<List<FacilityReport>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = allNotifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Admin mode switcher
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun setAdminMode(isAdmin: Boolean) {
        _isAdminMode.value = isAdmin
    }

    // Search and filter for "Laporan Saya"
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<ReportStatus?>(null) // null = Semua
    val statusFilter: StateFlow<ReportStatus?> = _statusFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: ReportStatus?) {
        _statusFilter.value = status
    }

    val filteredReports: StateFlow<List<FacilityReport>> = combine(
        allReports,
        searchQuery,
        statusFilter
    ) { reports, query, filter ->
        reports.filter { report ->
            val matchesQuery = query.isBlank() ||
                    report.title.contains(query, ignoreCase = true) ||
                    report.building.contains(query, ignoreCase = true) ||
                    report.room.contains(query, ignoreCase = true) ||
                    report.category.contains(query, ignoreCase = true) ||
                    report.id.contains(query, ignoreCase = true)

            val matchesFilter = filter == null || report.status == filter

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics for Home and Admin
    val waitingCount: StateFlow<Int> = allReports
        .map { list -> list.count { it.status == ReportStatus.MENUNGGU } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inProgressCount: StateFlow<Int> = allReports
        .map { list -> list.count { it.status == ReportStatus.DIPROSES || it.status == ReportStatus.DIVERIFIKASI } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedCount: StateFlow<Int> = allReports
        .map { list -> list.count { it.status == ReportStatus.SELESAI } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Create Report Draft State (Default unselected/empty per user request)
    private val _draftCategory = MutableStateFlow<FacilityCategory?>(null)
    val draftCategory = _draftCategory.asStateFlow()

    private val _draftBuilding = MutableStateFlow("")
    val draftBuilding = _draftBuilding.asStateFlow()

    private val _draftRoom = MutableStateFlow("")
    val draftRoom = _draftRoom.asStateFlow()

    private val _draftPhotoUri = MutableStateFlow<String?>(null)
    val draftPhotoUri = _draftPhotoUri.asStateFlow()

    private val _draftTitle = MutableStateFlow("")
    val draftTitle = _draftTitle.asStateFlow()

    private val _draftDescription = MutableStateFlow("")
    val draftDescription = _draftDescription.asStateFlow()

    private val _draftPriority = MutableStateFlow("Tinggi")
    val draftPriority = _draftPriority.asStateFlow()

    private val _lastSubmittedReport = MutableStateFlow<FacilityReport?>(null)
    val lastSubmittedReport = _lastSubmittedReport.asStateFlow()

    fun updateDraftCategory(category: FacilityCategory) {
        _draftCategory.value = category
    }

    fun updateDraftBuilding(building: String) {
        _draftBuilding.value = building
        _draftRoom.value = ""
    }

    fun updateDraftRoom(room: String) {
        _draftRoom.value = room
    }

    fun updateDraftPhoto(uri: String?) {
        _draftPhotoUri.value = uri
    }

    fun updateDraftTitle(title: String) {
        _draftTitle.value = title
    }

    fun updateDraftDescription(desc: String) {
        _draftDescription.value = desc
    }

    fun updateDraftPriority(priority: String) {
        _draftPriority.value = priority
    }

    fun resetDraft() {
        _draftCategory.value = null
        _draftBuilding.value = ""
        _draftRoom.value = ""
        _draftPhotoUri.value = null
        _draftTitle.value = ""
        _draftDescription.value = ""
        _draftPriority.value = "Tinggi"
    }

    fun submitReport(onSuccess: (FacilityReport) -> Unit) {
        viewModelScope.launch {
            val title = _draftTitle.value.ifBlank { "${_draftCategory.value?.title ?: "Fasilitas"} Bermasalah" }
            val cat = _draftCategory.value?.title ?: "Fasilitas lainnya"
            val bldg = _draftBuilding.value
            val rm = _draftRoom.value
            val photo = _draftPhotoUri.value
            val desc = _draftDescription.value.ifBlank { "Tidak ada deskripsi rinci." }
            val priority = _draftPriority.value

            val user = _currentUser.value
            val reporterName = user?.name ?: "Sonetto"
            val reporterNim = user?.nim ?: "G1Z030099"
            val reporterMajor = user?.major ?: "Teknik Rekayasa Mantra"

            val created = repository.createReport(
                title = title,
                category = cat,
                building = bldg,
                room = rm,
                photoUri = photo,
                description = desc,
                priority = priority,
                reporterName = reporterName,
                reporterNim = reporterNim,
                reporterMajor = reporterMajor
            )

            _lastSubmittedReport.value = created
            onSuccess(created)
        }
    }

    fun updateReportStatus(reportId: String, newStatus: ReportStatus, adminNote: String?, assignedOfficer: String? = null) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, newStatus, adminNote, assignedOfficer)
        }
    }

    // Admin Specific Workflow Actions (PDF pages 15-17)
    fun verifyReport(reportId: String) {
        viewModelScope.launch {
            repository.updateReportStatus(
                reportId = reportId,
                newStatus = ReportStatus.DIVERIFIKASI,
                adminNote = "Laporan telah diverifikasi oleh tim Sarpras kampus."
            )
        }
    }

    fun assignAndStartRepair(reportId: String, officer: String, adminNote: String?) {
        viewModelScope.launch {
            val note = adminNote?.ifBlank { null } ?: "Teknisi $officer sedang menangani perbaikan fasilitas."
            repository.updateReportStatus(
                reportId = reportId,
                newStatus = ReportStatus.DIPROSES,
                adminNote = note,
                assignedOfficer = officer
            )
        }
    }

    fun markReportCompleted(reportId: String, completionNote: String?) {
        viewModelScope.launch {
            val note = completionNote?.ifBlank { null } ?: "Fasilitas telah selesai diperbaiki dan siap digunakan kembali."
            repository.updateReportStatus(
                reportId = reportId,
                newStatus = ReportStatus.SELESAI,
                adminNote = note
            )
        }
    }

    fun submitRating(reportId: String, rating: Int, feedback: String?) {
        viewModelScope.launch {
            repository.submitRatingAndFeedback(reportId, rating, feedback)
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }
}
