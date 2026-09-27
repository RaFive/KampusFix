package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacilityReport
import com.example.data.model.ReportStatus
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.theme.StatusCompleted

private data class StepItem(
    val title: String,
    val time: String?,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

@Composable
fun TimelineStepper(
    report: FacilityReport,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        StepItem(
            title = "Laporan dikirim",
            time = report.createdAtFormatted,
            isCompleted = true,
            isCurrent = report.status == ReportStatus.MENUNGGU
        ),
        StepItem(
            title = "Diverifikasi",
            time = report.verifiedAt ?: if (report.status.stepIndex >= 1) "Dalam proses verifikasi" else "Menunggu antrean",
            isCompleted = report.status.stepIndex >= 1,
            isCurrent = report.status == ReportStatus.DIVERIFIKASI
        ),
        StepItem(
            title = "Diproses",
            time = report.inProgressAt ?: if (report.status.stepIndex >= 2) "Penanganan aktif" else "Setelah diverifikasi",
            isCompleted = report.status.stepIndex >= 2,
            isCurrent = report.status == ReportStatus.DIPROSES
        ),
        StepItem(
            title = "Selesai",
            time = report.completedAt ?: if (report.status == ReportStatus.SELESAI) "Selesai" else "Menunggu penanganan",
            isCompleted = report.status == ReportStatus.SELESAI,
            isCurrent = false
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            TimelineNode(
                step = step,
                isLast = index == steps.size - 1
            )
        }

        // Catatan Admin Card (Matching Prompt section 7)
        if (!report.adminNote.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEFF6FF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CampusBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Admin Support",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Catatan Admin Sarpras:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusBluePrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\"${report.adminNote}\"",
                            fontSize = 13.sp,
                            color = CampusTextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineNode(
    step: StepItem,
    isLast: Boolean
) {
    val nodeColor by animateColorAsState(
        targetValue = when {
            step.isCompleted -> StatusCompleted
            step.isCurrent -> CampusBluePrimary
            else -> CampusBorder
        },
        label = "nodeColor"
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline vertical indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (step.isCompleted) StatusCompleted else if (step.isCurrent) CampusBluePrimary else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = nodeColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                } else if (step.isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(if (step.isCompleted) StatusCompleted else CampusBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Step text details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Text(
                text = step.title,
                fontSize = 14.sp,
                fontWeight = if (step.isCompleted || step.isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (step.isCompleted || step.isCurrent) CampusTextPrimary else CampusTextSecondary
            )
            if (step.time != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = CampusTextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = step.time,
                        fontSize = 12.sp,
                        color = CampusTextSecondary
                    )
                }
            }
        }
    }
}
