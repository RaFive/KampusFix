package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CreateReportScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportDetailScreen
import com.example.ui.screens.ReportListScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SsoScreen
import com.example.ui.theme.CampusBlueContainer
import com.example.ui.theme.CampusBluePrimary
import com.example.ui.theme.CampusCanvas
import com.example.ui.theme.CampusSurface
import com.example.ui.theme.CampusTextPrimary
import com.example.ui.theme.CampusTextSecondary
import com.example.ui.theme.KampusFixTheme
import com.example.ui.viewmodel.AppThemeMode
import com.example.ui.viewmodel.KampusFixViewModel

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Sso : Screen("sso", "Login")
    object ForgotPassword : Screen("forgot_password", "Lupa Password")
    object Home : Screen("home", "Home")
    object Reports : Screen("reports", "Laporan")
    object Notifications : Screen("notifications", "Notif")
    object CreateReport : Screen("create_report", "Buat Laporan")
    object ReportDetail : Screen("report_detail/{reportId}", "Detail Laporan") {
        fun createRoute(reportId: String) = "report_detail/$reportId"
    }
    object Profile : Screen("profile", "Profil")
    object AdminDashboard : Screen("admin_dashboard", "Dashboard Admin")
}

class MainActivity : ComponentActivity() {

    private val viewModel: KampusFixViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            KampusFixTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val unreadNotifCount by viewModel.unreadNotifCount.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()

                // Bottom bar and student top bar only visible when logged in as Mahasiswa and on student main screens
                val isMahasiswa = currentUser?.role == UserRole.MAHASISWA
                val showBottomNav = isMahasiswa && currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Reports.route,
                    Screen.Notifications.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        if (showBottomNav) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.logo_unib),
                                            contentDescription = "Logo UNIB",
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "KAMPUSFIX",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            letterSpacing = 1.sp,
                                            color = CampusBluePrimary
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { navController.navigate(Screen.Profile.route) },
                                        modifier = Modifier.testTag("btn_top_profil")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Profil Pengguna",
                                            tint = CampusBluePrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = CampusSurface
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(
                                containerColor = CampusSurface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                // Home Tab (Prompt Section 1: 🏠 Home)
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Home.route,
                                    onClick = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentRoute == Screen.Home.route) Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "Home",
                                            fontWeight = if (currentRoute == Screen.Home.route) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CampusBluePrimary,
                                        selectedTextColor = CampusBluePrimary,
                                        indicatorColor = CampusBlueContainer,
                                        unselectedIconColor = CampusTextSecondary,
                                        unselectedTextColor = CampusTextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                // Laporan Tab (Prompt Section 1: 📋 Laporan)
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Reports.route,
                                    onClick = {
                                        navController.navigate(Screen.Reports.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentRoute == Screen.Reports.route) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                            contentDescription = "Laporan"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "Laporan",
                                            fontWeight = if (currentRoute == Screen.Reports.route) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CampusBluePrimary,
                                        selectedTextColor = CampusBluePrimary,
                                        indicatorColor = CampusBlueContainer,
                                        unselectedIconColor = CampusTextSecondary,
                                        unselectedTextColor = CampusTextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_laporan")
                                )

                                // Notifikasi Tab (Prompt Section 1: 🔔 Notif)
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Notifications.route,
                                    onClick = {
                                        navController.navigate(Screen.Notifications.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (unreadNotifCount > 0) {
                                                    Badge(containerColor = Color(0xFFDC2626)) {
                                                        Text(text = "$unreadNotifCount")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (currentRoute == Screen.Notifications.route) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                                contentDescription = "Notif"
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "Notif",
                                            fontWeight = if (currentRoute == Screen.Notifications.route) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CampusBluePrimary,
                                        selectedTextColor = CampusBluePrimary,
                                        indicatorColor = CampusBlueContainer,
                                        unselectedIconColor = CampusTextSecondary,
                                        unselectedTextColor = CampusTextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_notif")
                                )
                            }
                        }
                    },
                    containerColor = CampusCanvas
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() }
                    ) {
                        // Splash Screen (mulai aplikasi)
                        composable(Screen.Splash.route) {
                            SplashScreen(
                                onTimeout = {
                                    val destination = if (currentUser == null) {
                                        Screen.Login.route
                                    } else if (currentUser?.role == UserRole.ADMIN) {
                                        Screen.AdminDashboard.route
                                    } else {
                                        Screen.Home.route
                                    }
                                    navController.navigate(destination) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Login Screen (pilihan Akun Mahasiswa: Sonetto / Akun Admin: Admin R)
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onQuickLogin = { account ->
                                    viewModel.loginAs(account)
                                    val destination = if (account.role == UserRole.ADMIN) {
                                        Screen.AdminDashboard.route
                                    } else {
                                        Screen.Home.route
                                    }
                                    navController.navigate(destination) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToSso = {
                                    navController.navigate(Screen.Sso.route)
                                }
                            )
                        }

                        // Login Form Screen (Mahasiswa / Admin)
                        composable(Screen.Sso.route) {
                            SsoScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLoginSuccess = { role ->
                                    val destination = if (role == UserRole.ADMIN) {
                                        Screen.AdminDashboard.route
                                    } else {
                                        Screen.Home.route
                                    }
                                    navController.navigate(destination) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate(Screen.ForgotPassword.route)
                                }
                            )
                        }

                        // Halaman Baru: Lupa Password (kirim via email)
                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Home Screen (Mahasiswa: Sonetto)
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                onCreateReportClick = {
                                    viewModel.resetDraft()
                                    navController.navigate(Screen.CreateReport.route)
                                },
                                onReportClick = { reportId ->
                                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                                },
                                onViewAllReportsClick = { status ->
                                    viewModel.setStatusFilter(status)
                                    navController.navigate(Screen.Reports.route)
                                },
                                onAdminDashboardClick = {
                                    navController.navigate(Screen.AdminDashboard.route)
                                }
                            )
                        }

                        // Laporan Saya Screen (Mahasiswa: Sonetto)
                        composable(Screen.Reports.route) {
                            ReportListScreen(
                                viewModel = viewModel,
                                onReportClick = { reportId ->
                                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                                }
                            )
                        }

                        // Notifikasi Screen (Mahasiswa: Sonetto)
                        composable(Screen.Notifications.route) {
                            NotificationScreen(
                                viewModel = viewModel,
                                onNotificationClick = { reportId ->
                                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                                }
                            )
                        }

                        // Buat Laporan Screen
                        composable(Screen.CreateReport.route) {
                            CreateReportScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onViewDetail = { reportId ->
                                    navController.navigate(Screen.ReportDetail.createRoute(reportId)) {
                                        popUpTo(Screen.Home.route)
                                    }
                                },
                                onBackToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Detail Laporan Screen
                        composable(
                            route = Screen.ReportDetail.route,
                            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
                            ReportDetailScreen(
                                reportId = reportId,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Profil Screen (Sonetto - G1Z030099)
                        composable(Screen.Profile.route) {
                            ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToAdminDashboard = {
                                    navController.navigate(Screen.AdminDashboard.route)
                                },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Halaman Khusus Admin (Admin R)
                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
