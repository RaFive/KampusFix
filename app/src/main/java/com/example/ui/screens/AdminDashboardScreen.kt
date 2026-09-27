package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacilityReport
import com.example.data.model.ReportStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBlueDark
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusSurfaceVariant
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusWaiting
import com.example.ui.viewmodel.KampusFixViewModel

/**
 * Halaman Khusus Admin (Admin R - Sarana & Prasarana)
 * Sesuai ketentuan:
 * - Admin bernama "Admin R"
 * - Halaman & menu khusus untuk Admin (berbeda total dari Mahasiswa)
 * - Kelola status laporan (Verifikasi -> Tentukan Petugas & Mulai -> Selesai)
 * - Tinjau feedback & rating dari mahasiswa (Sonetto)
 * - Tombol Keluar untuk kembali ke Login
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: KampusFixViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.allReports.collectAsState()
    val waitingCount by viewModel.waitingCount.collectAsState()
    val inProgressCount by viewModel.inProgressCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()

    var selectedTabFilter by remember { mutableStateOf<ReportStatus?>(null) }
    var searchAdminQuery by remember { mutableStateOf("") }
    var selectedReportForDetail by remember { mutableStateOf<FacilityReport?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Filter reports based on tab & search query
    val filteredList = reports.filter { report ->
        val matchesTab = selectedTabFilter == null || report.status == selectedTabFilter
        val matchesQuery = searchAdminQuery.isBlank() ||
                report.title.contains(searchAdminQuery, ignoreCase = true) ||
                report.building.contains(searchAdminQuery, ignoreCase = true) ||
                report.room.contains(searchAdminQuery, ignoreCase = true) ||
                report.reporterName.contains(searchAdminQuery, ignoreCase = true) ||
                report.id.contains(searchAdminQuery, ignoreCase = true)
        matchesTab && matchesQuery
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "KAMPUSFIX ADMIN",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp,
                                color = CampusTextPrimary
                            )
                            Text(
                                text = "Petugas: Admin R • Sarana & Prasarana",
                                fontSize = 11.sp,
                                color = CampusBluePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.testTag("btn_admin_logout")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Keluar",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Keluar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CampusSurface)
            )
        },
        containerColor = CampusCanvas,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Admin Welcome Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CampusBlueDark),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Panel Kontrol Sarpras",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "Halo, Admin R 🛡️",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Shift Aktif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric Counter Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricSummaryBox(
                                count = reports.size,
                                label = "Total Masuk",
                                modifier = Modifier.weight(1f)
                            )
                            MetricSummaryBox(
                                count = waitingCount,
                                label = "Menunggu",
                                modifier = Modifier.weight(1f)
                            )
                            MetricSummaryBox(
                                count = inProgressCount,
                                label = "Diproses",
                                modifier = Modifier.weight(1f)
                            )
                            MetricSummaryBox(
                                count = completedCount,
                                label = "Selesai",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Search Bar for Admin
            item {
                OutlinedTextField(
                    value = searchAdminQuery,
                    onValueChange = { searchAdminQuery = it },
                    placeholder = { Text("Cari fasilitas, ruang, atau nama pelapor...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_admin_search"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = CampusTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchAdminQuery.isNotEmpty()) {
                            IconButton(onClick = { searchAdminQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus pencarian",
                                    tint = CampusTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusBluePrimary,
                        unfocusedBorderColor = CampusBorder,
                        focusedContainerColor = CampusSurface,
                        unfocusedContainerColor = CampusSurface
                    )
                )
            }

            // Filter Tabs Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTabFilter == null,
                            onClick = { selectedTabFilter = null },
                            label = { Text("Semua (${reports.size})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusBluePrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_admin_all")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTabFilter == ReportStatus.MENUNGGU,
                            onClick = { selectedTabFilter = ReportStatus.MENUNGGU },
                            label = { Text("⏳ Menunggu ($waitingCount)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusWaiting,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_admin_waiting")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTabFilter == ReportStatus.DIPROSES,
                            onClick = { selectedTabFilter = ReportStatus.DIPROSES },
                            label = { Text("⚙️ Diproses ($inProgressCount)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusProcessing,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_admin_in_progress")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTabFilter == ReportStatus.SELESAI,
                            onClick = { selectedTabFilter = ReportStatus.SELESAI },
                            label = { Text(" Selesai ($completedCount)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusCompleted,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("chip_admin_completed")
                        )
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Laporan Masuk (${filteredList.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextPrimary
                    )
                    Text(
                        text = "Klik untuk tindak lanjuti",
                        fontSize = 11.sp,
                        color = CampusTextSecondary
                    )
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = CampusSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = CampusTextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tidak ada laporan pada kategori ini",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CampusTextPrimary
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { report ->
                    AdminReportItemCard(
                        report = report,
                        onClick = { selectedReportForDetail = report }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Detail & Action Modal for Admin (Per PDF pages 15-17)
    if (selectedReportForDetail != null) {
        AdminDetailActionDialog(
            report = selectedReportForDetail!!,
            viewModel = viewModel,
            onDismiss = { selectedReportForDetail = null }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari akun Admin R?",
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
                    shape = RoundedCornerShape(10.dp)
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
private fun MetricSummaryBox(
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun AdminReportItemCard(
    report: FacilityReport,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("admin_card_${report.id}"),
        colors = CardDefaults.cardColors(containerColor = CampusSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Row 1: ID, Priority, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${report.id}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CampusBluePrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (report.priority) {
                                    "Tinggi" -> Color(0xFFFEE2E2)
                                    "Sedang" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFE0F2FE)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = report.priority,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (report.priority) {
                                "Tinggi" -> Color(0xFFDC2626)
                                "Sedang" -> Color(0xFFD97706)
                                else -> CampusBluePrimary
                            }
                        )
                    }
                }
                StatusBadge(status = report.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = report.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CampusTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Location & Reporter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = CampusTextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${report.building} • ${report.room}",
                    fontSize = 12.sp,
                    color = CampusTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = CampusBluePrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pelapor: ${report.reporterName} (${report.reporterNim})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = CampusTextPrimary
                    )
                }

                Text(
                    text = report.createdAtFormatted,
                    fontSize = 11.sp,
                    color = CampusTextSecondary
                )
            }

            if (!report.assignedOfficer.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CampusSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Engineering,
                        contentDescription = null,
                        tint = CampusBluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Petugas: ${report.assignedOfficer}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CampusBluePrimary
                    )
                }
            }
        }
    }
}

/**
 * Dialog Operasional Khusus Admin R untuk mengelola laporan fasilitas:
 * 1. Verifikasi Laporan
 * 2. Tentukan Teknisi & Mulai Perbaikan
 * 3. Selesaikan Perbaikan
 * 4. Tinjau rating & feedback dari Mahasiswa (Sonetto)
 */
