package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FacilityReport
import com.example.data.model.NotificationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM facility_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<FacilityReport>>

    @Query("SELECT * FROM facility_reports WHERE id = :reportId LIMIT 1")
    fun getReportById(reportId: String): Flow<FacilityReport?>

    @Query("SELECT * FROM facility_reports WHERE id = :reportId LIMIT 1")
    suspend fun getReportByIdSync(reportId: String): FacilityReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: FacilityReport)

    @Update
    suspend fun updateReport(report: FacilityReport)

    @Query("DELETE FROM facility_reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
