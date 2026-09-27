package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class ReportStatus(val label: String, val stepIndex: Int) {
    MENUNGGU("Menunggu", 0),
    DIVERIFIKASI("Diverifikasi", 1),
    DIPROSES("Diproses", 2),
    SELESAI("Selesai", 3)
}

enum class FacilityCategory(
    val title: String,
    val iconName: String,
    val sampleDescriptions: List<String>
) {
    AC("AC", "ac_unit", listOf("AC tidak dingin / bocor air", "Remote AC hilang / rusak", "AC mati total")),
    LAMPU("Lampu", "lightbulb", listOf("Lampu berkedip / redup", "Lampu padam total", "Saklar lampu rusak")),
    KURSI_MEJA("Kursi/Meja", "chair", listOf("Kaki kursi goyang / patah", "Meja retak / baut lepas", "Papan sandaran kursi copot")),
    PROYEKTOR("Proyektor", "videocam", listOf("Proyektor tidak mau menyala", "Kabel HDMI / port rusak", "Gambar buram / berbayang")),
    TOILET("Toilet", "wc", listOf("Kran air bocor / macet", "Flush tidak berfungsi", "Saluran air mampet")),
    WIFI("Wi-Fi", "wifi", listOf("Koneksi Wi-Fi sangat lambat", "Tidak bisa connect / login gagal", "Router indikator merah")),
    PINTU_JENDELA("Pintu/Jendela", "door_front", listOf("Engsel pintu berderit / lepas", "Kunci pintu macet / rusak", "Kaca jendela retak")),
    FASILITAS_LAIN("Fasilitas lainnya", "build", listOf("Papan tulis bernoda / rusak", "Stopkontak tidak ada aliran listrik", "Tempat sampah rusak"))
}

data class TimelineEvent(
    val title: String,
    val timestamp: String,
    val description: String,
    val isDone: Boolean
)

@Entity(tableName = "facility_reports")
data class FacilityReport(
    @PrimaryKey val id: String = "KP-" + (10000..99999).random(),
    val title: String,
    val category: String,
    val building: String,
    val room: String,
    val photoUri: String? = null,
    val description: String,
    val status: ReportStatus = ReportStatus.MENUNGGU,
    val priority: String = "Tinggi", // Rendah, Sedang, Tinggi
    val assignedOfficer: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val createdAtFormatted: String,
    val adminNote: String? = null,
    val verifiedAt: String? = null,
    val inProgressAt: String? = null,
    val completedAt: String? = null,
    val rating: Int = 0,
    val feedback: String? = null,
    val reporterName: String = "Sonetto",
    val reporterNim: String = "G1Z030099",
    val reporterMajor: String = "Teknik Rekayasa Mantra"
)

enum class UserRole {
    MAHASISWA,
    ADMIN
}

data class UserAccount(
    val username: String,
    val name: String,
    val role: UserRole,
    val nim: String? = null,
    val major: String? = null,
    val email: String,
    val password: String = "123456"
)

object DemoAccounts {
    val MAHASISWA = UserAccount(
        username = "G1Z030099",
        name = "Sonetto",
        role = UserRole.MAHASISWA,
        nim = "G1Z030099",
        major = "Teknik Rekayasa Mantra",
        email = "sonetto@kampus.ac.id",
        password = "Soneto123"
    )

    val ADMIN = UserAccount(
        username = "adminr",
        name = "Admin R",
        role = UserRole.ADMIN,
        nim = null,
        major = "Sarana & Prasarana",
        email = "admin.r@kampus.ac.id",
        password = "Admin123"
    )

    val ALL = listOf(MAHASISWA, ADMIN)
}

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val reportId: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val statusType: String = "INFO" // INFO, PROCESSING, COMPLETED
)
