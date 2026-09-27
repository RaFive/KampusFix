package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CampusData
import com.example.data.model.FacilityCategory
import com.example.data.model.FacilityReport
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
import java.io.File
import java.io.FileOutputStream

enum class ReportStep {
    FORM,
    CONFIRMATION,
    SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateReportScreen(
    viewModel: KampusFixViewModel,
    onNavigateBack: () -> Unit,
    onViewDetail: (String) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(ReportStep.FORM) }
    var submittedReport by remember { mutableStateOf<FacilityReport?>(null) }
    val context = LocalContext.current

    val selectedCategory by viewModel.draftCategory.collectAsState()
    val selectedBuilding by viewModel.draftBuilding.collectAsState()
    val selectedRoom by viewModel.draftRoom.collectAsState()
    val photoUri by viewModel.draftPhotoUri.collectAsState()
    val title by viewModel.draftTitle.collectAsState()
    val description by viewModel.draftDescription.collectAsState()

    var validationError by remember { mutableStateOf<String?>(null) }
    var attemptedSubmit by remember { mutableStateOf(false) }

    // Photo picker launcher (Galeri)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateDraftPhoto(uri.toString())
        }
    }

    // Camera photo capture launcher (Kamera langsung)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val file = File(context.cacheDir, "camera_bukti_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                viewModel.updateDraftPhoto(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentStep != ReportStep.SUCCESS) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentStep) {
                                ReportStep.FORM -> "Buat Laporan"
                                ReportStep.CONFIRMATION -> "Konfirmasi Laporan"
                                else -> ""
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = CampusTextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (currentStep == ReportStep.CONFIRMATION) {
                                    currentStep = ReportStep.FORM
                                } else {
                                    onNavigateBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = CampusTextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CampusSurface)
                )
            }
        },
        containerColor = CampusCanvas,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                },
                label = "reportStepAnimation"
            ) { step ->
                when (step) {
                    ReportStep.FORM -> {
                        FormStepContent(
                            category = selectedCategory,
                            onCategorySelect = { cat ->
                                viewModel.updateDraftCategory(cat)
                                validationError = null
                            },
                            building = selectedBuilding,
                            onBuildingSelect = { bldg ->
                                viewModel.updateDraftBuilding(bldg)
                                validationError = null
                            },
                            room = selectedRoom,
                            onRoomSelect = { rm ->
                                viewModel.updateDraftRoom(rm)
                                validationError = null
                            },
                            photoUri = photoUri,
                            onPickPhotoFromGallery = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onTakePhotoFromCamera = {
                                cameraLauncher.launch(null)
                            },
                            onRemovePhoto = { viewModel.updateDraftPhoto(null) },
                            title = title,
                            onTitleChange = {
                                viewModel.updateDraftTitle(it)
                                validationError = null
                            },
                            description = description,
                            onDescriptionChange = {
                                viewModel.updateDraftDescription(it)
                                validationError = null
                            },
                            attemptedSubmit = attemptedSubmit,
                            validationError = validationError,
                            onNext = {
                                attemptedSubmit = true
                                when {
                                    selectedCategory == null -> {
                                        validationError = "Silakan pilih Kategori Masalah terlebih dahulu."
                                    }
                                    selectedBuilding.isBlank() -> {
                                        validationError = "Silakan pilih Gedung lokasi fasilitas."
                                    }
                                    selectedRoom.isBlank() -> {
                                        validationError = "Silakan pilih Ruangan / Posisi fasilitas."
                                    }
                                    title.trim().isBlank() -> {
                                        validationError = "Judul laporan wajib diisi."
                                    }
                                    description.trim().isBlank() -> {
                                        validationError = "Deskripsi masalah wajib diisi."
                                    }
                                    else -> {
                                        validationError = null
                                        currentStep = ReportStep.CONFIRMATION
                                    }
                                }
                            }
                        )
                    }

                    ReportStep.CONFIRMATION -> {
                        ConfirmationStepContent(
                            category = selectedCategory?.title ?: "Fasilitas",
                            building = selectedBuilding,
                            room = selectedRoom,
                            photoUri = photoUri,
                            title = title,
                            description = description,
                            onConfirmSubmit = {
                                viewModel.submitReport { created ->
                                    submittedReport = created
                                    currentStep = ReportStep.SUCCESS
                                }
                            }
                        )
                    }

                    ReportStep.SUCCESS -> {
                        SuccessStepContent(
                            report = submittedReport,
                            onViewStatus = {
                                submittedReport?.let { onViewDetail(it.id) }
                            },
                            onBackToHome = onBackToHome
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormStepContent(
    category: FacilityCategory?,
    onCategorySelect: (FacilityCategory) -> Unit,
    building: String,
    onBuildingSelect: (String) -> Unit,
    room: String,
    onRoomSelect: (String) -> Unit,
    photoUri: String?,
    onPickPhotoFromGallery: () -> Unit,
    onTakePhotoFromCamera: () -> Unit,
    onRemovePhoto: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    attemptedSubmit: Boolean,
    validationError: String?,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    var buildingExpanded by remember { mutableStateOf(false) }
    var roomExpanded by remember { mutableStateOf(false) }

    val currentRooms = if (building.isNotBlank()) {
        CampusData.buildings.find { it.name == building }?.rooms ?: emptyList()
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Validation Error Notice Banner
        if (validationError != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Peringatan",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = validationError,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }

        // Section: Kategori (Wajib Dipilih)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Kategori Masalah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "*",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
            if (attemptedSubmit && category == null) {
                Text(
                    text = "Pilih salah satu kategori",
                    fontSize = 11.sp,
                    color = Color(0xFFDC2626)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Category pills/grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val allCategories = FacilityCategory.values()
                val chunked = allCategories.toList().chunked(4)

                chunked.forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCategories.forEach { cat ->
                            val isSelected = cat == category
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onCategorySelect(cat) }
                                    .testTag("cat_${cat.name.lowercase()}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CampusBluePrimary else CampusSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = if (isSelected) null else CardDefaults.outlinedCardBorder()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(cat.title),
                                        contentDescription = cat.title,
                                        tint = if (isSelected) Color.White else CampusBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cat.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else CampusTextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Lokasi (Gedung & Ruang - Wajib Dipilih)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lokasi Fasilitas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "*",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Gedung selector
            Box(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { buildingExpanded = true },
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    border = if (attemptedSubmit && building.isBlank()) {
                        CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626)))
                    } else CardDefaults.outlinedCardBorder(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (building.isNotBlank()) CampusBluePrimary else CampusTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Gedung",
                                    fontSize = 11.sp,
                                    color = CampusTextSecondary
                                )
                                Text(
                                    text = if (building.isNotBlank()) building else "Pilih Gedung",
                                    fontSize = 14.sp,
                                    fontWeight = if (building.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (building.isNotBlank()) CampusTextPrimary else CampusTextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Pilih Gedung",
                            tint = CampusTextSecondary
                        )
                    }
                }

                DropdownMenu(
                    expanded = buildingExpanded,
                    onDismissRequest = { buildingExpanded = false },
                    modifier = Modifier.background(CampusSurface)
                ) {
                    CampusData.buildings.forEach { bldg ->
                        DropdownMenuItem(
                            text = { Text(bldg.name, color = CampusTextPrimary) },
                            onClick = {
                                onBuildingSelect(bldg.name)
                                buildingExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ruangan selector
            Box(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (building.isNotBlank()) {
                                roomExpanded = true
                            } else {
                                buildingExpanded = true
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    border = if (attemptedSubmit && room.isBlank()) {
                        CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626)))
                    } else CardDefaults.outlinedCardBorder(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Ruangan / Posisi",
                                fontSize = 11.sp,
                                color = CampusTextSecondary
                            )
                            Text(
                                text = if (room.isNotBlank()) room else if (building.isBlank()) "Pilih gedung dahulu" else "Pilih Ruangan",
                                fontSize = 14.sp,
                                fontWeight = if (room.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (room.isNotBlank()) CampusTextPrimary else CampusTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Pilih Ruangan",
                            tint = CampusTextSecondary
                        )
                    }
                }

                if (currentRooms.isNotEmpty()) {
                    DropdownMenu(
                        expanded = roomExpanded,
                        onDismissRequest = { roomExpanded = false },
                        modifier = Modifier.background(CampusSurface)
                    ) {
                        currentRooms.forEach { rm ->
                            DropdownMenuItem(
                                text = { Text(rm, color = CampusTextPrimary) },
                                onClick = {
                                    onRoomSelect(rm)
                                    roomExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Section: Upload Foto (Kamera & Galeri)
        Column {
            Text(
                text = "Foto Bukti Kerusakan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CampusTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (photoUri != null) {
                // Photo Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (photoUri == "sample_ac") {
                        Image(
                            painter = painterResource(id = R.drawable.sample_ac_unit),
                            contentDescription = "Foto AC",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Foto Bukti",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Remove photo button
                    IconButton(
                        onClick = onRemovePhoto,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus Foto",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // Photo Upload Card (Ambil Kamera & Pilih Galeri)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CampusSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CampusBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = CampusBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Tambahkan Foto Bukti",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CampusTextPrimary
                        )

                        Text(
                            text = "Ambil foto langsung dengan kamera atau dari galeri",
                            fontSize = 12.sp,
                            color = CampusTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onTakePhotoFromCamera,
                                colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_ambil_kamera")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ambil Foto", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onPickPhotoFromGallery,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_pilih_galeri")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Galeri", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: Judul Laporan (Wajib Diisi)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Judul Laporan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "*",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Contoh: AC Ruang 203 Rusak", color = CampusTextSecondary) },
                isError = attemptedSubmit && title.trim().isBlank(),
                supportingText = if (attemptedSubmit && title.trim().isBlank()) {
                    { Text("Judul laporan wajib diisi", color = Color(0xFFDC2626), fontSize = 11.sp) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_judul_laporan"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CampusSurface,
                    unfocusedContainerColor = CampusSurface,
                    focusedBorderColor = CampusBluePrimary,
                    unfocusedBorderColor = CampusBorder,
                    errorBorderColor = Color(0xFFDC2626)
                ),
                singleLine = true
            )
        }

        // Section: Deskripsi (Wajib Diisi)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Deskripsi Masalah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CampusTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "*",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = { Text("Jelaskan detail kerusakan fasilitas yang Anda temukan...", color = CampusTextSecondary) },
                isError = attemptedSubmit && description.trim().isBlank(),
                supportingText = if (attemptedSubmit && description.trim().isBlank()) {
                    { Text("Deskripsi masalah wajib diisi", color = Color(0xFFDC2626), fontSize = 11.sp) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("input_deskripsi_laporan"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CampusSurface,
                    unfocusedContainerColor = CampusSurface,
                    focusedBorderColor = CampusBluePrimary,
                    unfocusedBorderColor = CampusBorder,
                    errorBorderColor = Color(0xFFDC2626)
                ),
                maxLines = 4
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Next to Confirmation Button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_lanjut_konfirmasi"),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "LANJUTKAN KE KONFIRMASI",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConfirmationStepContent(
    category: String,
    building: String,
    room: String,
    photoUri: String?,
    title: String,
    description: String,
    onConfirmSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Periksa kembali laporan fasilitas Anda sebelum diteruskan ke unit Sarpras kampus.",
            fontSize = 13.sp,
            color = CampusTextSecondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CampusSurface),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Photo
                if (photoUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        if (photoUri == "sample_ac") {
                            Image(
                                painter = painterResource(id = R.drawable.sample_ac_unit),
                                contentDescription = "Bukti Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Bukti Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Judul
                Column {
                    Text(
                        text = "Judul Laporan",
                        fontSize = 11.sp,
                        color = CampusTextSecondary
                    )
                    Text(
                        text = title.ifBlank { "Laporan Fasilitas Rusak" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextPrimary
                    )
                }

                // Lokasi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = CampusBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = building,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CampusTextPrimary
                        )
                        Text(
                            text = room,
                            fontSize = 13.sp,
                            color = CampusTextSecondary
                        )
                    }
                }

                // Kategori
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CampusBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(category),
                            contentDescription = null,
                            tint = CampusBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Kategori",
                            fontSize = 11.sp,
                            color = CampusTextSecondary
                        )
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CampusTextPrimary
                        )
                    }
                }

                // Deskripsi
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CampusSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Deskripsi:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CampusTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${description.ifBlank { "Tidak ada catatan tambahan." }}\"",
                        fontSize = 13.sp,
                        color = CampusTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Confirm Send Button
        Button(
            onClick = onConfirmSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_kirim_sekarang"),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "KIRIM SEKARANG",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun SuccessStepContent(
    report: FacilityReport?,
    onViewStatus: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Big Animated check icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFFD1FAE5)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(StatusCompleted),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Laporan Berhasil\nDikirim!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CampusTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Laporan Anda telah tercatat dan masuk ke antrean penanganan bagian Sarana & Prasarana.",
            fontSize = 13.sp,
            color = CampusTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nomor Laporan Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CampusSurface),
            shape = RoundedCornerShape(14.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nomor Laporan",
                    fontSize = 12.sp,
                    color = CampusTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "#${report?.id ?: "KP-00125"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CampusBluePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Action Buttons
        Button(
            onClick = onViewStatus,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_lihat_status"),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBluePrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "LIHAT STATUS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_kembali_ke_home"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusTextPrimary)
        ) {
            Text(
                text = "KEMBALI KE HOME",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
