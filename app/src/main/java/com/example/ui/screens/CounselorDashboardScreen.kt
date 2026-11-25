package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.AlertNotifier
import com.example.data.model.AiPriority
import com.example.data.model.BullyingReport
import com.example.data.model.CounselingSession
import com.example.data.model.EmergencyIdentityLog
import com.example.data.model.HumanDecision
import com.example.data.model.ReportStatus
import com.example.data.model.SosAlert
import com.example.data.model.StaffSubRole
import com.example.data.model.StudentMaster
import com.example.data.model.normalizeClassToken
import com.example.ui.components.AI_TRIAGE_DISCLAIMER
import com.example.ui.components.CategoryDistributionChart
import com.example.ui.components.CounselorAvatar
import com.example.ui.components.FourStageStepper
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SchoolAreaHotspotMap
import com.example.ui.components.StatusBadge
import com.example.ui.components.TrendBarChart
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.LavenderSoft
import com.example.ui.theme.MintPrimary
import com.example.ui.theme.MintSoft
import com.example.ui.theme.NavySidebar
import com.example.ui.theme.NavySidebarSurface
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueSoft
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.UiState

enum class CounselorMenu(val label: String, val icon: ImageVector) {
    BERANDA("Beranda", Icons.Filled.Home),
    LAPORAN("Laporan Kasus", Icons.Filled.Description),
    KONSELING("Konseling", Icons.Filled.CalendarToday),
    DATA_SISWA("Data Siswa (Kode)", Icons.Filled.Badge),
    SISWA("Siswa Terkait", Icons.Filled.Groups),
    INSIGHT_AI("Insight AI", Icons.Filled.AutoAwesome),
    PENGATURAN("Pengaturan", Icons.Filled.Settings),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CounselorDashboardScreen(
    isVerifiedStaff: Boolean,
    staffSubRole: StaffSubRole,
    assignedStaffClass: String,
    soundAlertsEnabled: Boolean,
    reportsState: UiState<List<BullyingReport>>,
    sosAlertsState: UiState<List<SosAlert>>,
    counselingState: UiState<List<CounselingSession>>,
    studentsState: UiState<List<StudentMaster>>,
    emergencyLogsState: UiState<List<EmergencyIdentityLog>>,
    preventionInsight: String?,
    isGeneratingInsight: Boolean,
    insightError: String?,
    statusBanner: String?,
    onClearBanner: () -> Unit,
    onSwitchToStudent: () -> Unit,
    onSelectStaffSubRole: (StaffSubRole) -> Unit,
    onSelectAssignedClass: (String) -> Unit,
    onToggleSoundAlerts: (Boolean) -> Unit,
    onResolveSos: (String, String) -> Unit,
    onSaveHumanDecision: (
        BullyingReport, ReportStatus, AiPriority, HumanDecision,
        String, String, String, Boolean, String
    ) -> Unit,
    onRerunAiTriage: (BullyingReport) -> Unit,
    onAdvanceCounselingStage: (CounselingSession) -> Unit,
    onGenerateInsight: (List<BullyingReport>) -> Unit,
    onAddStudentManual: (String, String, String, String) -> Unit,
    onImportStudentsCsv: (String) -> Unit,
    onRevealEmergencyIdentity: (BullyingReport, String) -> Unit,
) {
    var selectedMenu by remember { mutableStateOf(CounselorMenu.BERANDA) }
    var selectedReport by remember { mutableStateOf<BullyingReport?>(null) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var sosRevealTarget by remember { mutableStateOf<SosAlert?>(null) }
    var sosRevealedIdentity by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (selectedMenu != CounselorMenu.BERANDA) {
        BackHandler { selectedMenu = CounselorMenu.BERANDA }
    }

    LaunchedEffect(statusBanner) {
        if (!statusBanner.isNullOrBlank()) {
            snackbarHostState.showSnackbar(statusBanner)
            onClearBanner()
        }
    }

    val allReports = (reportsState as? UiState.Success)?.data ?: emptyList()
    val allSos = (sosAlertsState as? UiState.Success)?.data ?: emptyList()
    val allSessions = (counselingState as? UiState.Success)?.data ?: emptyList()
    val allStudents = (studentsState as? UiState.Success)?.data ?: emptyList()
    val emergencyLogs = (emergencyLogsState as? UiState.Success)?.data ?: emptyList()

    val isWaliKelas = staffSubRole == StaffSubRole.WALI_KELAS
    val normalizedClass = remember(assignedStaffClass) { normalizeClassToken(assignedStaffClass) }

    // Wali Kelas strictly sees reports, SOS alerts, and sessions scoped to their class
    val scopedReports = remember(allReports, isWaliKelas, normalizedClass) {
        if (!isWaliKelas) allReports
        else allReports.filter {
            normalizeClassToken(it.studentClassCode.ifBlank { it.studentClass }) == normalizedClass
        }
    }
    val scopedReportIds = remember(scopedReports) { scopedReports.map { it.id }.toSet() }
    val scopedSos = remember(allSos, isWaliKelas, normalizedClass) {
        if (!isWaliKelas) allSos
        else allSos.filter { normalizeClassToken(it.studentClassCode) == normalizedClass }
    }
    val scopedSessions = remember(allSessions, isWaliKelas, scopedReportIds, normalizedClass) {
        if (!isWaliKelas) allSessions
        else allSessions.filter {
            it.reportId in scopedReportIds || it.studentLabel.contains(normalizedClass, ignoreCase = true)
        }
    }
    val activeSos = scopedSos.filter { it.status == "AKTIF" }

    val availableMenus = remember(isWaliKelas) {
        if (isWaliKelas) {
            CounselorMenu.entries.filter { it != CounselorMenu.DATA_SISWA }
        } else {
            CounselorMenu.entries
        }
    }
    LaunchedEffect(isWaliKelas, selectedMenu) {
        if (isWaliKelas && selectedMenu == CounselorMenu.DATA_SISWA) {
            selectedMenu = CounselorMenu.BERANDA
        }
    }

    Scaffold(
        containerColor = SoftBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWide = maxWidth >= 840.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (isWide) {
                    CounselorSidebar(
                        staffSubRole = staffSubRole,
                        assignedStaffClass = normalizedClass,
                        menus = availableMenus,
                        selectedMenu = selectedMenu,
                        onSelectMenu = { selectedMenu = it },
                        onSwitchToStudent = onSwitchToStudent
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .navigationBarsPadding()
                ) {
                    // Top Header Bar
                    Surface(
                        color = CleanWhite,
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.statusBarsPadding()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (!isWide) {
                                        CounselorAvatar(modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    Column {
                                        Text(
                                            text = if (isWaliKelas) {
                                                "TICARE AI • Wali Kelas ($normalizedClass)"
                                            } else {
                                                "TICARE AI • Dashboard Guru BK"
                                            },
                                            style = MaterialTheme.typography.titleLarge,
                                            color = TextPrimary
                                        )
                                    Text(
                                        text = if (isWaliKelas) {
                                            "Laporan & Kode Siswa khusus kelas $normalizedClass • Human-in-the-Loop"
                                        } else {
                                            "Trauma-Informed Counseling & Reporting • Technology with Empathy"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = onSwitchToStudent,
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mode Siswa", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(onClick = { showNotifDialog = true }) {
                                        BadgedBox(
                                            badge = {
                                                val count = activeSos.size + scopedReports.count { it.statusEnum() == ReportStatus.BARU }
                                                if (count > 0) {
                                                    Badge(containerColor = EmergencyRed) {
                                                        Text(count.toString(), color = CleanWhite)
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Notifications,
                                                contentDescription = "Notifikasi dasbor"
                                            )
                                        }
                                    }
                                }
                            }

                            if (isWaliKelas) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Kelas Diampu:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(listOf("8A", "8B", "8C", "9A", "9B")) { cls ->
                                            FilterChip(
                                                selected = normalizedClass == cls,
                                                onClick = { onSelectAssignedClass(cls) },
                                                label = { Text(cls, style = MaterialTheme.typography.labelSmall) }
                                            )
                                        }
                                    }
                                }
                            }

                            if (!isWide) {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(availableMenus) { menu ->
                                        FilterChip(
                                            selected = selectedMenu == menu,
                                            onClick = { selectedMenu = menu },
                                            label = { Text(menu.label) },
                                            leadingIcon = {
                                                Icon(menu.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Main Scrollable Body
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!isVerifiedStaff) {
                            Surface(
                                color = WarningAmberSoft,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Mode Pratinjau Akun: Menampilkan data dari akunmu. Masuk dengan akun Guru BK terverifikasi untuk memantau seluruh laporan sekolah dan database Kode Siswa.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarningAmber,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Emergency SOS Banner
                        if (activeSos.isNotEmpty()) {
                            ActiveSosBanner(
                                alerts = activeSos,
                                canRevealIdentity = !isWaliKelas && isVerifiedStaff,
                                onResolve = onResolveSos,
                                onRequestRevealIdentity = { alert -> sosRevealTarget = alert }
                            )
                        }

                        when (selectedMenu) {
                            CounselorMenu.BERANDA -> {
                                StatSummaryRow(reports = scopedReports)

                                if (isWide) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1.35f)) {
                                            RecentReportsPanel(
                                                reports = scopedReports,
                                                onSelectReport = { selectedReport = it }
                                            )
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            AiInsightAnalyticsPanel(
                                                reports = scopedReports,
                                                preventionInsight = preventionInsight,
                                                isGenerating = isGeneratingInsight,
                                                insightError = insightError,
                                                onGenerateInsight = { onGenerateInsight(scopedReports) }
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            CounselingSchedulePanel(
                                                sessions = scopedSessions,
                                                onAdvanceStage = onAdvanceCounselingStage
                                            )
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            FollowUpProgressCard(reports = scopedReports)
                                        }
                                    }
                                } else {
                                    RecentReportsPanel(
                                        reports = scopedReports,
                                        onSelectReport = { selectedReport = it }
                                    )
                                    AiInsightAnalyticsPanel(
                                        reports = scopedReports,
                                        preventionInsight = preventionInsight,
                                        isGenerating = isGeneratingInsight,
                                        insightError = insightError,
                                        onGenerateInsight = { onGenerateInsight(scopedReports) }
                                    )
                                    CounselingSchedulePanel(
                                        sessions = scopedSessions,
                                        onAdvanceStage = onAdvanceCounselingStage
                                    )
                                    FollowUpProgressCard(reports = scopedReports)
                                }
                            }

                            CounselorMenu.LAPORAN -> {
                                StatSummaryRow(reports = scopedReports)
                                RecentReportsPanel(
                                    reports = scopedReports,
                                    onSelectReport = { selectedReport = it }
                                )
                            }

                            CounselorMenu.KONSELING -> {
                                CounselingSchedulePanel(
                                    sessions = scopedSessions,
                                    onAdvanceStage = onAdvanceCounselingStage
                                )
                                FollowUpProgressCard(reports = scopedReports)
                            }

                            CounselorMenu.DATA_SISWA -> {
                                StudentMasterManagementPanel(
                                    isVerifiedStaff = isVerifiedStaff,
                                    students = allStudents,
                                    emergencyLogs = emergencyLogs,
                                    onAddStudentManual = onAddStudentManual,
                                    onImportStudentsCsv = onImportStudentsCsv
                                )
                            }

                            CounselorMenu.SISWA -> {
                                RelatedStudentsPanel(
                                    reports = scopedReports,
                                    onSelectReport = { selectedReport = it }
                                )
                            }

                            CounselorMenu.INSIGHT_AI -> {
                                AiInsightAnalyticsPanel(
                                    reports = scopedReports,
                                    preventionInsight = preventionInsight,
                                    isGenerating = isGeneratingInsight,
                                    insightError = insightError,
                                    onGenerateInsight = { onGenerateInsight(scopedReports) }
                                )
                            }

                            CounselorMenu.PENGATURAN -> {
                                CounselorSettingsPanel(
                                    staffSubRole = staffSubRole,
                                    assignedStaffClass = normalizedClass,
                                    soundAlertsEnabled = soundAlertsEnabled,
                                    onSelectStaffSubRole = onSelectStaffSubRole,
                                    onSelectAssignedClass = onSelectAssignedClass,
                                    onToggleSoundAlerts = onToggleSoundAlerts
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val currentSelected = selectedReport
    if (currentSelected != null) {
        val syncedReport = scopedReports.firstOrNull { it.id == currentSelected.id } ?: currentSelected
        ReportHumanDecisionDialog(
            report = syncedReport,
            canEmergencyReveal = !isWaliKelas && isVerifiedStaff,
            onDismiss = { selectedReport = null },
            onRerunAi = { onRerunAiTriage(syncedReport) },
            onRevealEmergencyIdentity = { reason ->
                onRevealEmergencyIdentity(syncedReport, reason)
            },
            onSave = { status, priority, decision, note, bkMsg, waliNotes, createCs, schedule ->
                onSaveHumanDecision(
                    syncedReport, status, priority, decision, note,
                    bkMsg, waliNotes, createCs, schedule
                )
                selectedReport = null
            }
        )
    }

    val currentSosTarget = sosRevealTarget
    if (currentSosTarget != null) {
        var sosReason by remember(currentSosTarget) { mutableStateOf("") }
        var sosReasonErr by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { sosRevealTarget = null },
            title = { Text("Buka Identitas Asli (Darurat SOS)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Kode Pelapor: ${currentSosTarget.studentCode.ifBlank { currentSosTarget.reporterName }}. Pembukaan identitas asli akan dicatat dalam log audit darurat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = sosReason,
                        onValueChange = {
                            sosReason = it
                            sosReasonErr = null
                        },
                        label = { Text("Alasan Darurat (wajib)*") },
                        placeholder = { Text("Contoh: Siswa dalam bahaya fisik, perlu hubungi orang tua") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (sosReasonErr != null) {
                        Text(sosReasonErr.orEmpty(), color = EmergencyRed, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sosReason.trim().length < 5) {
                            sosReasonErr = "Tuliskan alasan darurat minimal 5 karakter."
                            return@Button
                        }
                        val matchedStudent = allStudents.firstOrNull {
                            it.kodeSiswa.equals(currentSosTarget.studentCode, ignoreCase = true)
                        }
                        val matchedReport = allReports.firstOrNull {
                            it.studentCode.equals(currentSosTarget.studentCode, ignoreCase = true) ||
                                it.reporterId == currentSosTarget.reporterId
                        }
                        if (matchedReport != null) {
                            onRevealEmergencyIdentity(matchedReport, sosReason.trim())
                        }
                        sosRevealedIdentity = if (matchedStudent != null) {
                            "${matchedStudent.namaLengkap} • NISN ${matchedStudent.nisn} • Kelas ${matchedStudent.kelas}\nAlasan: ${sosReason.trim()}"
                        } else {
                            "Kode ${currentSosTarget.studentCode.ifBlank { currentSosTarget.reporterName }} belum memiliki data master siswa lokal. Alasan tercatat: ${sosReason.trim()}"
                        }
                        sosRevealTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Buka Identitas")
                }
            },
            dismissButton = {
                TextButton(onClick = { sosRevealTarget = null }) { Text("Batal") }
            }
        )
    }

    if (sosRevealedIdentity != null) {
        AlertDialog(
            onDismissRequest = { sosRevealedIdentity = null },
            title = { Text("Identitas Darurat Terbuka", color = EmergencyRed) },
            text = { Text(sosRevealedIdentity.orEmpty(), style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(onClick = { sosRevealedIdentity = null }) { Text("Tutup") }
            }
        )
    }

    if (showNotifDialog) {
        NotificationsDialog(
            reports = scopedReports,
            sosAlerts = scopedSos,
            onDismiss = { showNotifDialog = false }
        )
    }
}

@Composable
private fun CounselorSidebar(
    staffSubRole: StaffSubRole,
    assignedStaffClass: String,
    menus: List<CounselorMenu>,
    selectedMenu: CounselorMenu,
    onSelectMenu: (CounselorMenu) -> Unit,
    onSwitchToStudent: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(248.dp)
            .fillMaxHeight()
            .background(NavySidebar)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = SkyBluePrimary,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = CleanWhite)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("TICARE AI", style = MaterialTheme.typography.titleMedium, color = CleanWhite)
                    Text("Technology with Empathy", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NavySidebarSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CounselorAvatar(modifier = Modifier.size(42.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(staffSubRole.title, style = MaterialTheme.typography.titleSmall, color = CleanWhite)
                        Text(
                            text = if (staffSubRole == StaffSubRole.WALI_KELAS) {
                                "${staffSubRole.subtitle} • Kelas $assignedStaffClass"
                            } else {
                                staffSubRole.subtitle
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            menus.forEach { menu ->
                val isSelected = selectedMenu == menu
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) SkyBluePrimary.copy(alpha = 0.24f) else Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectMenu(menu) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = menu.icon,
                            contentDescription = menu.label,
                            tint = if (isSelected) Color(0xFF7DD3FC) else Color(0xFFCBD5E1),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = menu.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) CleanWhite else Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onSwitchToStudent,
            border = BorderStroke(1.dp, Color(0xFF475569)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CleanWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tampilan Siswa")
        }
    }
}

@Composable
private fun ActiveSosBanner(
    alerts: List<SosAlert>,
    canRevealIdentity: Boolean,
    onResolve: (String, String) -> Unit,
    onRequestRevealIdentity: (SosAlert) -> Unit
) {
    val context = LocalContext.current
    val topAlert = alerts.first()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmergencyRed),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_alarm_banner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Alarm SOS",
                        tint = CleanWhite,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ALARM SOS DARURAT (${alerts.size} Aktif)",
                            style = MaterialTheme.typography.titleMedium,
                            color = CleanWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${topAlert.studentCode.ifBlank { topAlert.reporterName }} • ${topAlert.locationLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CleanWhite.copy(alpha = 0.92f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val url = "https://www.google.com/maps/search/?api=1&query=${topAlert.latitude},${topAlert.longitude}"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CleanWhite,
                        contentColor = EmergencyRed
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buka GPS Maps", style = MaterialTheme.typography.labelSmall)
                }

                if (canRevealIdentity && topAlert.studentCode.isNotBlank()) {
                    Button(
                        onClick = { onRequestRevealIdentity(topAlert) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7F1D1D),
                            contentColor = CleanWhite
                        )
                    ) {
                        Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buka Identitas", style = MaterialTheme.typography.labelSmall)
                    }
                }

                OutlinedButton(
                    onClick = { onResolve(topAlert.id, "DITANGANI") },
                    border = BorderStroke(1.dp, CleanWhite),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CleanWhite)
                ) {
                    Text("Tangani", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun StatSummaryRow(reports: List<BullyingReport>) {
    val total = reports.size
    val high = reports.count { it.priorityEnum() == AiPriority.TINGGI }
    val inProgress = reports.count {
        it.statusEnum() == ReportStatus.DIPROSES || it.statusEnum() == ReportStatus.TINDAK_LANJUT
    }
    val done = reports.count { it.statusEnum() == ReportStatus.SELESAI }

    val stats = listOf(
        Triple("Total Laporan", total.toString(), SkyBluePrimary to SkyBlueSoft),
        Triple("Prioritas Tinggi", high.toString(), EmergencyRed to EmergencyRedSoft),
        Triple("Diproses", inProgress.toString(), WarningAmber to WarningAmberSoft),
        Triple("Selesai", done.toString(), SuccessGreen to SuccessGreenSoft),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { (label, value, colors) ->
            val (fg, bg) = colors
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bg,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (label) {
                                    "Total Laporan" -> Icons.Filled.Description
                                    "Prioritas Tinggi" -> Icons.Filled.Warning
                                    "Diproses" -> Icons.Filled.PendingActions
                                    else -> Icons.Filled.CheckCircle
                                },
                                contentDescription = label,
                                tint = fg,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(value, style = MaterialTheme.typography.headlineSmall, color = fg)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentReportsPanel(
    reports: List<BullyingReport>,
    onSelectReport: (BullyingReport) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Daftar Laporan Terbaru", style = MaterialTheme.typography.titleMedium, color = TextPrimary)

            if (reports.isEmpty()) {
                Text(
                    text = "Belum ada laporan yang masuk untuk cakupan ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                reports.forEach { report ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SoftBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectReport(report) }
                            .testTag("report_item_${report.ticketCode}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "#${report.ticketCode}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    PriorityBadge(report.priorityEnum())
                                }
                                StatusBadge(report.statusEnum())
                            }
                            Text(
                                text = "Pelapor: ${report.displayReporterLabel()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SkyBlueDark
                            )
                            Text(
                                text = "Tujuan: ${report.displayRecipientLabel()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LavenderPrimary
                            )
                            Text(
                                text = "${report.categoryEnum().label} • ${report.locationArea} • ${report.incidentTimeLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = report.aiSummary.ifBlank { report.chronology },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiInsightAnalyticsPanel(
    reports: List<BullyingReport>,
    preventionInsight: String?,
    isGenerating: Boolean,
    insightError: String?,
    onGenerateInsight: () -> Unit
) {
    var isMonthly by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = LavenderPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Insight AI & Analitik", style = MaterialTheme.typography.titleMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = !isMonthly,
                        onClick = { isMonthly = false },
                        label = { Text("Mingguan", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = isMonthly,
                        onClick = { isMonthly = true },
                        label = { Text("Bulanan", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            TrendBarChart(reports = reports, isMonthly = isMonthly)

            HorizontalDivider(color = SlateBorder)

            Text("Distribusi Kategori Perundungan", style = MaterialTheme.typography.titleSmall)
            CategoryDistributionChart(reports = reports)

            HorizontalDivider(color = SlateBorder)

            SchoolAreaHotspotMap(reports = reports)

            HorizontalDivider(color = SlateBorder)

            Button(
                onClick = onGenerateInsight,
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = CleanWhite, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyusun Rekomendasi AI...")
                } else {
                    Icon(Icons.Filled.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analisis Program Pencegahan AI")
                }
            }

            if (!insightError.isNullOrBlank()) {
                Text(insightError, style = MaterialTheme.typography.bodySmall, color = EmergencyRed)
            }

            if (!preventionInsight.isNullOrBlank()) {
                Surface(
                    color = LavenderSoft.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = preventionInsight,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Surface(
                color = SkyBlueSoft.copy(alpha = 0.55f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = AI_TRIAGE_DISCLAIMER,
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBlueDark,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun CounselingSchedulePanel(
    sessions: List<CounselingSession>,
    onAdvanceStage: (CounselingSession) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Jadwal Konseling",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            if (sessions.isEmpty()) {
                Text(
                    text = "Belum ada sesi konseling terjadwal. Buka laporan kasus untuk menjadwalkan konseling.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            } else {
                sessions.forEach { session ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SoftBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${session.scheduleLabel} • #${session.ticketCode}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${session.studentLabel} • ${session.counselorName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                if (session.stageIndex < 3L) {
                                    OutlinedButton(
                                        onClick = { onAdvanceStage(session) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Lanjut Tahap", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            FourStageStepper(currentStage = session.stageIndex.toInt())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowUpProgressCard(reports: List<BullyingReport>) {
    val latestStage = reports.maxOfOrNull { it.stageIndex.toInt() } ?: 0
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tindak Lanjut Penanganan",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            FourStageStepper(currentStage = latestStage)
            Text(
                text = if (reports.isEmpty()) {
                    "Tahap pelacakan otomatis aktif setelah laporan pertama diterima."
                } else {
                    "${reports.count { it.statusEnum() == ReportStatus.SELESAI }} dari ${reports.size} kasus telah mencapai tahap Selesai."
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StudentMasterManagementPanel(
    isVerifiedStaff: Boolean,
    students: List<StudentMaster>,
    emergencyLogs: List<EmergencyIdentityLog>,
    onAddStudentManual: (String, String, String, String) -> Unit,
    onImportStudentsCsv: (String) -> Unit
) {
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }
    var unmaskNames by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val content = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.getOrNull()
            if (!content.isNullOrBlank()) {
                onImportStudentsCsv(content)
            }
        }
    }

    val filtered = remember(students, search) {
        val q = search.trim()
        if (q.isBlank()) students
        else students.filter {
            it.kodeSiswa.contains(q, ignoreCase = true) ||
                it.kelas.contains(q, ignoreCase = true)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CleanWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Database Master Siswa & Kode Otomatis", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Nama asli siswa disensor secara bawaan (${students.size} siswa terdaftar).",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { unmaskNames = !unmaskNames },
                        modifier = Modifier.testTag("toggle_unmask_students")
                    ) {
                        Icon(
                            imageVector = if (unmaskNames) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (unmaskNames) "Sensor Nama" else "Tampilkan Nama Asli",
                            tint = LavenderPrimary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        enabled = isVerifiedStaff,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_student_manual_button")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Siswa", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = {
                            csvLauncher.launch(
                                arrayOf("text/csv", "text/comma-separated-values", "text/plain", "*/*")
                            )
                        },
                        enabled = isVerifiedStaff,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("import_csv_button")
                    ) {
                        Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Impor CSV", style = MaterialTheme.typography.labelMedium)
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Cari Kode Siswa (SW-8B-014) atau Kelas") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (filtered.isEmpty()) {
                    Text(
                        text = "Belum ada data siswa terdaftar. Klik Tambah Siswa atau Impor CSV (format: nisn,nama_lengkap,kelas,pin_akses).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    filtered.forEach { student ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SoftBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(color = LavenderSoft, shape = RoundedCornerShape(8.dp)) {
                                            Text(
                                                text = student.kodeSiswa,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = LavenderPrimary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Text(
                                            text = "Kelas ${student.kelas}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = SkyBlueDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (unmaskNames) {
                                            "${student.namaLengkap} (NISN: ${student.nisn})"
                                        } else {
                                            student.censoredName()
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                }
                                Surface(color = MintSoft, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = "PIN: ${student.pinAkses}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MintPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Audit log for emergency identity opens
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CleanWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Log Audit Buka Identitas Darurat", style = MaterialTheme.typography.titleMedium)
                if (emergencyLogs.isEmpty()) {
                    Text(
                        text = "Belum ada pembukaan identitas darurat yang tercatat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    emergencyLogs.take(10).forEach { log ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmergencyRedSoft.copy(alpha = 0.55f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "#${log.ticketCode} • ${log.studentCode} (${log.openedByName})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmergencyRed
                                )
                                Text(
                                    text = "Alasan: ${log.reason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var nisn by remember { mutableStateOf("") }
        var nama by remember { mutableStateOf("") }
        var kelas by remember { mutableStateOf("") }
        var pin by remember { mutableStateOf("") }
        var formError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Data Siswa & Buat Kode") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nisn,
                        onValueChange = {
                            nisn = it
                            formError = null
                        },
                        label = { Text("NISN*") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nama,
                        onValueChange = {
                            nama = it
                            formError = null
                        },
                        label = { Text("Nama Lengkap*") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = kelas,
                        onValueChange = {
                            kelas = it
                            formError = null
                        },
                        label = { Text("Kelas* (mis. 8B)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text("PIN Akses (kosongkan untuk otomatis)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (formError != null) {
                        Text(formError.orEmpty(), color = EmergencyRed, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nisn.trim().length < 4 || nama.trim().length < 2 || kelas.trim().isBlank()) {
                            formError = "Lengkapi NISN (min. 4 digit), Nama, dan Kelas."
                            return@Button
                        }
                        onAddStudentManual(nisn, nama, kelas, pin)
                        showAddDialog = false
                    }
                ) {
                    Text("Simpan & Buat Kode")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun RelatedStudentsPanel(
    reports: List<BullyingReport>,
    onSelectReport: (BullyingReport) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Siswa Terkait & Observasi Wali Kelas", style = MaterialTheme.typography.titleMedium)
            if (reports.isEmpty()) {
                Text(
                    text = "Belum ada laporan siswa pada cakupan ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            } else {
                reports.forEach { report ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SoftBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectReport(report) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${report.displayReporterLabel()} • #${report.ticketCode}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = report.waliKelasNotes.ifBlank { "Belum ada catatan observasi Wali Kelas." },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CounselorSettingsPanel(
    staffSubRole: StaffSubRole,
    assignedStaffClass: String,
    soundAlertsEnabled: Boolean,
    onSelectStaffSubRole: (StaffSubRole) -> Unit,
    onSelectAssignedClass: (String) -> Unit,
    onToggleSoundAlerts: (Boolean) -> Unit
) {
    var classField by remember(assignedStaffClass) { mutableStateOf(assignedStaffClass) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Pengaturan Dasbor & Hak Akses Staf", style = MaterialTheme.typography.titleMedium)

            Text("Perspektif Staf Aktif", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StaffSubRole.entries.forEach { role ->
                    FilterChip(
                        selected = staffSubRole == role,
                        onClick = { onSelectStaffSubRole(role) },
                        label = { Text(role.subtitle) }
                    )
                }
            }

            if (staffSubRole == StaffSubRole.WALI_KELAS) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = classField,
                        onValueChange = { classField = it },
                        label = { Text("Kelas yang Diampu (mis. 8B)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { onSelectAssignedClass(classField) }) {
                        Text("Terapkan")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Alarm Audio & Notifikasi Real-Time", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Otomatis membunyikan alarm & notifikasi saat SOS atau laporan baru masuk via listener real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Switch(checked = soundAlertsEnabled, onCheckedChange = onToggleSoundAlerts)
            }

            OutlinedButton(
                onClick = { AlertNotifier.playSosAlarmSound() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uji Bunyi Alarm SOS")
            }

            Surface(
                color = LavenderSoft.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "TICARE AI • Technology with Empathy",
                        style = MaterialTheme.typography.titleSmall,
                        color = LavenderPrimary
                    )
                    Text(
                        text = "Trauma-Informed Counseling and Reporting Environment berbasis AI. Filosofi: Listen – Understand – Protect – Empower. Prinsip utama: AI membantu menganalisis, manusia (Guru BK) memvalidasi dan mengambil keputusan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }

            Surface(
                color = SkyBlueSoft.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Info FCM: Notifikasi real-time aktif saat aplikasi terbuka menggunakan Firestore real-time listener. Untuk push notifikasi latar belakang via Firebase Cloud Messaging (FCM) saat aplikasi tertutup, aktifkan Cloud Function/FCM server key di Firebase Console.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SkyBlueDark,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReportHumanDecisionDialog(
    report: BullyingReport,
    canEmergencyReveal: Boolean,
    onDismiss: () -> Unit,
    onRerunAi: () -> Unit,
    onRevealEmergencyIdentity: (String) -> Unit,
    onSave: (
        ReportStatus, AiPriority, HumanDecision,
        String, String, String, Boolean, String
    ) -> Unit
) {
    val context = LocalContext.current
    var status by remember(report) { mutableStateOf(report.statusEnum()) }
    var priority by remember(report) { mutableStateOf(report.priorityEnum()) }
    var decision by remember(report) { mutableStateOf(report.decisionEnum()) }
    var decisionNote by remember(report) { mutableStateOf(report.humanDecisionNote) }
    var bkMessage by remember(report) { mutableStateOf(report.bkMessage) }
    var waliNotes by remember(report) { mutableStateOf(report.waliKelasNotes) }
    var createCounseling by remember { mutableStateOf(false) }
    var scheduleLabel by remember { mutableStateOf("Hari ini, 13.30 WIB") }
    var showEmergencyConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CleanWhite),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detail Kasus #${report.ticketCode}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup")
                    }
                }

                // Original Student Report
                Surface(
                    color = SoftBackground,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Pelapor: ${report.displayReporterLabel()}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (report.emergencyRevealedName.isNotBlank()) {
                            Surface(color = EmergencyRedSoft, shape = RoundedCornerShape(10.dp)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Identitas Dibuka Darurat: ${report.emergencyRevealedName}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = EmergencyRed
                                    )
                                    Text(
                                        text = "Alasan: ${report.emergencyRevealReason}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Tujuan: ${report.displayRecipientLabel()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = LavenderPrimary
                        )
                        Text(
                            text = "Kategori: ${report.categoryEnum().label} • Lokasi: ${report.locationArea} • Waktu: ${report.incidentTimeLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(report.chronology, style = MaterialTheme.typography.bodyMedium)

                        if (report.latitude != null && report.longitude != null) {
                            TextButton(
                                onClick = {
                                    val url = "https://www.google.com/maps/search/?api=1&query=${report.latitude},${report.longitude}"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                }
                            ) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lihat Lokasi GPS di Google Maps")
                            }
                        }
                    }
                }

                // Emergency Identity Reveal Protocol (Guru BK only on High Priority)
                if (canEmergencyReveal && report.priorityEnum() == AiPriority.TINGGI && report.emergencyRevealedName.isBlank()) {
                    Button(
                        onClick = { showEmergencyConfirm = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmergencyRed,
                            contentColor = CleanWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("emergency_reveal_button")
                    ) {
                        Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buka Identitas Asli (Darurat)")
                    }
                }

                // AI Triage Panel + Disclaimer
                Surface(
                    color = LavenderSoft.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Triase Otomatis AI", style = MaterialTheme.typography.titleSmall, color = LavenderPrimary)
                            TextButton(onClick = onRerunAi) {
                                Text("Analisis Ulang", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Text("Ringkasan: ${report.aiSummary}", style = MaterialTheme.typography.bodySmall)
                        Text("Rekomendasi Awal: ${report.aiRecommendation}", style = MaterialTheme.typography.bodySmall)
                        if (report.aiTrendNote.isNotBlank()) {
                            Text("Pola: ${report.aiTrendNote}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Surface(
                            color = CleanWhite.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = AI_TRIAGE_DISCLAIMER,
                                style = MaterialTheme.typography.labelSmall,
                                color = SkyBlueDark,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Human-in-the-Loop Decision Form
                Text("Keputusan Manusia (Guru BK)", style = MaterialTheme.typography.titleMedium)

                Text("Keputusan Terhadap Rekomendasi AI", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        HumanDecision.DISETUJUI to "Setujui",
                        HumanDecision.DIMODIFIKASI to "Ubah",
                        HumanDecision.DITOLAK to "Tolak"
                    ).forEach { (dec, label) ->
                        FilterChip(
                            selected = decision == dec,
                            onClick = { decision = dec },
                            label = { Text(label) }
                        )
                    }
                }

                Text("Prioritas Kasus", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiPriority.entries.forEach { prio ->
                        FilterChip(
                            selected = priority == prio,
                            onClick = { priority = prio },
                            label = { Text(prio.label) }
                        )
                    }
                }

                Text("Status Penanganan", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportStatus.entries.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.label) }
                        )
                    }
                }

                OutlinedTextField(
                    value = decisionNote,
                    onValueChange = { decisionNote = it },
                    label = { Text("Catatan Keputusan Guru BK") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = waliNotes,
                    onValueChange = { waliNotes = it },
                    label = { Text("Catatan Observasi Wali Kelas") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bkMessage,
                    onValueChange = { bkMessage = it },
                    label = { Text("Pesan Resmi untuk Siswa (tampil di Riwayat)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = createCounseling, onCheckedChange = { createCounseling = it })
                    Text("Buat agenda jadwal konseling baru", style = MaterialTheme.typography.bodySmall)
                }

                if (createCounseling) {
                    OutlinedTextField(
                        value = scheduleLabel,
                        onValueChange = { scheduleLabel = it },
                        label = { Text("Jadwal Konseling") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        onSave(
                            status, priority, decision,
                            decisionNote, bkMessage, waliNotes,
                            createCounseling, scheduleLabel
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Simpan Keputusan Guru BK")
                }
            }
        }
    }

    if (showEmergencyConfirm) {
        var reason by remember { mutableStateOf("") }
        var reasonError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEmergencyConfirm = false },
            title = { Text("Konfirmasi Buka Identitas Darurat") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Protokol ini hanya digunakan jika siswa dalam kondisi bahaya mendesak dan membutuhkan pendampingan langsung atau kontak orang tua. Akses ini dicatat permanen dalam log audit.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = {
                            reason = it
                            reasonError = null
                        },
                        label = { Text("Alasan Darurat (wajib)*") },
                        placeholder = { Text("Contoh: Ancaman kekerasan fisik aktif, perlu dihubungi segera") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("emergency_reason_input")
                    )
                    if (reasonError != null) {
                        Text(reasonError.orEmpty(), color = EmergencyRed, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reason.trim().length < 5) {
                            reasonError = "Tuliskan alasan darurat minimal 5 karakter."
                            return@Button
                        }
                        onRevealEmergencyIdentity(reason.trim())
                        showEmergencyConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Buka Identitas Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
