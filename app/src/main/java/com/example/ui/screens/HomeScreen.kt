package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FacilityReport
import com.example.data.model.ReportStatus
import com.example.ui.components.ReportItemCard
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBlueDark
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusWaiting
import com.example.ui.viewmodel.KampusFixViewModel

@Composable
fun HomeScreen(
    viewModel: KampusFixViewModel,
    onCreateReportClick: () -> Unit,
    onReportClick: (String) -> Unit,
    onViewAllReportsClick: (ReportStatus?) -> Unit,
    onAdminDashboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.allReports.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val waitingCount by viewModel.waitingCount.collectAsState()
    val inProgressCount by viewModel.inProgressCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    val greetingName = currentUser?.name ?: "Sonetto"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CampusCanvas)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Greeting Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halo, $greetingName 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CampusTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ada fasilitas bermasalah di kampus?",
                        fontSize = 14.sp,
                        color = CampusTextSecondary
                    )
                }

                if (isAdminMode) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CampusBluePrimary)
                            .clickable(onClick = onAdminDashboardClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Admin",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mode Sarpras",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Campus Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_kampus_banner),
                        contentDescription = "Kampus Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        CampusBlueDark.copy(alpha = 0.85f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Lapor Cepat, Fasilitas Siap!",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bantu jaga kenyamanan belajar di seluruh kampus.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Big Primary Action Button (Prompt Section 1 & 2: "＋ BUAT LAPORAN")
        item {
            Button(
                onClick = onCreateReportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_buat_laporan"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CampusBluePrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BUAT LAPORAN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Section "Laporan Saya" Counter Cards (Prompt Section 2: 2 Menunggu, 1 Diproses, Selesai)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Laporan Saya",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextPrimary
                    )
                    Text(
                        text = "Lihat Semua",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CampusBluePrimary,
                        modifier = Modifier
                            .clickable { onViewAllReportsClick(null) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Menunggu card
                    StatusCountCard(
                        count = waitingCount,
                        label = "Menunggu",
                        accentColor = StatusWaiting,
                        icon = Icons.Default.HourglassEmpty,
                        onClick = { onViewAllReportsClick(ReportStatus.MENUNGGU) },
                        modifier = Modifier.weight(1f)
                    )
                    // Diproses card
                    StatusCountCard(
                        count = inProgressCount,
                        label = "Diproses",
                        accentColor = StatusProcessing,
                        icon = Icons.Default.PendingActions,
                        onClick = { onViewAllReportsClick(ReportStatus.DIPROSES) },
                        modifier = Modifier.weight(1f)
                    )
                    // Selesai card
                    StatusCountCard(
                        count = completedCount,
                        label = "Selesai",
                        accentColor = StatusCompleted,
                        icon = Icons.Default.CheckCircle,
                        onClick = { onViewAllReportsClick(ReportStatus.SELESAI) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section "Laporan Terbaru" (Prompt Section 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Laporan Terbaru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusTextPrimary
                )
            }
        }

        val recentReports = reports.take(4)
        if (recentReports.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = CampusTextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada laporan aktif",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CampusTextPrimary
                        )
                        Text(
                            text = "Tekan tombol Buat Laporan di atas bila ada fasilitas rusak.",
                            fontSize = 12.sp,
                            color = CampusTextSecondary
                        )
                    }
                }
            }
        } else {
            items(recentReports, key = { it.id }) { report ->
                ReportItemCard(
                    report = report,
                    onClick = { onReportClick(report.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatusCountCard(
    count: Int,
    label: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CampusSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = count.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CampusTextPrimary
            )

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = CampusTextSecondary
            )
        }
    }
}
