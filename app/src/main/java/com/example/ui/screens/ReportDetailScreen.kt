package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ReportStatus
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.TimelineStepper
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusSurfaceVariant
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.theme.StatusCompleted
import com.example.ui.viewmodel.KampusFixViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: String,
    viewModel: KampusFixViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allReports by viewModel.allReports.collectAsState()
    val report = allReports.find { it.id == reportId }
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    var userRating by remember(report?.rating) { mutableIntStateOf(report?.rating ?: 0) }
    var feedbackText by remember(report?.feedback) { mutableStateOf(report?.feedback ?: "") }
    var ratingSavedMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Laporan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CampusTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = CampusTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CampusSurface)
            )
        },
        containerColor = CampusCanvas,
        modifier = modifier
    ) { innerPadding ->
        if (report == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Laporan tidak ditemukan.", color = CampusTextSecondary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info Card (Prompt Section 7)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${report.id}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CampusBluePrimary
                            )
                            StatusBadge(status = report.status, large = true)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = report.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lokasi",
                                tint = CampusBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${report.building} • ${report.room}",
                                fontSize = 13.sp,
                                color = CampusTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category tag & Pelapor info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CampusSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(report.category),
                                    contentDescription = null,
                                    tint = CampusBluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = report.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CampusTextPrimary
                                )
                            }

                            Text(
                                text = "Pelapor: ${report.reporterName} (${report.reporterNim})",
                                fontSize = 11.sp,
                                color = CampusTextSecondary
                            )
                        }

                        if (report.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CampusCanvas, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = report.description,
                                    fontSize = 13.sp,
                                    color = CampusTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Photo Preview (if present)
                if (report.photoUri != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CampusSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Foto Fasilitas Terkait",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CampusTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                if (report.photoUri == "sample_ac") {
                                    Image(
                                        painter = painterResource(id = R.drawable.sample_ac_unit),
                                        contentDescription = "Foto AC",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    AsyncImage(
                                        model = report.photoUri,
                                        contentDescription = "Foto Laporan",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // Tracking & Timeline Card (Prompt Section 7: Timeline Stepper)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Status Penanganan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TimelineStepper(report = report)
                    }
                }

                // Rating & Feedback Card (Prompt Section 12: "Setelah laporan selesai: Apakah masalah sudah terselesaikan? ⭐⭐⭐⭐⭐ 💬 Feedback")
                if (report.status == ReportStatus.SELESAI) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CampusSurface),
                        shape = RoundedCornerShape(18.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Evaluasi Perbaikan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CampusTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Apakah masalah sudah terselesaikan dengan baik?",
                                fontSize = 13.sp,
                                color = CampusTextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            StarRatingBar(
                                rating = userRating,
                                onRatingChanged = { newRate ->
                                    userRating = newRate
                                },
                                starSize = 36
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = feedbackText,
                                onValueChange = { feedbackText = it },
                                placeholder = { Text("Berikan ulasan / tanggapan teknisi...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CampusBluePrimary,
                                    unfocusedBorderColor = CampusBorder
                                ),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.submitRating(report.id, userRating, feedbackText)
                                    ratingSavedMessage = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_simpan_rating"),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                                shape = RoundedCornerShape(12.dp),
                                enabled = userRating > 0
                            ) {
                                Text(
                                    text = if (report.rating > 0) "Perbarui Penilaian" else "Kirim Penilaian",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            AnimatedVisibility(visible = ratingSavedMessage) {
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusCompleted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Terima kasih atas penilaian Anda!",
                                        fontSize = 12.sp,
                                        color = StatusCompleted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
