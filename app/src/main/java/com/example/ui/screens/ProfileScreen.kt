package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusSurfaceVariant
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.viewmodel.AppThemeMode
import com.example.ui.viewmodel.KampusFixViewModel
import com.example.ui.viewmodel.NotificationPreferences

@Composable
fun ProfileScreen(
    viewModel: KampusFixViewModel,
    onNavigateToAdminDashboard: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val notifPrefs by viewModel.notifPrefs.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showNotifSettingsDialog by remember { mutableStateOf(false) }
    var showAppSettingsDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val studentName = currentUser?.name ?: "Sonetto"
    val studentNim = currentUser?.nim ?: "G1Z030099"
    val studentMajor = currentUser?.major ?: "Teknik Rekayasa Mantra"
    val studentEmail = currentUser?.email ?: "sonetto@kampus.ac.id"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CampusCanvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Profil",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CampusTextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Identity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CampusSurface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CampusBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto Profil",
                        tint = CampusBluePrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = studentName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CampusTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = studentNim,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CampusBluePrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = CampusTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = studentMajor,
                        fontSize = 13.sp,
                        color = CampusTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = studentEmail,
                    fontSize = 12.sp,
                    color = CampusTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Card (Mode Admin Sarpras Dihapus sesuai permintaan user)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CampusSurface),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    title = "Profil Mahasiswa",
                    subtitle = "Lihat nama, email, dan jurusan",
                    onClick = { showEditProfileDialog = true }
                )
                HorizontalDivider(color = CampusBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Pengaturan Notifikasi",
                    subtitle = "Atur jenis notifikasi pembaruan fasilitas",
                    onClick = { showNotifSettingsDialog = true }
                )
                HorizontalDivider(color = CampusBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Pengaturan",
                    subtitle = "Preferensi sistem",
                    onClick = { showAppSettingsDialog = true }
                )
                HorizontalDivider(color = CampusBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = "Bantuan & FAQ",
                    subtitle = "Panduan pelaporan dan tindak lanjut sarpras",
                    onClick = { showFaqDialog = true }
                )
                HorizontalDivider(color = CampusBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Keluar",
                    subtitle = "Keluar dari akun $studentName",
                    tint = Color(0xFFDC2626),
                    onClick = { showLogoutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // DIALOG 1: Edit Profil (Tidak dapat diedit, tersinkronisasi SIAKAD)
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Profil Mahasiswa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CampusTextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Data profil mahasiswa disinkronkan secara otomatis dengan SIAKAD / SSO Kampus dan tidak dapat diedit secara manual.",
                        fontSize = 12.sp,
                        color = CampusTextSecondary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Nama Lengkap", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CampusTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_nama"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CampusSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = CampusSurfaceVariant.copy(alpha = 0.5f),
                            focusedBorderColor = CampusBorder,
                            unfocusedBorderColor = CampusBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CampusTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studentEmail,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_email"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CampusSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = CampusSurfaceVariant.copy(alpha = 0.5f),
                            focusedBorderColor = CampusBorder,
                            unfocusedBorderColor = CampusBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Program Studi / Jurusan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CampusTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studentMajor,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_jurusan"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CampusSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = CampusSurfaceVariant.copy(alpha = 0.5f),
                            focusedBorderColor = CampusBorder,
                            unfocusedBorderColor = CampusBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEditProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_tutup_profil")
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CampusSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // DIALOG 2: Pengaturan Notifikasi (Dapat diatur jenisnya)
    if (showNotifSettingsDialog) {
        var statusUpdates by remember { mutableStateOf(notifPrefs.statusUpdates) }
        var technicianAssigned by remember { mutableStateOf(notifPrefs.technicianAssigned) }
        var reportCompleted by remember { mutableStateOf(notifPrefs.reportCompleted) }
        var campusAnnouncements by remember { mutableStateOf(notifPrefs.campusAnnouncements) }
        var soundAndVibrate by remember { mutableStateOf(notifPrefs.soundAndVibrate) }

        AlertDialog(
            onDismissRequest = { showNotifSettingsDialog = false },
            title = {
                Text(
                    text = "Pengaturan Notifikasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CampusTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Atur jenis notifikasi sesuai preferensi Anda:",
                        fontSize = 13.sp,
                        color = CampusTextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    NotificationSettingToggleItem(
                        title = "Update Status Perbaikan",
                        description = "Notifikasi saat laporan diverifikasi, diproses, atau selesai",
                        checked = statusUpdates,
                        onCheckedChange = { statusUpdates = it }
                    )

                    NotificationSettingToggleItem(
                        title = "Penugasan Teknisi",
                        description = "Pemberitahuan saat teknisi sarpras meluncur ke lokasi",
                        checked = technicianAssigned,
                        onCheckedChange = { technicianAssigned = it }
                    )

                    NotificationSettingToggleItem(
                        title = "Laporan Selesai & Ulasan",
                        description = "Permintaan rating setelah fasilitas selesai diperbaiki",
                        checked = reportCompleted,
                        onCheckedChange = { reportCompleted = it }
                    )

                    NotificationSettingToggleItem(
                        title = "Pengumuman Sarpras",
                        description = "Info jadwal pemeliharaan fasilitas kampus",
                        checked = campusAnnouncements,
                        onCheckedChange = { campusAnnouncements = it }
                    )

                    NotificationSettingToggleItem(
                        title = "Suara & Getar",
                        description = "Bunyikan dering ringan saat ada notifikasi masuk",
                        checked = soundAndVibrate,
                        onCheckedChange = { soundAndVibrate = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateNotifPrefs(
                            NotificationPreferences(
                                statusUpdates = statusUpdates,
                                technicianAssigned = technicianAssigned,
                                reportCompleted = reportCompleted,
                                campusAnnouncements = campusAnnouncements,
                                soundAndVibrate = soundAndVibrate
                            )
                        )
                        showNotifSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_simpan_notif_prefs")
                ) {
                    Text("Terapkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotifSettingsDialog = false }) {
                    Text("Tutup")
                }
            },
            containerColor = CampusSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // DIALOG 3: Pengaturan Tema (Dark / Light Mode)
    if (showAppSettingsDialog) {
        var selectedTheme by remember { mutableStateOf(themeMode) }

        AlertDialog(
            onDismissRequest = { showAppSettingsDialog = false },
            title = {
                Text(
                    text = "Pengaturan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CampusTextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mode Tampilan Aplikasi:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    AppThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTheme = mode }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == mode,
                                onClick = { selectedTheme = mode },
                                colors = RadioButtonDefaults.colors(selectedColor = CampusBluePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = mode.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CampusTextPrimary
                                )
                                Text(
                                    text = when (mode) {
                                        AppThemeMode.LIGHT -> "Tampilan cerah dengan kontras tinggi"
                                        AppThemeMode.DARK -> "Tampilan gelap yang nyaman di mata pada malam hari"
                                        AppThemeMode.SYSTEM -> "Menyesuaikan otomatis dengan tema sistem ponsel"
                                    },
                                    fontSize = 11.sp,
                                    color = CampusTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = CampusBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Informasi Aplikasi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "KampusFix Mobile v0.8.2",
                        fontSize = 12.sp,
                        color = CampusTextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setThemeMode(selectedTheme)
                        showAppSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_simpan_tema")
                ) {
                    Text("Terapkan Tema", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppSettingsDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = CampusSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // DIALOG 4: FAQ & Bantuan
    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = {
                Text("Bantuan & FAQ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CampusTextPrimary)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "1. Bagaimana alur laporan diproses?\nLaporan yang dikirim akan diverifikasi oleh staf sarpras, diteruskan ke teknisi, diperbaiki di lokasi, dan diakhiri dengan evaluasi rating dari Anda.",
                        fontSize = 13.sp,
                        color = CampusTextPrimary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "2. Berapa lama respon perbaikan?\nPrioritas tinggi ditindaklanjuti dalam waktu 2-4 jam. Prioritas normal maksimal 1x24 jam.",
                        fontSize = 13.sp,
                        color = CampusTextPrimary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "3. Kontak darurat Sarpras:\nCall Center Sarpras: Ext 104 / Gedung Biro Sarpras Lantai 1 Kampus.",
                        fontSize = 13.sp,
                        color = CampusTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFaqDialog = false }) {
                    Text("Tutup", color = CampusBluePrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CampusSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // DIALOG 5: Konfirmasi Keluar
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = CampusTextPrimary
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari akun $studentName ($studentNim)?",
                    fontSize = 14.sp,
                    color = CampusTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_konfirmasi_keluar")
                ) {
                    Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = CampusSurface
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    tint: Color = CampusTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (tint == Color(0xFFDC2626)) tint else CampusBluePrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tint
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = CampusTextSecondary
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = CampusTextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(13.dp)
        )
    }
}

@Composable
private fun NotificationSettingToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CampusTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = CampusTextSecondary,
                lineHeight = 15.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CampusBluePrimary
            )
        )
    }
}
