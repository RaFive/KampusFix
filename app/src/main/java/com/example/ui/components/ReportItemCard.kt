package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacilityReport
import com.example.data.model.ReportStatus
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "ac" -> Icons.Default.AcUnit
        "lampu" -> Icons.Default.Lightbulb
        "kursi/meja", "kursi", "meja" -> Icons.Default.Chair
        "proyektor" -> Icons.Default.Videocam
        "toilet" -> Icons.Default.Wc
        "wi-fi", "wifi" -> Icons.Default.Wifi
        "pintu/jendela", "pintu", "jendela" -> Icons.Default.DoorFront
        else -> Icons.Default.Build
    }
}

@Composable
fun ReportItemCard(
    report: FacilityReport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("report_card_${report.id}"),
        colors = CardDefaults.cardColors(
            containerColor = CampusSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CampusBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(report.category),
                            contentDescription = report.category,
                            tint = CampusBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "#${report.id}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusBluePrimary
                        )
                        Text(
                            text = report.createdAtFormatted,
                            fontSize = 11.sp,
                            color = CampusTextSecondary
                        )
                    }
                }

                StatusBadge(status = report.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = report.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CampusTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = CampusTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${report.building} • ${report.room}",
                    fontSize = 12.sp,
                    color = CampusTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (report.status == ReportStatus.SELESAI && report.rating > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(report.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dinilai ${report.rating}/5",
                        fontSize = 11.sp,
                        color = CampusTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
