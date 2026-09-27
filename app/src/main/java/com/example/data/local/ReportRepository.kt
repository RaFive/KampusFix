package com.example.data.local

import com.example.data.model.FacilityReport
import com.example.data.model.NotificationItem
import com.example.data.model.ReportStatus
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepository(
    private val reportDao: ReportDao,
    private val notificationDao: NotificationDao
) {
    val allReports: Flow<List<FacilityReport>> = reportDao.getAllReports()
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()

    fun getReportById(reportId: String): Flow<FacilityReport?> {
        return reportDao.getReportById(reportId)
    }

    suspend fun createReport(
        title: String,
        category: String,
        building: String,
        room: String,
        photoUri: String?,
        description: String,
        priority: String = "Tinggi",
        reporterName: String = "Sonetto",
        reporterNim: String = "G1Z030099",
        reporterMajor: String = "Teknik Rekayasa Mantra"
    ): FacilityReport {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
        val formattedDate = dateFormat.format(Date(currentTime))
        val reportId = "KP-" + (10000..99999).random()

        val newReport = FacilityReport(
            id = reportId,
            title = title,
            category = category,
            building = building,
            room = room,
            photoUri = photoUri,
            description = description,
            status = ReportStatus.MENUNGGU,
            priority = priority,
            createdAt = currentTime,
            createdAtFormatted = formattedDate,
            reporterName = reporterName,
            reporterNim = reporterNim,
            reporterMajor = reporterMajor
        )

        reportDao.insertReport(newReport)

        // Insert confirmation notification
        notificationDao.insertNotification(
            NotificationItem(
                reportId = reportId,
                title = "Laporan Berhasil Terkirim",
                message = "Laporan #${reportId} ($title di $building) telah diterima sistem.",
                timeAgo = "Baru saja",
                statusType = "INFO"
            )
        )

        return newReport
    }

    suspend fun updateReportStatus(
        reportId: String,
        newStatus: ReportStatus,
        adminNote: String?,
        assignedOfficer: String? = null
    ) {
        val existing = reportDao.getReportByIdSync(reportId) ?: return
        val timeNow = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date())

        val updated = existing.copy(
            status = newStatus,
            adminNote = adminNote ?: existing.adminNote,
            assignedOfficer = assignedOfficer ?: existing.assignedOfficer,
            verifiedAt = if (newStatus == ReportStatus.DIVERIFIKASI && existing.verifiedAt == null) timeNow else existing.verifiedAt,
            inProgressAt = if (newStatus == ReportStatus.DIPROSES && existing.inProgressAt == null) timeNow else existing.inProgressAt,
            completedAt = if (newStatus == ReportStatus.SELESAI && existing.completedAt == null) timeNow else existing.completedAt
        )

        reportDao.updateReport(updated)

        // Generate status update notification matching PDF page 12
        val (notifTitle, notifMsg, statusType) = when (newStatus) {
            ReportStatus.MENUNGGU -> Triple(
                "Laporan Terdaftar",
                "Laporan ${existing.title} sedang dalam antrean verifikasi.",
                "INFO"
            )
            ReportStatus.DIVERIFIKASI -> Triple(
                "Laporan Diverifikasi",
                "Laporan #${existing.id} diverifikasi oleh tim Sarpras kampus.",
                "INFO"
            )
            ReportStatus.DIPROSES -> Triple(
                "Laporan Diperbarui",
                "Laporan ${existing.title} sekarang sedang dalam proses perbaikan.",
                "PROCESSING"
            )
            ReportStatus.SELESAI -> Triple(
                "Laporan Selesai",
                "${existing.title} telah diperbaiki.",
                "COMPLETED"
            )
        }

        notificationDao.insertNotification(
            NotificationItem(
                reportId = reportId,
                title = notifTitle,
                message = notifMsg,
                timeAgo = "Baru saja",
                statusType = statusType
            )
        )
    }

    suspend fun submitRatingAndFeedback(reportId: String, rating: Int, feedback: String?) {
        val existing = reportDao.getReportByIdSync(reportId) ?: return
        reportDao.updateReport(
            existing.copy(
                rating = rating,
                feedback = feedback
            )
        )
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    suspend fun markAllNotificationsAsRead() {
        notificationDao.markAllAsRead()
    }
}
