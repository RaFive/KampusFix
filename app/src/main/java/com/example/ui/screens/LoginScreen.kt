package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DemoAccounts
import com.example.data.model.UserAccount
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBlueDark
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary

/**
 * Halaman Login Awal KampusFix:
 * - Tidak menggunakan kata "SSO kampus"
 * - Hanya tersimpan SATU akun di halaman ini (Akun Mahasiswa Sonetto - G1Z030099)
 * - Tombol utama masuk ke akun kampus
 * - Opsi masuk dengan akun lain
 */
@Composable
fun LoginScreen(
    onNavigateToSso: () -> Unit,
    onQuickLogin: (UserAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSecurityInfo by remember { mutableStateOf(false) }
    var isSavedAccountVisible by remember { mutableStateOf(true) }

    // Hanya SATU akun tersimpan di perangkat (Mahasiswa Sonetto)
    val savedAccount = DemoAccounts.MAHASISWA

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CampusCanvas)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Branding Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_unib),
                    contentDescription = "Logo UNIB",
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "KAMPUSFIX",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = CampusBluePrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Laporkan fasilitas\nkampus dengan mudah.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = CampusTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // Center / Main Action Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Kartu Akun Tersimpan (bisa dihilangkan dengan tombol X)
            if (isSavedAccountVisible) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Akun Tersimpan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusTextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CampusBlueContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "1 Akun Aktif",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CampusBluePrimary
                                    )
                                }
                            }

                            // Tombol X untuk menghilangkan akun tersimpan
                            IconButton(
                                onClick = { isSavedAccountVisible = false },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("btn_remove_saved_account")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus akun tersimpan",
                                    tint = CampusTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Kartu 1 Akun Tersimpan (Sonetto)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CampusCanvas)
                                .clickable { onQuickLogin(savedAccount) }
                                .padding(12.dp)
                                .testTag("btn_saved_account_login"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(CampusBluePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = savedAccount.name.take(1),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = savedAccount.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CampusTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CampusBluePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "NPM: ${savedAccount.nim ?: ""} • ${savedAccount.major ?: ""}",
                                        fontSize = 11.sp,
                                        color = CampusTextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Masuk",
                                tint = CampusBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tombol Masuk Cepat dengan Akun Tersimpan
                        Button(
                            onClick = { onQuickLogin(savedAccount) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_masuk_akun_tersimpan"),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Lanjutkan sebagai ${savedAccount.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Masuk dengan Akun Kampus Lainnya
                OutlinedButton(
                    onClick = onNavigateToSso,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_masuk_akun_lain"),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CampusBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuk dengan Akun Lain",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusBluePrimary
                    )
                }
            } else {
                // Tampilan jika akun tersimpan telah dihilangkan / ditutup
                Button(
                    onClick = onNavigateToSso,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_masuk_ke_akun"),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Masuk ke Akun Kampus",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informasi keamanan link (tanpa kata SSO)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showSecurityInfo = true }
                    .padding(8.dp)
                    .testTag("link_info_keamanan")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = CampusTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Informasi keamanan akun",
                    fontSize = 13.sp,
                    color = CampusTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Footer
        Text(
            text = "Versi 0.8.2",
            fontSize = 11.sp,
            color = CampusTextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }

    if (showSecurityInfo) {
        AlertDialog(
            onDismissRequest = { showSecurityInfo = false },
            title = {
                Text(
                    text = "Keamanan Akun Kampus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "KampusFix menggunakan autentikasi terintegrasi portal universitas. Data login dan pengaduan Anda terlindungi oleh sistem enkripsi kampus dengan aman dan transparan.",
                    fontSize = 13.sp,
                    color = CampusTextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showSecurityInfo = false }) {
                    Text(text = "Mengerti", fontWeight = FontWeight.Bold, color = CampusBluePrimary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = CampusSurface
        )
    }
}
