package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReportStatus
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusCompletedBg
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusProcessingBg
import com.example.ui.theme.StatusVerified
import com.example.ui.theme.StatusVerifiedBg
import com.example.ui.theme.StatusWaiting
import com.example.ui.theme.StatusWaitingBg

@Composable
fun StatusBadge(
    status: ReportStatus,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val (dotColor, bgColor, textColor, label) = when (status) {
        ReportStatus.MENUNGGU -> Quadruple(StatusWaiting, StatusWaitingBg, StatusWaiting, "Menunggu")
        ReportStatus.DIVERIFIKASI -> Quadruple(StatusVerified, StatusVerifiedBg, StatusVerified, "Diverifikasi")
        ReportStatus.DIPROSES -> Quadruple(StatusProcessing, StatusProcessingBg, StatusProcessing, "Diproses")
        ReportStatus.SELESAI -> Quadruple(StatusCompleted, StatusCompletedBg, StatusCompleted, "Selesai")
    }

    val horizontalPad = if (large) 12.dp else 8.dp
    val verticalPad = if (large) 6.dp else 4.dp
    val fontSize = if (large) 13.sp else 11.sp

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bgColor)
            .padding(horizontal = horizontalPad, vertical = verticalPad),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (large) 8.dp else 6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