@Composable
private fun AdminDetailActionDialog(
    report: FacilityReport,
    viewModel: KampusFixViewModel,
    onDismiss: () -> Unit
) {
    val officers = listOf(
        "Pak Hendra (Teknisi AC)",
        "Pak Joko (Teknisi Kelistrikan)",
        "Pak Budi (Teknisi Sanitasi/Pipa)",
        "Pak Agus (Teknisi Sarpras/Sipil)"
    )

    var selectedOfficer by remember { mutableStateOf(report.assignedOfficer ?: officers.first()) }
    var technicianNote by remember {
        mutableStateOf(report.adminNote ?: "Teknisi sedang menuju lokasi ${report.room} untuk perbaikan.")
    }
    var completionNote by remember {
        mutableStateOf("Perbaikan fasilitas telah selesai dilaksanakan dan diverifikasi normal.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tindak Lanjut #${report.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Prioritas: ${report.priority} • ${report.category}",
                        fontSize = 11.sp,
                        color = CampusTextSecondary
                    )
                }
                StatusBadge(status = report.status)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CampusSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = report.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📍 ${report.building} - ${report.room}",
                            fontSize = 12.sp,
                            color = CampusTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "👤 Pelapor: ${report.reporterName} (NIM: ${report.reporterNim})",
                            fontSize = 11.sp,
                            color = CampusTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${report.description}\"",
                            fontSize = 12.sp,
                            color = CampusTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                HorizontalDivider(color = CampusBorder)

                // Workflow Actions per PDF pages 15-17
                when (report.status) {
                    ReportStatus.MENUNGGU -> {
                        Text(
                            text = "Langkah 1: Verifikasi Laporan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )
                        Text(
                            text = "Laporan baru masuk dari mahasiswa. Konfirmasi validitas pengaduan untuk melanjutkan ke penugasan teknisi.",
                            fontSize = 12.sp,
                            color = CampusTextSecondary
                        )
                        Button(
                            onClick = {
                                viewModel.verifyReport(report.id)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_admin_verifikasi"),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("VERIFIKASI LAPORAN", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    ReportStatus.DIVERIFIKASI -> {
                        Text(
                            text = "Langkah 2: Penugasan & Perbaikan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )

                        Text(
                            text = "Catatan Pengerjaan / Tindak Lanjut:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CampusTextSecondary
                        )

                        OutlinedTextField(
                            value = technicianNote,
                            onValueChange = { technicianNote = it },
                            placeholder = { Text("Contoh: Tim teknisi dikerahkan untuk perbaikan", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 2
                        )

                        Button(
                            onClick = {
                                viewModel.assignAndStartRepair(
                                    reportId = report.id,
                                    officer = "Tim Teknisi Sarpras",
                                    adminNote = technicianNote
                                )
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_admin_mulai_perbaikan"),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusProcessing),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TUGASKAN & MULAI PERBAIKAN", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    ReportStatus.DIPROSES -> {
                        Text(
                            text = "Langkah 3: Selesaikan Penanganan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )

                        if (!report.assignedOfficer.isNullOrBlank()) {
                            Text(
                                text = "Petugas di lapangan: ${report.assignedOfficer}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CampusBluePrimary
                            )
                        }

                        Text(
                            text = "Catatan Hasil Pekerjaan:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CampusTextSecondary
                        )

                        OutlinedTextField(
                            value = completionNote,
                            onValueChange = { completionNote = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 2
                        )

                        Button(
                            onClick = {
                                viewModel.markReportCompleted(
                                    reportId = report.id,
                                    completionNote = completionNote
                                )
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_admin_selesai"),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TANDAI SELESAI", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    ReportStatus.SELESAI -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFD1FAE5))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = StatusCompleted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Fasilitas Telah Selesai Diperbaiki",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }
                            if (!report.adminNote.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Catatan: ${report.adminNote}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }

                        // Display Student Rating & Review if available
                        if (report.rating > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Penilaian dari Mahasiswa (${report.reporterName}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CampusTextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= report.rating) Color(0xFFF59E0B) else CampusBorder,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${report.rating}/5",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusTextPrimary
                                )
                            }
                            if (!report.feedback.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"${report.feedback}\"",
                                    fontSize = 11.sp,
                                    color = CampusTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = CampusBluePrimary, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = CampusSurface
    )
}
