package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FacilityReport
import com.example.data.model.NotificationItem
import com.example.data.model.ReportStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String = status.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = try {
        ReportStatus.valueOf(value)
    } catch (e: Exception) {
        ReportStatus.MENUNGGU
    }
}

@Database(
    entities = [FacilityReport::class, NotificationItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KampusFixDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: KampusFixDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): KampusFixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KampusFixDatabase::class.java,
                    "kampusfix_database_v2"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.reportDao(), database.notificationDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(
            reportDao: ReportDao,
            notificationDao: NotificationDao
        ) {
            val report1 = FacilityReport(
                id = "KP-00125",
                title = "AC Ruang 203 Rusak",
                category = "AC",
                building = "Gedung Teknik",
                room = "Ruang 203",
                photoUri = "sample_ac",
                description = "AC menyala tetapi tidak mengeluarkan udara dingin sama sekali dan ada tetesan air di bagian bawah unit.",
                status = ReportStatus.DIPROSES,
                createdAt = System.currentTimeMillis() - 4 * 3600 * 1000,
                createdAtFormatted = "12 Sept, 08:30",
                verifiedAt = "12 Sept, 09:15",
                inProgressAt = "12 Sept, 11:00",
                completedAt = null,
                adminNote = "Teknisi Pak Hendra sedang menuju ruang 203 dengan membawa sparepart kompresor pendingin.",
                rating = 0,
                reporterName = "Sonetto",
                reporterNim = "G1Z030099",
                reporterMajor = "Teknik Rekayasa Mantra"
            )

            val report2 = FacilityReport(
                id = "KP-00124",
                title = "Lampu Koridor Redup & Berkedip",
                category = "Lampu",
                building = "Gedung B",
                room = "Koridor Lantai 2",
                photoUri = null,
                description = "Lampu penerangan jalan lorong lantai 2 berkedip cepat terus menerus sehingga mengganggu penglihatan saat malam.",
                status = ReportStatus.SELESAI,
                priority = "Sedang",
                createdAt = System.currentTimeMillis() - 24 * 3600 * 1000,
                createdAtFormatted = "11 Sept, 14:10",
                verifiedAt = "11 Sept, 15:00",
                inProgressAt = "11 Sept, 16:30",
                completedAt = "12 Sept, 09:45",
                adminNote = "Bohlam LED tube 18W dan ballast telah diganti baru oleh teknisi sarpras. Lampu menyala normal.",
                rating = 5,
                feedback = "Perbaikan sangat cepat, lorong gedung sekarang terang kembali. Terima kasih!",
                reporterName = "Sonetto",
                reporterNim = "G1Z030099",
                reporterMajor = "Teknik Rekayasa Mantra"
            )

            val report3 = FacilityReport(
                id = "KP-00123",
                title = "Kursi Kuliah Sandaran Goyang",
                category = "Kursi/Meja",
                building = "Gedung Teknik",
                room = "Ruang 105",
                photoUri = null,
                description = "Baut sandaran kursi kuliah baris ketiga copot dan hampir patah bila diduduki.",
                status = ReportStatus.SELESAI,
                priority = "Rendah",
                createdAt = System.currentTimeMillis() - 48 * 3600 * 1000,
                createdAtFormatted = "10 Sept, 10:20",
                verifiedAt = "10 Sept, 11:00",
                inProgressAt = "10 Sept, 14:00",
                completedAt = "11 Sept, 10:00",
                adminNote = "Baut telah diganti dan diperkuat dengan ring besi baru.",
                rating = 4,
                feedback = "Sudah kokoh kembali.",
                reporterName = "Sonetto",
                reporterNim = "G1Z030099",
                reporterMajor = "Teknik Rekayasa Mantra"
            )

            val report4 = FacilityReport(
                id = "KP-00126",
                title = "Wi-Fi Perpustakaan Tidak Bisa Connect",
                category = "Wi-Fi",
                building = "Perpustakaan Pusat",
                room = "Area Baca Lt. 1",
                photoUri = null,
                description = "SSID Kampus-Hotspot muncul namun selalu gagal saat autentikasi akun portal mahasiswa.",
                status = ReportStatus.MENUNGGU,
                priority = "Tinggi",
                createdAt = System.currentTimeMillis() - 1 * 3600 * 1000,
                createdAtFormatted = "Hari ini, 13:10",
                verifiedAt = null,
                inProgressAt = null,
                completedAt = null,
                adminNote = null,
                rating = 0,
                reporterName = "Sonetto",
                reporterNim = "G1Z030099",
                reporterMajor = "Teknik Rekayasa Mantra"
            )

            val report5 = FacilityReport(
                id = "KP-00127",
                title = "Kran Wastafel Bocor Terus Menetes",
                category = "Toilet",
                building = "Gedung B",
                room = "Toilet Lt. 2",
                photoUri = null,
                description = "Kran air wastafel sebelah kiri tidak bisa ditutup rapat, air mengalir terus menerus.",
                status = ReportStatus.MENUNGGU,
                priority = "Sedang",
                createdAt = System.currentTimeMillis() - 45 * 60 * 1000,
                createdAtFormatted = "Hari ini, 13:45",
                verifiedAt = null,
                inProgressAt = null,
                completedAt = null,
                adminNote = null,
                rating = 0,
                reporterName = "Sonetto",
                reporterNim = "G1Z030099",
                reporterMajor = "Teknik Rekayasa Mantra"
            )

            reportDao.insertReport(report1)
            reportDao.insertReport(report2)
            reportDao.insertReport(report3)
            reportDao.insertReport(report4)
            reportDao.insertReport(report5)

            // Notifications matching prompt
            notificationDao.insertNotification(
                NotificationItem(
                    reportId = "KP-00125",
                    title = "Laporan Diperbarui",
                    message = "AC Ruang 203 sedang diperbaiki oleh teknisi Sarpras.",
                    timeAgo = "10 menit lalu",
                    timestamp = System.currentTimeMillis() - 10 * 60 * 1000,
                    statusType = "PROCESSING"
                )
            )
            notificationDao.insertNotification(
                NotificationItem(
                    reportId = "KP-00124",
                    title = "Laporan Selesai",
                    message = "Lampu Koridor Gedung B telah diperbaiki dan berfungsi normal.",
                    timeAgo = "2 jam lalu",
                    timestamp = System.currentTimeMillis() - 2 * 3600 * 1000,
                    statusType = "COMPLETED"
                )
            )
            notificationDao.insertNotification(
                NotificationItem(
                    reportId = "KP-00125",
                    title = "Laporan Diverifikasi",
                    message = "Laporan AC Ruang 203 telah diverifikasi dan diteruskan ke bagian teknisi AC.",
                    timeAgo = "3 jam lalu",
                    timestamp = System.currentTimeMillis() - 3 * 3600 * 1000,
                    statusType = "INFO"
                )
            )
        }
    }
}
