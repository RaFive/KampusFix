package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DemoAccounts
import com.example.data.model.UserRole
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBlueDark
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusBorder
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.viewmodel.KampusFixViewModel

/**
 * Halaman Login:
 * - Pemisahan Login Mahasiswa dan Admin: menu dan tulisan berganti saat tombol/tab ditekan.
 * - Untuk Mahasiswa: input "Email / NPM" dengan placeholder "Masukkan Email / NPM"
 * - Untuk Admin: input "Username" dengan placeholder "Masukkan Username"
 * - Password: placeholder "Masukkan Password" (tanpa kata contoh)
 * - Kolom input kosong dari teks secara default
 * - Teks petunjuk masuk di bawah dihapus
 * - Tombol dengan kontras warna tinggi, tegas, dan mudah dibaca
 * - Navigasi lupa password ke halaman baru
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SsoScreen(
    viewModel: KampusFixViewModel,
    onNavigateBack: () -> Unit,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Default pilihan akun ke Mahasiswa
    var selectedRole by remember { mutableStateOf(UserRole.MAHASISWA) }

    // Kolom input kosong dari teks secara default
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val isMahasiswa = selectedRole == UserRole.MAHASISWA

    fun performLogin() {
        focusManager.clearFocus()
        errorMessage = null

        val trimmedUser = usernameInput.trim()
        val trimmedPass = passwordInput.trim()

        if (trimmedUser.isEmpty() || trimmedPass.isEmpty()) {
            errorMessage = if (isMahasiswa) {
                "Silakan isi Email / NPM dan password."
            } else {
                "Silakan isi Username dan password."
            }
            return
        }

        // Jalankan autentikasi
        val success = viewModel.login(trimmedUser, trimmedPass)
        if (success) {
            val user = viewModel.currentUser.value
            if (user != null) {
                onLoginSuccess(user.role)
                return
            }
        }

        // Fallback cerdas berdasarkan tab yang aktif
        if (isMahasiswa &&
            (trimmedUser.equals("G1Z03099", ignoreCase = true) ||
             trimmedUser.equals("G1Z030099", ignoreCase = true) ||
             trimmedUser.equals("sonetto@kampus.ac.id", ignoreCase = true) ||
             trimmedUser.equals("sonetto", ignoreCase = true) ||
             trimmedUser.equals("mahasiswa", ignoreCase = true)) &&
            (trimmedPass == "Soneto123" || trimmedPass.equals("soneto123", ignoreCase = true))
        ) {
            viewModel.loginAs(DemoAccounts.MAHASISWA)
            onLoginSuccess(UserRole.MAHASISWA)
            return
        } else if (!isMahasiswa &&
            (trimmedUser.equals("adminr", ignoreCase = true) ||
             trimmedUser.equals("admin_r", ignoreCase = true) ||
             trimmedUser.equals("admin.r@kampus.ac.id", ignoreCase = true) ||
             trimmedUser.equals("admin", ignoreCase = true)) &&
            (trimmedPass == "Admin123" || trimmedPass.equals("admin123", ignoreCase = true))
        ) {
            viewModel.loginAs(DemoAccounts.ADMIN)
            onLoginSuccess(UserRole.ADMIN)
            return
        }

        errorMessage = if (isMahasiswa) {
            "Email / NPM atau password salah."
        } else {
            "Username atau password salah."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isMahasiswa) "Login Mahasiswa" else "Login Admin",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = CampusTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_back_login")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Beranda",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Logo Header Dinamis Berdasarkan Menu Mahasiswa vs Admin
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(if (isMahasiswa) CampusBlueContainer else Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMahasiswa) Icons.Default.School else Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = if (isMahasiswa) CampusBluePrimary else Color(0xFFD97706),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isMahasiswa) "Portal Mahasiswa" else "Portal Administrator",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isMahasiswa) CampusBluePrimary else Color(0xFFB45309),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isMahasiswa)
                    "Silakan masuk untuk melapor dan memantau fasilitas kampus."
                else
                    "Masuk ke panel pengelolaan dan penugasan perbaikan sarpras.",
                fontSize = 13.sp,
                color = CampusTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // TAB SWITCHER: Pemisahan Login Mahasiswa dan Admin yang Jelas dan Berkontras Tinggi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab Mahasiswa
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable {
                            selectedRole = UserRole.MAHASISWA
                            usernameInput = ""
                            passwordInput = ""
                            errorMessage = null
                        }
                        .testTag("tab_login_mahasiswa"),
                    color = if (isMahasiswa) CampusBluePrimary else Color.Transparent,
                    shape = RoundedCornerShape(11.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = if (isMahasiswa) Color.White else Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mahasiswa",
                            fontSize = 13.sp,
                            fontWeight = if (isMahasiswa) FontWeight.Bold else FontWeight.Medium,
                            color = if (isMahasiswa) Color.White else Color(0xFF475569)
                        )
                    }
                }

                // Tab Admin
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable {
                            selectedRole = UserRole.ADMIN
                            usernameInput = ""
                            passwordInput = ""
                            errorMessage = null
                        }
                        .testTag("tab_login_admin"),
                    color = if (!isMahasiswa) Color(0xFFD97706) else Color.Transparent,
                    shape = RoundedCornerShape(11.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = if (!isMahasiswa) Color.White else Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin",
                            fontSize = 13.sp,
                            fontWeight = if (!isMahasiswa) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isMahasiswa) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CampusSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CampusBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Field 1: Dinamis antara Email / NPM (Mahasiswa) vs Username (Admin)
                    Text(
                        text = if (isMahasiswa) "Email / NPM" else "Username",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = {
                            usernameInput = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                text = if (isMahasiswa) "Masukkan Email / NPM" else "Masukkan Username",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_username"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isMahasiswa) CampusBluePrimary else Color(0xFFD97706),
                            unfocusedBorderColor = CampusBorder,
                            focusedTextColor = CampusTextPrimary,
                            unfocusedTextColor = CampusTextPrimary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        leadingIcon = {
                            Icon(
                                imageVector = if (isMahasiswa) Icons.Default.Person else Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (isMahasiswa) CampusBluePrimary else Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: Password
                    Text(
                        text = "Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                text = "Masukkan Password",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { performLogin() }),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isMahasiswa) CampusBluePrimary else Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                modifier = Modifier.testTag("btn_toggle_password_visibility")
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Sembunyikan password" else "Tampilkan password",
                                    tint = CampusTextSecondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isMahasiswa) CampusBluePrimary else Color(0xFFD97706),
                            unfocusedBorderColor = CampusBorder,
                            focusedTextColor = CampusTextPrimary,
                            unfocusedTextColor = CampusTextPrimary
                        )
                    )

                    // Error Message banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(10.dp)
                                .testTag("error_login_message"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Tombol Masuk dengan Warna Kontras Tinggi & Teks Jelas
                    Button(
                        onClick = { performLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_login_masuk"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMahasiswa) CampusBluePrimary else Color(0xFFD97706),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (isMahasiswa) "MASUK SEBAGAI MAHASISWA" else "MASUK SEBAGAI ADMIN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Lupa password? Hanya untuk Mahasiswa (Admin tidak perlu karena akses langsung ke operator database)
                    if (isMahasiswa) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Lupa password?",
                                fontSize = 13.sp,
                                color = CampusBluePrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onNavigateToForgotPassword() }
                                    .padding(8.dp)
                                    .testTag("btn_lupa_password")
                            )
                        }
                    }
                }
            }

            // Bagian petunjuk masuk paling bawah telah dihapus sesuai permintaan
        }
    }
}
