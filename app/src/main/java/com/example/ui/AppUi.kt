package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.*
import com.example.ui.theme.*
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.net.URLEncoder

// Database Ruangan Mapping from index.html
val databaseRuangan = mapOf(
    "Daerah M" to listOf("M-03", "M-04", "M-05", "M-06", "M-07", "M-08", "M-09", "M-10", "M-11", "M-12", "M-13", "M-14 A", "M-14 B"),
    "Daerah L & Surau L" to listOf("L-01", "L-02", "L-04", "L-05", "L-06", "L-07", "L-08", "L-09", "L-10", "Surau L (Lt.1) 01", "Surau L (Lt.1) 02", "Surau L (Lt.1) 03", "Surau L (Lt.1) 04", "Surau L (Lt.1) 05", "Surau L (Lt.1) 06", "Surau L (Lt.2) 01", "Surau L (Lt.2) 02", "Surau L (Lt.2) 03", "Surau L (Lt.2) 04", "Surau L (Lt.2) 05", "Surau L (Lt.2) 06"),
    "Daerah N" to listOf("N-01", "N-02", "N-05", "N-06", "N-07", "N-08", "N-09", "N-10"),
    "Daerah S" to listOf("S-02", "S-03", "S-04", "S-05", "S-06", "S-07", "S-08", "S-09", "S-10"),
    "Daerah R" to listOf("R-03", "R-04", "R-05", "R-06", "R-07", "R-08", "R-09", "R-10", "R-11", "R-12", "R-13", "R-14", "R-15"),
    "Mabna Al-Ghazali" to listOf("Al-Ghazali - 3.08", "Al-Ghazali - 3.09", "Al-Ghazali - 3.10", "Al-Ghazali - 4.01", "Al-Ghazali - 4.02", "Al-Ghazali - 4.03", "Al-Ghazali - 4.04", "Al-Ghazali - 4.05", "Al-Ghazali - 4.06", "Al-Ghazali - 4.07", "Al-Ghazali - 4.08", "Al-Ghazali - 4.09", "Al-Ghazali - 4.10"),
    "Mushalla Al-Ghazali" to listOf("Mushalla Al-Ghazali 01", "Mushalla Al-Ghazali 02", "Mushalla Al-Ghazali 03", "Mushalla Al-Ghazali 04", "Mushalla Al-Ghazali 05", "Mushalla Al-Ghazali 06"),
    "Mabna An-Nawawi" to listOf("An-Nawawi - 3.01", "An-Nawawi - 3.02", "An-Nawawi - 3.03", "An-Nawawi - 3.04", "An-Nawawi - 3.05", "An-Nawawi - 3.06", "An-Nawawi - 3.07", "An-Nawawi - 3.08", "An-Nawawi - 3.09", "An-Nawawi - 3.10", "An-Nawawi - 3.11", "An-Nawawi - 3.12", "An-Nawawi - 4.01", "An-Nawawi - 4.02", "An-Nawawi - 4.03", "An-Nawawi - 4.04", "An-Nawawi - 4.05", "An-Nawawi - 4.06", "An-Nawawi - 4.07", "An-Nawawi - 4.08", "An-Nawawi - 4.09", "An-Nawawi - 4.10", "An-Nawawi - 4.11", "An-Nawawi - 4.12"),
    "Barat An-Nawawi" to listOf("Barat An-Nawawi (Semua)"),
    "Ar-Raudhah 1" to listOf("Ar-Raudhah 1 (Semua)"),
    "Ar-Raudhah 2" to listOf("Ar-Raudhah 2 (Semua)")
)

// Abbreviation formater
fun fmtTbl(s: String): String {
    return s.replace("Surau L (Lt.1) ", "SL1-")
        .replace("Surau L (Lt.2) ", "SL2-")
        .replace("Al-Ghazali - ", "Ghz ")
        .replace("An-Nawawi - ", "Nww ")
        .replace("Mushalla Al-Ghazali ", "Mush. ")
        .replace("Barat An-Nawawi (Semua)", "Barat Nww")
        .replace("Ar-Raudhah 1 (Semua)", "Raudhah 1")
        .replace("Ar-Raudhah 2 (Semua)", "Raudhah 2")
}

@Composable
fun AppMainUi(viewModel: AppViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val inspections by viewModel.inspections.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val systemInDark = androidx.compose.foundation.isSystemInDarkTheme()
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { (msg, isSuccess) ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationTabBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) },
                hasUnsyncedItems = inspections.any { !it.isSynced }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top header bar with settings / sync icons
                HeaderControlsRow(
                    onSettingsClick = { showSettingsDialog = true },
                    onSyncClick = { viewModel.syncDataWithCloud() },
                    isSyncing = isLoading,
                    appSettings = appSettings,
                    themeMode = themeMode,
                    onThemeToggle = {
                        val isCurrentlyDark = when (themeMode) {
                            "dark" -> true
                            "light" -> false
                            else -> systemInDark
                        }
                        val nextMode = if (isCurrentlyDark) "light" else "dark"
                        viewModel.setThemeMode(nextMode)
                    }
                )

                // Beautiful green Falak / Clock banner
                FalakClockBanner(viewModel = viewModel)

                // Main body content switching tabs
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .somePadding()
                        .weight(1f)
                ) {
                    when (currentTab) {
                        "inspeksi" -> TabInspeksiScreen(viewModel = viewModel)
                        "statistik" -> TabStatistikScreen(viewModel = viewModel)
                        "murid" -> TabSantriScreen(viewModel = viewModel)
                        "pembimbing" -> TabUstadzScreen(viewModel = viewModel)
                    }
                }
            }

            // Floating WhatsApp Bar (shown on Inspeksi tab if we have entries recorded)
            if (currentTab == "inspeksi" && inspections.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    FloatingWABar(
                        count = inspections.size,
                        onBarClick = { viewModel.setTab("statistik") },
                        onSendClick = { kirimWhatsApp(context, inspections, viewModel.wisDate.value, viewModel.wisClock.value) }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            CircularProgressIndicator(color = GreenPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Menghubungi Cloud...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            currentSettings = appSettings,
            onDismiss = { showSettingsDialog = false }
        )
    }
}

// Helper spacer modifier
fun Modifier.somePadding(): Modifier = this.padding(horizontal = 12.dp, vertical = 6.dp)

@Composable
fun HeaderControlsRow(
    onSettingsClick: () -> Unit,
    onSyncClick: () -> Unit,
    isSyncing: Boolean,
    appSettings: AppSettings?,
    themeMode: String,
    onThemeToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
            Text(
                text = "TIM IT MMU TARBIYAH IDADIYAH",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            )
        }

        val isDarkTheme = when (themeMode) {
            "dark" -> true
            "light" -> false
            else -> androidx.compose.foundation.isSystemInDarkTheme()
        }

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection Status / Quick Sync Button
            IconButton(
                onClick = onSyncClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync Sheet",
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Dark/Light Mode toggle button next to Sync and Settings
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.WbSunny else Icons.Default.NightsStay,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFF5C6BC0),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Settings button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun FalakClockBanner(viewModel: AppViewModel) {
    val clockStr by viewModel.wisClock.collectAsStateWithLifecycle()
    val dateStr by viewModel.wisDate.collectAsStateWithLifecycle()
    val sholatNama by viewModel.nextSholatNama.collectAsStateWithLifecycle()
    val sholatTimer by viewModel.nextSholatTimer.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF113A29), Color(0xFF0A2419))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(1.dp, Color(0xFF1B4D39), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo and Titles (Logo on top, text below)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.05f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_mmu_idadiyah),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(125.dp),
                    contentScale = ContentScale.Fit
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-24).dp)
                ) {
                    Text(
                        text = "SISTEM PENGAWASAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2DD4BF),
                            letterSpacing = 0.5.sp,
                            fontSize = 9.sp
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = "KBM & LOKER SANTRI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp,
                            fontSize = 14.sp
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .height(115.dp)
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // Right: Clock and Sholat Tracker
            Column(
                modifier = Modifier
                    .weight(0.95f)
                    .padding(start = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WAKTU ISTIWAK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = clockStr,
                    fontSize = 25.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Golden pill countdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF052216), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sholatNama.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = sholatTimer,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabInspeksiScreen(viewModel: AppViewModel) {
    val selectedD by viewModel.selectedDaerah.collectAsStateWithLifecycle()
    val isLocked by viewModel.isPosLocked.collectAsStateWithLifecycle()
    val lockedD by viewModel.lockedDaerah.collectAsStateWithLifecycle()

    val selectedK by viewModel.selectedKelas.collectAsStateWithLifecycle()
    val telatVal by viewModel.telatMenit.collectAsStateWithLifecycle()
    val isGuruActive by viewModel.guruAktif.collectAsStateWithLifecycle()
    val isStudentActive by viewModel.muridAktif.collectAsStateWithLifecycle()
    val condVal by viewModel.kekondusifan.collectAsStateWithLifecycle()
    val rapVal by viewModel.kerapian.collectAsStateWithLifecycle()
    val notesVal by viewModel.catatan.collectAsStateWithLifecycle()

    val inspections by viewModel.inspections.collectAsStateWithLifecycle()

    val activeDaerah = if (isLocked) lockedD else selectedD
    val classroomList = databaseRuangan[activeDaerah] ?: emptyList()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp) // Leave space for floating bottom bars
    ) {
        // 1. Wilayah Kontrol Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. WILAYAH KONTROL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = GreenPrimary,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (!isLocked) {
                    var expandedDropdown by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedD.ifBlank { "-- PILIH POS / DAERAH --" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("daerah_dropdown"),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            databaseRuangan.keys.forEach { pos ->
                                DropdownMenuItem(
                                    text = { Text(pos, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        viewModel.selectedDaerah.value = pos
                                        viewModel.selectedKelas.value = ""
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    val posBg = if (MaterialTheme.colorScheme.background == LightBackground) Color(0xFFECFDF5) else Color(0xFF064E3B)
                    val posBorder = if (MaterialTheme.colorScheme.background == LightBackground) Color(0xFF34D399) else Color(0xFF059669)
                    val posTextColor = if (MaterialTheme.colorScheme.background == LightBackground) Color.DarkGray else Color.White
                    val posBtnBg = if (MaterialTheme.colorScheme.background == LightBackground) Color.White else Color(0xFF0F172A)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(posBg, RoundedCornerShape(12.dp))
                            .border(1.dp, posBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "POS AKTIF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (MaterialTheme.colorScheme.background == LightBackground) GreenPrimary else Color(0xFF34D399),
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = lockedD,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = posTextColor
                                )
                            )
                        }
                        Button(
                            onClick = { viewModel.unlockPos() },
                            colors = ButtonDefaults.buttonColors(containerColor = posBtnBg),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, posBorder),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Ganti Pos",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (MaterialTheme.colorScheme.background == LightBackground) GreenPrimary else Color(0xFF34D399)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. KETUK RUANG KELAS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. KETUK RUANG KELAS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = GreenPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "${classroomList.size} ruang",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (classroomList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                2.dp,
                                Brush.linearGradient(listOf(Color(0xFF34D399), Color.LightGray)),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color(0xFFECFDF5).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Silakan pilih Pos/Daerah di atas",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Display classroom grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp) // Avoid infinite grid nesting
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(classroomList) { r ->
                                val isRecorded = inspections.any { it.kelas == r && it.daerah == activeDaerah }
                                val isSelected = r == selectedK

                                val chipBg by animateColorAsState(
                                    targetValue = when {
                                        isSelected -> GreenPrimary
                                        isRecorded -> Color(0xFFECFDF5)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                val chipBorderColor = when {
                                    isSelected -> GreenPrimary
                                    isRecorded -> Color(0xFF34D399)
                                    else -> Color.LightGray.copy(alpha = 0.5f)
                                }
                                val chipTextColor = when {
                                    isSelected -> Color.White
                                    isRecorded -> if (MaterialTheme.colorScheme.background == LightBackground) GreenPrimary else Color(0xFF34D399)
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(chipBg)
                                        .border(1.dp, chipBorderColor, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.selectedKelas.value = r }
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isRecorded) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Recorded",
                                                tint = GreenPrimary,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(end = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = fmtTbl(r),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isRecorded || isSelected) FontWeight.Black else FontWeight.Bold,
                                                color = chipTextColor,
                                                fontSize = 11.sp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Form Card (KBM controls)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // KETERLAMBATAN GURU
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KETERLAMBATAN GURU",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.Gray
                        )
                    )

                    val badgeColor = if (telatVal == 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                    val badgeTextColor = if (telatVal == 0) Color(0xFF065F46) else Color(0xFF991B1B)
                    val badgeBorderColor = if (telatVal == 0) Color(0xFF34D399) else Color(0xFFF87171)
                    val badgeText = if (telatVal == 0) "✓ TEPAT WAKTU" else "⚠️ TELAT ${telatVal}m"

                    Box(
                        modifier = Modifier
                            .background(badgeColor, RoundedCornerShape(20.dp))
                            .border(1.dp, badgeBorderColor, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = badgeTextColor,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Presets input row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = telatVal.toString(),
                        onValueChange = {
                            val v = it.toIntOrNull() ?: 0
                            viewModel.telatMenit.value = v
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(56.dp)
                            .height(42.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )

                    Text(
                        text = "Mnt",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Presets
                    listOf(0, 5, 10, 15).forEach { m ->
                        val isPresetSel = telatVal == m
                        val btnBg = if (isPresetSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        val btnTextColor = if (isPresetSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        val btnBorderColor = if (isPresetSel) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(btnBg)
                                .border(1.dp, btnBorderColor, RoundedCornerShape(8.dp))
                                .clickable { viewModel.telatMenit.value = m }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${m}m",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = btnTextColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // VERIFIKASI KBM
                Text(
                    text = "VERIFIKASI KBM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Guru check card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isGuruActive) Color(0xFFECFDF5) else Color(0xFFF8FAFC))
                            .border(
                                1.5.dp,
                                if (isGuruActive) Color(0xFF10B981) else Color.LightGray.copy(alpha = 0.5f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.guruAktif.value = !isGuruActive }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Guru",
                                tint = if (isGuruActive) GreenPrimary else Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Guru Aktif",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isGuruActive) GreenPrimary else Color.Gray
                                )
                            )
                        }
                    }

                    // Murid check card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isStudentActive) Color(0xFFECFDF5) else Color(0xFFF8FAFC))
                            .border(
                                1.5.dp,
                                if (isStudentActive) Color(0xFF10B981) else Color.LightGray.copy(alpha = 0.5f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.muridAktif.value = !isStudentActive }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Murid",
                                tint = if (isStudentActive) GreenPrimary else Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Murid Aktif",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isStudentActive) GreenPrimary else Color.Gray
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SUASANA / KEKONDUSIFAN
                Text(
                    text = "SUASANA / KEKONDUSIFAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                SegmentedGroupRow(
                    selectedValue = condVal,
                    options = listOf("Sangat Baik", "Baik", "Cukup", "Kurang"),
                    onSelected = { viewModel.kekondusifan.value = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // KERAPIAN KELAS
                Text(
                    text = "KERAPIAN KELAS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                SegmentedGroupRow(
                    selectedValue = rapVal,
                    options = listOf("Sangat Baik", "Baik", "Cukup", "Kurang"),
                    onSelected = { viewModel.kerapian.value = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CATATAN
                OutlinedTextField(
                    value = notesVal,
                    onValueChange = { viewModel.catatan.value = it },
                    placeholder = { Text("Catatan opsional temuan kelas...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // REKAM RUANGAN BUTTON
                Button(
                    onClick = { viewModel.submitInspection() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_inspection_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Save",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REKAM RUANGAN INI",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SegmentedGroupRow(
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    val containerBg = if (MaterialTheme.colorScheme.background == LightBackground) Color(0xFFE2E8F0) else Color(0xFF1E293B)
    val itemBg = if (MaterialTheme.colorScheme.background == LightBackground) Color.White else Color(0xFF0F172A)
    val unselTextColor = if (MaterialTheme.colorScheme.background == LightBackground) Color.DarkGray else Color(0xFF94A3B8)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBg, RoundedCornerShape(12.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { item ->
            val isSel = item == selectedValue
            val bg by animateColorAsState(targetValue = if (isSel) itemBg else Color.Transparent)
            val textColor = if (isSel) GreenPrimary else unselTextColor

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(bg)
                    .clickable { onSelected(item) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
                        color = textColor,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TabStatistikScreen(viewModel: AppViewModel) {
    val inspections by viewModel.inspections.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Group items by Pos/Daerah
    val groupedInspections = remember(inspections) {
        inspections.groupBy { it.daerah }
    }

    // Calculate metric percentages
    val totalCount = inspections.size
    val metricLatePercent = if (totalCount > 0) {
        (inspections.count { it.telatMenit > 0 } * 100) / totalCount
    } else 0
    val metricGuruPercent = if (totalCount > 0) {
        (inspections.count { it.statusGuru == "Aktif" } * 100) / totalCount
    } else 0
    val metricMuridPercent = if (totalCount > 0) {
        (inspections.count { it.statusMurid == "Aktif" } * 100) / totalCount
    } else 0
    val metricKondusifPercent = if (totalCount > 0) {
        (inspections.count { it.kekondusifan in listOf("Sangat Baik", "Baik") } * 100) / totalCount
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REKAP SESI HARI INI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Text(
                                text = "Statistik Antrean KBM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Riset Otomatis\n24.00 WIS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDA4AF),
                                    fontSize = 8.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metrics grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MetricBlock(icon = "⏱️", pct = "$metricLatePercent%", label = "Telat", color = Color(0xFFF87171), modifier = Modifier.weight(1f))
                        MetricBlock(icon = "👨‍🏫", pct = "$metricGuruPercent%", label = "Guru Aktif", color = Color(0xFF34D399), modifier = Modifier.weight(1f))
                        MetricBlock(icon = "🌟", pct = "$metricKondusifPercent%", label = "Kondusif", color = Color(0xFFFBBF24), modifier = Modifier.weight(1f))
                        MetricBlock(icon = "👥", pct = "$metricMuridPercent%", label = "Murid Aktif", color = Color(0xFF60A5FA), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Action controls
        if (totalCount > 0) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { viewModel.clearAllInspections() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Clear",
                                    tint = Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "KOSONGKAN ANTREAN STATISTIK",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFDC2626)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "*Mengosongkan antrean ini TIDAK akan menghapus data permanen di Google Sheet.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center
                            ),
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        }

        // Group lists title
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "DATA TERLAPORKAN (URUT PER-POS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = GreenPrimary,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        if (groupedInspections.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada kelas direkam pada sesi ini.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    )
                }
            }
        } else {
            groupedInspections.forEach { (daerah, reports) ->
                item {
                    // Pos Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp)
                            .background(Color(0xFFE1F5FE).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFB3E5FC), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Place",
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "POS: ${daerah.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = GreenPrimary,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                items(reports) { item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.kelas,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Text(
                                    text = "Kondisi: ${item.kekondusifan} | Rapi: ${item.kerapian}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (item.catatan.isNotBlank()) {
                                    Text(
                                        text = "Notes: \"${item.catatan}\"",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (item.telatMenit == 0) {
                                    Text(
                                        text = "Tepat Waktu",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF059669)
                                        )
                                    )
                                } else {
                                    Text(
                                        text = "Telat ${item.telatMenit}m",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete Item",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { viewModel.deleteInspection(item.id) }
                                )
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                    }
                }
            }

            // Global WhatsApp send button below group
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { kirimWhatsApp(context, inspections, viewModel.wisDate.value, viewModel.wisClock.value) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp_white),
                            contentDescription = "WA",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KIRIM REKAP KE WHATSAPP",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBlock(
    icon: String,
    pct: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 16.sp)
            Text(
                text = pct,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = color
                )
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun TabSantriScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val query by viewModel.studentSearchQuery.collectAsStateWithLifecycle()
    val result by viewModel.studentResult.collectAsStateWithLifecycle()
    val errMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PENCARIAN PENEMPATAN SANTRI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = GreenPrimary,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.studentSearchQuery.value = it },
                        placeholder = { Text("Ketik IDPPS Santri...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("idpps_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )

                    Button(
                        onClick = { viewModel.searchStudent(query) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("search_student_button")
                    ) {
                        Text(
                            text = "CARI",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scan Barcode Button
                Button(
                    onClick = {
                        launchBarcodeScanner(context) { scanValue ->
                            viewModel.studentSearchQuery.value = scanValue
                            viewModel.searchStudent(scanValue)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5)),
                    border = BorderStroke(2.dp, Brush.linearGradient(listOf(Color(0xFF34D399), Color.LightGray))),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📷 Pindai Barcode Kartu",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = GreenPrimary
                            )
                        )
                    }
                }
            }
        }

        // Error message if any
        errMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️ $it",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    if ((it.contains("tidak ditemukan") && (it.contains("Sheet") || it.contains("URL") || it.contains("Web App"))) || it.contains("sinkronisasi") || it.contains("Koneksi gagal")) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.checkCloudConnection() },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "🔄 Inisialisasi & Buat Sheet",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Result Card
        result?.let { r ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0F172A), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "IDPPS: ${r.idpps}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = GoldAccent,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = r.nama,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD1FAE5), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFF34D399), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = r.kelas,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = GreenPrimaryLight
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PEMBIMBING: ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.Gray
                            )
                        )
                        Text(
                            text = r.pembimbing.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = GreenPrimary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    viewModel.selectPembimbingAndSearch(r.pembimbing)
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pagi/Sore Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Pagi/Sore",
                                tint = Color(0xFFE28743),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KBM PAGI/SORE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF64748B),
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Text(
                            text = r.ruang_pagi,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Malam Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .border(1.dp, GreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = "Malam",
                                tint = GreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KBM MALAM",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = GreenPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Text(
                            text = r.ruang_malam,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF15803D)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "📍 Domisili: " + r.domisili.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TabUstadzScreen(viewModel: AppViewModel) {
    val query by viewModel.pembimbingSearchQuery.collectAsStateWithLifecycle()
    val result by viewModel.pembimbingResult.collectAsStateWithLifecycle()
    val errMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PENCARIAN RUANG PEMBIMBING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = GreenPrimary,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.pembimbingSearchQuery.value = it },
                        placeholder = { Text("Ketik Nama Ustadz...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pembimbing_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )

                    Button(
                        onClick = { viewModel.searchPembimbing(query) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("search_pembimbing_button")
                    ) {
                        Text(
                            text = "CARI",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black)
                        )
                    }
                }
            }
        }

        // Error message if any
        errMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️ $it",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    if ((it.contains("tidak ditemukan") && (it.contains("Sheet") || it.contains("URL") || it.contains("Web App"))) || it.contains("sinkronisasi") || it.contains("Koneksi gagal")) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.checkCloudConnection() },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "🔄 Inisialisasi & Buat Sheet",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Result Card
        result?.let { r ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (r.idpps.isNotBlank() && r.idpps != "-") {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0F172A), RoundedCornerShape(50))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "IDPPS: ${r.idpps}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFBBF24), // Gold yellow
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = r.nama.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = GreenPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (r.guru.isNotBlank() && r.guru != "-") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = r.guru.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309) // Warm brownish gold
                                )
                            )
                        }
                    }

                    if (r.alamat.isNotBlank() && r.alamat != "-") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = r.alamat.uppercase(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pagi/Sore Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Pagi/Sore",
                                tint = Color(0xFFE28743),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KBM PAGI/SORE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF64748B),
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Text(
                            text = r.ruang_pagi,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Malam Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .border(1.dp, GreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = "Malam",
                                tint = GreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KBM MALAM",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = GreenPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                        Text(
                            text = r.ruang_malam,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF15803D)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingWABar(
    count: Int,
    onBarClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF046C4E), Color(0xFF024731))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, Color(0xFF34D399).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onBarClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = GoldAccent
                    )
                )
            }

            Column {
                Text(
                    text = "Antrean Tersimpan",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Text(
                    text = "Buka Tab Statistik ➔",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA7F3D0),
                        fontSize = 10.sp
                    )
                )
            }
        }

        IconButton(
            onClick = onSendClick,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF25D366))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_whatsapp_white),
                contentDescription = "WhatsApp",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationTabBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    hasUnsyncedItems: Boolean
) {
    val barBg = if (MaterialTheme.colorScheme.background == LightBackground) Color(0xFFF1F5F9).copy(alpha = 0.95f) else Color(0xFF0F172A).copy(alpha = 0.95f)
    val barBorder = if (MaterialTheme.colorScheme.background == LightBackground) Color.LightGray.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.15f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(16.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = barBg,
        border = BorderStroke(1.dp, barBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabButton(
                id = "inspeksi",
                label = "Kontrol",
                icon = Icons.Default.Edit,
                isSelected = selectedTab == "inspeksi",
                onClick = { onTabSelected("inspeksi") }
            )
            TabButton(
                id = "statistik",
                label = "Statistik",
                icon = Icons.Default.Info,
                isSelected = selectedTab == "statistik",
                onClick = { onTabSelected("statistik") },
                showBadgeDot = hasUnsyncedItems
            )
            TabButton(
                id = "murid",
                label = "Santri",
                icon = Icons.Default.Person,
                isSelected = selectedTab == "murid",
                onClick = { onTabSelected("murid") }
            )
            TabButton(
                id = "pembimbing",
                label = "Ustadz",
                icon = Icons.Default.Home,
                isSelected = selectedTab == "pembimbing",
                onClick = { onTabSelected("pembimbing") }
            )
        }
    }
}

@Composable
fun TabButton(
    id: String,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    showBadgeDot: Boolean = false
) {
    val context = LocalContext.current
    val bg = if (isSelected) GreenPrimary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .testTag("tab_${id}")
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                if (showBadgeDot) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    color = contentColor,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun SettingsDialog(
    viewModel: AppViewModel,
    currentSettings: AppSettings?,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var webAppUrlText by remember { mutableStateOf(currentSettings?.webAppUrl ?: "") }
    var isSnippetExpanded by remember { mutableStateOf(false) }

    val scriptSnippet = """
// =========================================================================
// API ENDPOINT FOR ANDROID APP (ADD THIS TO THE END OF YOUR CODE.GS)
// =========================================================================
function getSpreadsheet() {
  const spreadsheetId = "1v7wxBvxOmzVuf6LbBcBGc7zmbBkPZxbsDCba4R85Sgk";
  try {
    return SpreadsheetApp.openById(spreadsheetId);
  } catch (err) {
    try {
      const ss = SpreadsheetApp.getActiveSpreadsheet();
      if (ss) return ss;
    } catch (e) {}
    throw new Error("Tidak dapat membuka Spreadsheet. Pastikan ID benar dan hak akses diberikan. Detail: " + err.message);
  }
}

function doGet(e) {
  // If there are no query parameters, render the original index page
  if (!e || !e.parameter || !e.parameter.action) {
    return HtmlService.createTemplateFromFile('Index')
        .evaluate()
        .setTitle('Loker & Inspeksi KBM - Sidogiri')
        .addMetaTag('viewport', 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no');
  }
  
  const action = e.parameter.action;
  let result = {};
  
  try {
    if (action === 'sinkron') {
      result = sinkronisasiDatabase();
    } else if (action === 'cariSantri') {
      const id = e.parameter.id;
      result = cariDataSantri(id);
    } else if (action === 'cariPembimbing') {
      const nama = e.parameter.nama;
      result = cariDataPembimbing(nama);
    } else if (action === 'simpan') {
      const data = {
        daerah: e.parameter.daerah,
        kelas: e.parameter.kelas,
        telatMenit: parseInt(e.parameter.telatMenit || "0"),
        guruAktif: e.parameter.guruAktif === 'true',
        muridAktif: e.parameter.muridAktif === 'true',
        kekondusifan: e.parameter.kekondusifan,
        kerapian: e.parameter.kerapian,
        catatan: e.parameter.catatan || ""
      };
      result = simpanDataKeSheet(data);
    } else {
      result = { error: true, pesan: "Aksi '" + action + "' tidak dikenal!" };
    }
  } catch (err) {
    result = { error: true, pesan: "Error API: " + err.message };
  }
  
  return ContentService.createTextOutput(JSON.stringify(result))
      .setMimeType(ContentService.MimeType.JSON);
}

function doPost(e) {
  try {
    const postData = JSON.parse(e.postData.contents);
    const action = postData.action;
    let result = {};
    
    if (action === 'simpan') {
      result = simpanDataKeSheet(postData.data);
    } else {
      result = { error: true, pesan: "Aksi POST tidak dikenal!" };
    }
    
    return ContentService.createTextOutput(JSON.stringify(result))
        .setMimeType(ContentService.MimeType.JSON);
  } catch (err) {
    return ContentService.createTextOutput(JSON.stringify({ error: true, pesan: "Error: " + err.message }))
        .setMimeType(ContentService.MimeType.JSON);
  }
}

// Helper to find the best matching sheet name
function findSheetByNameFuzzy(ss, keyword, defaultName) {
  let sheet = ss.getSheetByName(defaultName);
  if (sheet) return sheet;
  
  const sheets = ss.getSheets();
  const kw = keyword.toLowerCase();
  for (let s of sheets) {
    const sName = s.getName().toLowerCase();
    if (sName.includes(kw)) {
      return s;
    }
  }
  return null;
}

// Helper to find headers dynamically
function getHeadersAndIndex(data) {
  let bestRowIdx = 0;
  let maxMatches = -1;
  
  const scanLimit = Math.min(data.length, 12);
  for (let i = 0; i < scanLimit; i++) {
    const row = data[i];
    let matches = 0;
    for (let col of row) {
      const val = String(col).toLowerCase().trim();
      if (val.includes('nama') || val.includes('id') || val.includes('pembimbing') || 
          val.includes('alamat') || val.includes('pagi') || val.includes('malam') || 
          val.includes('kelas') || val.includes('domisili') || val.includes('guru') || 
          val.includes('pps') || val.includes('kbm')) {
        matches++;
      }
    }
    if (matches > maxMatches) {
      maxMatches = matches;
      bestRowIdx = i;
    }
  }
  
  const headerRow = data[bestRowIdx] || [];
  const cleanHeaders = headerRow.map(h => String(h).trim().toLowerCase());
  return {
    index: bestRowIdx,
    headers: cleanHeaders
  };
}

// =========================================================================
// API HELPER FUNCTIONS FOR GOOGLE SHEETS
// =========================================================================
function sinkronisasiDatabase() {
  const ss = getSpreadsheet();
  
  // Ensure "Data Santri" sheet exists
  let sheetSantri = findSheetByNameFuzzy(ss, "santri", "Data Santri");
  if (!sheetSantri) {
    sheetSantri = ss.insertSheet("Data Santri");
    sheetSantri.appendRow(["IDPPS", "Nama", "Domisili", "Kelas", "Pembimbing", "Ruang Pagi", "Ruang Malam"]);
  }
  
  // Ensure "Data Pembimbing" sheet exists
  let sheetPembimbing = findSheetByNameFuzzy(ss, "pembimbing", "Data Pembimbing");
  if (!sheetPembimbing) {
    sheetPembimbing = ss.insertSheet("Data Pembimbing");
    sheetPembimbing.appendRow(["NO", "ID PPS", "NAMA", "GURU", "ALAMAT", "RUANG KELAS PAGI/SORE", "RUANG KELAS MALAM"]);
  }
  
  // Ensure "Laporan Inspeksi" sheet exists
  let sheetLaporan = findSheetByNameFuzzy(ss, "laporan", "Laporan Inspeksi");
  if (!sheetLaporan) {
    sheetLaporan = ss.insertSheet("Laporan Inspeksi");
    sheetLaporan.appendRow(["Timestamp", "Daerah", "Kelas", "Telat Menit", "Guru Aktif", "Murid Aktif", "Kekondusifan", "Kerapian", "Catatan"]);
  }

  return { error: false, pesan: "Koneksi ke Google Spreadsheet berhasil! Semua sheet siap digunakan." };
}

function cariDataSantri(id) {
  const ss = getSpreadsheet();
  let sheet = findSheetByNameFuzzy(ss, "santri", "Data Santri");
  if (!sheet) {
    return { error: true, pesan: "Sheet berisi data Santri tidak ditemukan. Pastikan ada sheet bernama 'Data Santri'!" };
  }
  
  const data = sheet.getDataRange().getValues();
  if (data.length === 0) {
    return { error: true, pesan: "Sheet Santri kosong!" };
  }
  
  const headerInfo = getHeadersAndIndex(data);
  const headers = headerInfo.headers;
  const headerRowIdx = headerInfo.index;
  
  // Find column indices dynamically
  let idxID = headers.findIndex(h => h === 'idpps' || h.includes('id pps') || h.includes('id') || h.includes('induk'));
  let idxNama = headers.findIndex(h => h.includes('nama') || h.includes('santri'));
  let idxDomisili = headers.findIndex(h => h.includes('domisili') || h.includes('alamat') || h.includes('kamar') || h.includes('daerah') || h.includes('mabna'));
  let idxKelas = headers.findIndex(h => h.includes('kelas') || h.includes('kbm') || h.includes('ruang'));
  let idxPem = headers.findIndex(h => h.includes('pembimbing') || h.includes('guru') || h.includes('pem') || h.includes('ustadz'));
  let idxPagi = headers.findIndex(h => h.includes('pagi') || h.includes('pks'));
  let idxMalam = headers.findIndex(h => h.includes('malam') || h.includes('isya'));
  
  // Fallbacks
  if (idxID === -1) idxID = 0;
  if (idxNama === -1) idxNama = 1;
  if (idxDomisili === -1) idxDomisili = 2;
  if (idxKelas === -1) idxKelas = 3;
  if (idxPem === -1) idxPem = 4;
  if (idxPagi === -1) idxPagi = 5;
  if (idxMalam === -1) idxMalam = 6;
  
  const searchId = String(id).trim().toLowerCase();
  
  for (let i = headerRowIdx + 1; i < data.length; i++) {
    const row = data[i];
    if (row.length === 0) continue;
    
    // Check main id column or fallback to search in all columns
    let isMatch = false;
    if (idxID < row.length && String(row[idxID]).trim().toLowerCase() === searchId) {
      isMatch = true;
    } else {
      // Secondary check: does any column exactly equal the ID?
      for (let cell of row) {
        if (String(cell).trim().toLowerCase() === searchId) {
          isMatch = true;
          break;
        }
      }
    }
    
    if (isMatch) {
      return {
        error: false,
        pesan: "Data ditemukan",
        data: {
          idpps: idxID < row.length ? String(row[idxID]).trim() : searchId,
          nama: idxNama !== -1 && idxNama < row.length && row[idxNama] ? String(row[idxNama]).trim() : (row[1] ? String(row[1]).trim() : "-"),
          domisili: idxDomisili !== -1 && idxDomisili < row.length && row[idxDomisili] ? String(row[idxDomisili]).trim() : (row[2] ? String(row[2]).trim() : "-"),
          kelas: idxKelas !== -1 && idxKelas < row.length && row[idxKelas] ? String(row[idxKelas]).trim() : (row[3] ? String(row[3]).trim() : "-"),
          pembimbing: idxPem !== -1 && idxPem < row.length && row[idxPem] ? String(row[idxPem]).trim() : (row[4] ? String(row[4]).trim() : "-"),
          ruang_pagi: idxPagi !== -1 && idxPagi < row.length && row[idxPagi] ? String(row[idxPagi]).trim() : (row[5] ? String(row[5]).trim() : "-"),
          ruang_malam: idxMalam !== -1 && idxMalam < row.length && row[idxMalam] ? String(row[idxMalam]).trim() : (row[6] ? String(row[6]).trim() : "-")
        }
      };
    }
  }
  
  return { error: true, pesan: "Santri dengan IDPPS '" + id + "' tidak ditemukan! (Pindai " + (data.length - headerRowIdx - 1) + " baris)" };
}

function cariDataPembimbing(nama) {
  const ss = getSpreadsheet();
  let sheet = findSheetByNameFuzzy(ss, "pembimbing", "Data Pembimbing");
  if (!sheet) {
    return { error: true, pesan: "Sheet berisi data Pembimbing tidak ditemukan. Pastikan ada sheet bernama 'Data Pembimbing'!" };
  }
  
  const data = sheet.getDataRange().getValues();
  if (data.length === 0) {
    return { error: true, pesan: "Sheet Pembimbing kosong!" };
  }
  
  const headerInfo = getHeadersAndIndex(data);
  const headers = headerInfo.headers;
  const headerRowIdx = headerInfo.index;
  
  // Find column indices dynamically
  let idxID = headers.findIndex(h => h === 'id pps' || h.includes('idpps') || h.includes('id') || h.includes('induk'));
  let idxNama = headers.findIndex(h => h.includes('nama') || h.includes('pembimbing') || h.includes('guru'));
  let idxGuru = headers.findIndex(h => h === 'guru' || h.includes('jabatan') || h.includes('status') || h.includes('ustadz'));
  let idxAlamat = headers.findIndex(h => h.includes('alamat') || h.includes('domisili') || h.includes('kamar') || h.includes('daerah') || h.includes('mabna'));
  let idxPagi = headers.findIndex(h => h.includes('pagi') || h.includes('pks') || h.includes('ruang'));
  let idxMalam = headers.findIndex(h => h.includes('malam') || h.includes('isya'));
  
  // Fallbacks
  if (idxID === -1) idxID = 1;
  if (idxNama === -1) idxNama = 2;
  if (idxGuru === -1) idxGuru = 3; 
  if (idxAlamat === -1) idxAlamat = 4;
  if (idxPagi === -1) idxPagi = 5;
  if (idxMalam === -1) idxMalam = 6;
  
  const searchKey = String(nama).trim().toLowerCase();
  
  for (let i = headerRowIdx + 1; i < data.length; i++) {
    const row = data[i];
    if (row.length === 0) continue;
    
    let isMatch = false;
    
    // Check dynamic name column first
    if (idxNama !== -1 && idxNama < row.length) {
      const nameStr = row[idxNama] ? String(row[idxNama]).trim() : "";
      if (nameStr.toLowerCase().includes(searchKey)) {
        isMatch = true;
      }
    }
    
    // Fallback: search in all columns if not matched
    if (!isMatch) {
      for (let cell of row) {
        if (String(cell).toLowerCase().includes(searchKey)) {
          isMatch = true;
          break;
        }
      }
    }
    
    if (isMatch) {
      // Find name from row (prefer idxNama, fallback to matching column or column 1)
      let foundName = idxNama !== -1 && idxNama < row.length ? String(row[idxNama]).trim() : "";
      if (!foundName) {
        for (let cell of row) {
          if (String(cell).toLowerCase().includes(searchKey)) {
            foundName = String(cell).trim();
            break;
          }
        }
      }
      if (!foundName && row[1]) foundName = String(row[1]).trim();
      if (!foundName) foundName = nama;
      
      return {
        error: false,
        pesan: "Data ditemukan",
        data: {
          idpps: idxID !== -1 && idxID < row.length && row[idxID] ? String(row[idxID]).trim() : "-",
          nama: foundName,
          guru: idxGuru !== -1 && idxGuru < row.length && row[idxGuru] ? String(row[idxGuru]).trim() : "-",
          alamat: idxAlamat !== -1 && idxAlamat < row.length && row[idxAlamat] ? String(row[idxAlamat]).trim() : (row[2] ? String(row[2]).trim() : "-"),
          ruang_pagi: idxPagi !== -1 && idxPagi < row.length && row[idxPagi] ? String(row[idxPagi]).trim() : (row[3] ? String(row[3]).trim() : "-"),
          ruang_malam: idxMalam !== -1 && idxMalam < row.length && row[idxMalam] ? String(row[idxMalam]).trim() : (row[4] ? String(row[4]).trim() : "-")
        }
      };
    }
  }
  
  return { error: true, pesan: "Pembimbing dengan nama '" + nama + "' tidak ditemukan! (Pindai " + (data.length - headerRowIdx - 1) + " baris)" };
}

function simpanDataKeSheet(data) {
  const ss = getSpreadsheet();
  let sheet = findSheetByNameFuzzy(ss, "laporan", "Laporan Inspeksi");
  if (!sheet) {
    sheet = ss.insertSheet("Laporan Inspeksi");
    sheet.appendRow(["Timestamp", "Daerah", "Kelas", "Telat Menit", "Guru Aktif", "Murid Aktif", "Kekondusifan", "Kerapian", "Catatan"]);
  }
  
  const timestamp = new Date();
  sheet.appendRow([
    timestamp,
    data.daerah,
    data.kelas,
    data.telatMenit,
    data.guruAktif ? "Aktif" : "Tidak Aktif",
    data.muridAktif ? "Aktif" : "Tidak Aktif",
    data.kekondusifan,
    data.kerapian,
    data.catatan
  ]);
  
  return { error: false, pesan: "Laporan berhasil disimpan di Google Sheet!" };
}
"""

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pengaturan Cloud",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = GreenPrimary)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GOOGLE WEB APP URL",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = webAppUrlText,
                    onValueChange = { webAppUrlText = it },
                    placeholder = { Text("https://script.google.com/macros/s/.../exec", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_url_input"),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TEMA APLIKASI",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(6.dp))
                val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
                val themeOptions = listOf("Sistem", "Terang", "Gelap")
                val selectedThemeOption = when (themeMode) {
                    "light" -> "Terang"
                    "dark" -> "Gelap"
                    else -> "Sistem"
                }
                SegmentedGroupRow(
                    selectedValue = selectedThemeOption,
                    options = themeOptions,
                    onSelected = { selectedStr ->
                        val nextMode = when (selectedStr) {
                            "Terang" -> "light"
                            "Gelap" -> "dark"
                            else -> "system"
                        }
                        viewModel.setThemeMode(nextMode)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cek Koneksi
                    Button(
                        onClick = { viewModel.checkCloudConnection() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cek Koneksi", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Simpan
                    Button(
                        onClick = {
                            viewModel.saveSettingsUrl(webAppUrlText)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simpan URL", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.clearCache() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🗑️ Hapus Cache Lokal", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Apps Script deployment guides
                val instructionBg = if (MaterialTheme.colorScheme.background == LightBackground) Color(0xFFECFDF5) else Color(0xFF064E3B)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(instructionBg)
                        .clickable { isSnippetExpanded = !isSnippetExpanded }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instruksi Pemasangan API",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Black, 
                            color = if (MaterialTheme.colorScheme.background == LightBackground) GreenPrimary else Color(0xFF34D399)
                        )
                    )
                    Icon(
                        imageVector = if (isSnippetExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = if (MaterialTheme.colorScheme.background == LightBackground) GreenPrimary else Color(0xFF34D399)
                    )
                }

                AnimatedVisibility(visible = isSnippetExpanded) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            text = "Agar aplikasi Android ini dapat terhubung ke Google Spreadsheet Anda, tambahkan snippet API berikut ke bagian bawah file code.gs di Spreadsheet Anda, lalu lakukan Deploy Ulang sebagai Web App (Anyone can access):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), 
                                lineHeight = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val snippetBoxBg = if (MaterialTheme.colorScheme.background == LightBackground) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(snippetBoxBg, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("code.gs snippet", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(
                                        text = "SALIN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black, 
                                            color = if (MaterialTheme.colorScheme.background == LightBackground) GreenPrimary else Color(0xFF34D399)
                                        ),
                                        modifier = Modifier.clickable {
                                            clipboardManager.setText(AnnotatedString(scriptSnippet))
                                        }
                                    )
                                }
                                Text(
                                    text = scriptSnippet,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 160.dp)
                                        .verticalScroll(rememberScrollState())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Function to start play-services-code-scanner
fun launchBarcodeScanner(context: Context, onResult: (String) -> Unit) {
    try {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let { onResult(it) }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal memindai: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    } catch (e: Exception) {
        Toast.makeText(context, "Sistem scan tidak tersedia di perangkat ini.", Toast.LENGTH_SHORT).show()
    }
}

// WhatsApp redirect builder
fun kirimWhatsApp(
    context: Context,
    inspections: List<InspectionReport>,
    dateStr: String,
    clockStr: String
) {
    if (inspections.isEmpty()) return

    val total = inspections.size
    var telat = 0
    var gAktif = 0
    var mAktif = 0
    var kondusif = 0

    inspections.forEach { d ->
        if (d.telatMenit > 0) telat++
        if (d.statusGuru == "Aktif") gAktif++
        if (d.statusMurid == "Aktif") mAktif++
        if (d.kekondusifan in listOf("Sangat Baik", "Baik")) kondusif++
    }

    val telatPct = (telat * 100) / total
    val guruPct = (gAktif * 100) / total
    val muridPct = (mAktif * 100) / total
    val kondusifPct = (kondusif * 100) / total

    val builder = StringBuilder()
    builder.append("*REKAP KONTROL KBM MMU IDADIYAH*\n")
    builder.append("*Waktu:* $dateStr ($clockStr WIS)\n\n")
    builder.append("*📊 RANGKUMAN STATISTIK SESI*\n")
    builder.append(" • Ketelatan Guru : $telatPct%\n")
    builder.append(" • Kehadiran Guru : $guruPct%\n")
    builder.append(" • Keaktifan Murid: $muridPct%\n")
    builder.append(" • Kekondusifan   : $kondusifPct%\n\n")
    builder.append("*📋 LAPORAN URUT PER-POS*\n")

    val grouped = inspections.groupBy { it.daerah }
    grouped.forEach { (pos, list) ->
        builder.append("\n*=== POS: ${pos.uppercase()} ===*\n")
        list.forEachIndexed { idx, d ->
            val g = if (d.statusGuru == "Aktif") "Aktif" else "Tidak"
            val m = if (d.statusMurid == "Aktif") "Aktif" else "Tidak"
            val tLate = if (d.telatMenit == 0) "Tepat Waktu" else "*Telat ${d.telatMenit}m*"
            
            builder.append("*${idx + 1}. ${d.kelas}* ($tLate)\n")
            builder.append(" • KBM    : Guru ($g) | Murid ($m)\n")
            builder.append(" • Suasana: Kondusif (${d.kekondusifan}) | Rapi (${d.kerapian})\n")
            if (d.catatan.isNotBlank()) {
                builder.append(" • Catatan: _\"${d.catatan}\"_\n")
            }
        }
    }
    builder.append("\n_Total Terekap: $total Ruang Kelas_")

    try {
        val url = "https://api.whatsapp.com/send?text=" + URLEncoder.encode(builder.toString(), "UTF-8")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal meluncurkan WhatsApp: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
