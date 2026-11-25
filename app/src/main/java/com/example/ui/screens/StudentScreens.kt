package com.example.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.LocationHelper
import com.example.data.model.BullyingCategory
import com.example.data.model.BullyingReport
import com.example.data.model.ChatMessage
import com.example.data.model.DEFAULT_GURU_BK_OPTIONS
import com.example.data.model.DEFAULT_GURU_LAINNYA_OPTIONS
import com.example.data.model.RecipientType
import com.example.data.model.ReportDraft
import com.example.data.model.SosAlert
import com.example.data.model.UserProfile
import com.example.data.model.UserRoleMode
import com.example.ui.components.EmergencySosDialog
import com.example.ui.components.FourStageStepper
import com.example.ui.components.FriendlyCounselorIllustration
import com.example.ui.components.HelpGuideDialog
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.PriorityBadge
import com.example.ui.components.RobotCompanionAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudentTopHeader
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedBright
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.LavenderSoft
import com.example.ui.theme.MintPrimary
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueSoft
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class StudentTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    BERANDA("Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    LAPOR("Lapor", Icons.Filled.Report, Icons.Outlined.Report),
    AI_ASSISTANT("AI Assistant", Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
    RIWAYAT("Riwayat", Icons.Filled.History, Icons.Outlined.History),
    PROFIL("Profil", Icons.Filled.Person, Icons.Outlined.Person),
}

@Composable
fun StudentPortalShell(
    userEmail: String,
    userName: String,
    isVerifiedStaff: Boolean,
    userProfile: UserProfile?,
    reportsState: UiState<List<BullyingReport>>,
    sosAlertsState: UiState<List<SosAlert>>,
    reportDraft: ReportDraft,
    chatMessages: List<ChatMessage>,
    isChatBusy: Boolean,
    chatError: String?,
    isSubmittingReport: Boolean,
    lastCreatedTicket: String?,
    lastSubmittedRecipient: String?,
    isFetchingGps: Boolean,
    isVerifyingStudent: Boolean,
    studentLoginError: String?,
    isStudentGateBypassed: Boolean,
    soundAlertsEnabled: Boolean,
    statusBanner: String?,
    onClearBanner: () -> Unit,
    onClearLastTicket: () -> Unit,
    onVerifyStudentLogin: (String, String) -> Unit,
    onBypassStudentGate: () -> Unit,
    onResetStudentGate: () -> Unit,
    onSwitchToCounselor: () -> Unit,
    onQuickExit: () -> Unit,
    onSendChat: (String) -> Unit,
    onBuildDraftFromChat: (() -> Unit) -> Unit,
    onConnectCounselorFromChat: (() -> Unit) -> Unit,
    onSubmitReport: (
        Boolean, RecipientType, String, String, BullyingCategory,
        String, String, String, String, Double?, Double?, String, List<String>
    ) -> Unit,
    onTriggerSos: ((SosAlert?) -> Unit) -> Unit,
    onSaveProfile: (String, String) -> Unit,
    onToggleSoundAlerts: (Boolean) -> Unit,
    onSignOut: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(StudentTab.BERANDA) }
    var showSosDialog by remember { mutableStateOf(false) }
    var lastSentSos by remember { mutableStateOf<SosAlert?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (selectedTab != StudentTab.BERANDA) {
        BackHandler { selectedTab = StudentTab.BERANDA }
    }

    LaunchedEffect(statusBanner) {
        if (!statusBanner.isNullOrBlank()) {
            snackbarHostState.showSnackbar(statusBanner)
            onClearBanner()
        }
    }

    val reportsList = (reportsState as? UiState.Success)?.data ?: emptyList()
    val sosList = (sosAlertsState as? UiState.Success)?.data ?: emptyList()
    val linkedCode = userProfile?.linkedStudentCode.orEmpty()
    val linkedClass = userProfile?.linkedStudentClass.orEmpty()

    Scaffold(
        containerColor = SoftBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StudentTopHeader(
                title = when (selectedTab) {
                    StudentTab.BERANDA -> "TICARE AI"
                    StudentTab.LAPOR -> "Buat Laporan"
                    StudentTab.AI_ASSISTANT -> "AI Assistant"
                    StudentTab.RIWAYAT -> "Riwayat Laporan"
                    StudentTab.PROFIL -> "Profil Saya"
                },
                subtitle = when (selectedTab) {
                    StudentTab.BERANDA -> if (linkedCode.isNotBlank()) {
                        "Kode: $linkedCode • Technology with Empathy"
                    } else {
                        "Technology with Empathy • Sekolah Aman"
                    }
                    StudentTab.LAPOR -> "Identitasmu dijaga dengan aman"
                    StudentTab.AI_ASSISTANT -> "Sahabat Curhatmu"
                    StudentTab.RIWAYAT -> "Pantau progres penangananmu"
                    StudentTab.PROFIL -> linkedCode.ifBlank { userEmail.ifBlank { "Siswa Terdaftar" } }
                },
                notificationCount = reportsList.count { it.bkMessage.isNotBlank() },
                roleMode = UserRoleMode.STUDENT,
                onToggleRole = onSwitchToCounselor,
                onQuickExit = onQuickExit,
                onNotificationClick = { showNotifDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CleanWhite,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                StudentTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LavenderPrimary,
                            selectedTextColor = LavenderPrimary,
                            indicatorColor = LavenderSoft,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (linkedCode.isBlank() && !isStudentGateBypassed && selectedTab == StudentTab.BERANDA) {
                StudentCodeVerificationScreen(
                    isVerifying = isVerifyingStudent,
                    errorMessage = studentLoginError,
                    isVerifiedStaff = isVerifiedStaff,
                    onVerify = onVerifyStudentLogin,
                    onBypass = onBypassStudentGate,
                    onOpenEmergencySos = { showSosDialog = true }
                )
            } else {
                when (selectedTab) {
                    StudentTab.BERANDA -> StudentHomeScreen(
                        linkedStudentCode = linkedCode,
                        linkedStudentClass = linkedClass,
                        onGoToReport = { selectedTab = StudentTab.LAPOR },
                        onGoToAiChat = { selectedTab = StudentTab.AI_ASSISTANT },
                        onGoToHistory = { selectedTab = StudentTab.RIWAYAT },
                        onOpenHelp = { showHelpDialog = true },
                        onOpenSos = { showSosDialog = true },
                        onLinkStudentCode = onResetStudentGate
                    )

                    StudentTab.LAPOR -> CreateReportScreen(
                        draft = reportDraft,
                        isSubmitting = isSubmittingReport,
                        lastCreatedTicket = lastCreatedTicket,
                        lastSubmittedRecipient = lastSubmittedRecipient,
                        linkedStudentCode = linkedCode,
                        linkedStudentClass = linkedClass,
                        defaultStudentName = userProfile?.displayName?.takeIf { it != linkedCode } ?: userName,
                        defaultStudentClass = linkedClass.ifBlank { userProfile?.className.orEmpty() },
                        onDismissTicketDialog = {
                            onClearLastTicket()
                            selectedTab = StudentTab.RIWAYAT
                        },
                        onSubmitReport = onSubmitReport
                    )

                    StudentTab.AI_ASSISTANT -> AiAssistantScreen(
                        messages = chatMessages,
                        isBusy = isChatBusy,
                        error = chatError,
                        onSendMessage = onSendChat,
                        onBuildDraft = {
                            onBuildDraftFromChat { selectedTab = StudentTab.LAPOR }
                        },
                        onConnectCounselor = {
                            onConnectCounselorFromChat { selectedTab = StudentTab.LAPOR }
                        }
                    )

                    StudentTab.RIWAYAT -> ReportHistoryScreen(
                        reportsState = reportsState,
                        onCreateNew = { selectedTab = StudentTab.LAPOR }
                    )

                    StudentTab.PROFIL -> StudentProfileScreen(
                        userEmail = userEmail,
                        userName = userProfile?.displayName ?: userName,
                        userClass = linkedClass.ifBlank { userProfile?.className.orEmpty() },
                        linkedStudentCode = linkedCode,
                        isVerifiedStaff = isVerifiedStaff,
                        soundAlertsEnabled = soundAlertsEnabled,
                        onSaveProfile = onSaveProfile,
                        onResetStudentGate = {
                            onResetStudentGate()
                            selectedTab = StudentTab.BERANDA
                        },
                        onToggleSoundAlerts = onToggleSoundAlerts,
                        onSwitchToCounselor = onSwitchToCounselor,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }

    if (showSosDialog) {
        EmergencySosDialog(
            isSending = isFetchingGps,
            lastSentSos = lastSentSos,
            onSendSosWithGps = {
                onTriggerSos { sent -> lastSentSos = sent }
            },
            onDismiss = { showSosDialog = false }
        )
    }

    if (showHelpDialog) {
        HelpGuideDialog(onDismiss = { showHelpDialog = false })
    }

    if (showNotifDialog) {
        NotificationsDialog(
            reports = reportsList,
            sosAlerts = sosList,
            onDismiss = { showNotifDialog = false }
        )
    }
}

@Composable
private fun StudentCodeVerificationScreen(
    isVerifying: Boolean,
    errorMessage: String?,
    isVerifiedStaff: Boolean,
    onVerify: (String, String) -> Unit,
    onBypass: () -> Unit,
    onOpenEmergencySos: () -> Unit
) {
    var nisnOrCode by remember { mutableStateOf("") }
    var pinAkses by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 520.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SkyBlueSoft,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Badge,
                                    contentDescription = "Verifikasi Kode Siswa",
                                    tint = SkyBluePrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Verifikasi Siswa Sekolah",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Masuk privat dengan NISN & PIN Akses",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        color = MintSoft.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Setelah terverifikasi, namamu otomatis disamarkan menjadi Kode Siswa (contoh: SW-8B-014) agar Guru BK tahu laporan berasal dari siswa resmi sekolah tanpa membocorkan namamu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MintPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = nisnOrCode,
                        onValueChange = { nisnOrCode = it },
                        label = { Text("NISN atau Kode Siswa (mis. SW-8B-014)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_nisn_input")
                    )

                    OutlinedTextField(
                        value = pinAkses,
                        onValueChange = { pinAkses = it },
                        label = { Text("PIN Akses") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_pin_input")
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Surface(
                            color = EmergencyRedSoft,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                color = EmergencyRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { onVerify(nisnOrCode, pinAkses) },
                        enabled = !isVerifying,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verify_student_code_button")
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(
                                color = CleanWhite,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memverifikasi...")
                        } else {
                            Icon(Icons.Filled.VerifiedUser, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Masuk dengan Kode Siswa")
                        }
                    }

                    TextButton(
                        onClick = onBypass,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .testTag("bypass_student_gate_button")
                    ) {
                        Text(
                            text = if (isVerifiedStaff) {
                                "Pratinjau Portal Siswa Tanpa Kode (Staf)"
                            } else {
                                "Lanjutkan sebagai Tamu Anonim (Tanpa Kode)"
                            },
                            color = TextSecondary
                        )
                    }
                }
            }

            // Always allow 1-tap SOS even before entering student code
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmergencyRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenEmergencySos() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = CleanWhite)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Butuh Bantuan Darurat Sekarang?", color = CleanWhite, style = MaterialTheme.typography.titleSmall)
                            Text("Tekan untuk SOS + GPS atau telepon 110 / 119 / SAPA 129", color = CleanWhite.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CleanWhite)
                }
            }
        }
    }
}

@Composable
private fun StudentHomeScreen(
    linkedStudentCode: String,
    linkedStudentClass: String,
    onGoToReport: () -> Unit,
    onGoToAiChat: () -> Unit,
    onGoToHistory: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenSos: () -> Unit,
    onLinkStudentCode: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Banner (Pseudonymized with Kode Siswa if verified)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SkyBlueSoft, MintSoft.copy(alpha = 0.65f))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (linkedStudentCode.isNotBlank()) {
                                    "Halo, $linkedStudentCode!"
                                } else {
                                    "Halo, Sahabat Baik!"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                color = SkyBlueDark
                            )
                            if (linkedStudentCode.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = CleanWhite.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Identitas Disamarkan • Kelas ${linkedStudentClass.ifBlank { "-" }}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MintPrimary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Hubungkan NISN & PIN Akses",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LavenderPrimary,
                                    modifier = Modifier.clickable { onLinkStudentCode() }
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Kamu tidak sendirian. Di sini, kamu bisa melapor, bercerita, dan mendapatkan dukungan dengan aman.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        FriendlyCounselorIllustration(modifier = Modifier.size(96.dp))
                    }
                }
            }

            // Hero Card: Lapor Sekarang
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onGoToReport() }
                    .testTag("hero_lapor_sekarang")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(LavenderPrimary, IndigoDeep)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CleanWhite.copy(alpha = 0.22f),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Campaign,
                                        contentDescription = "Ikon Lapor Sekarang",
                                        tint = CleanWhite,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Lapor Sekarang",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = CleanWhite
                                )
                                Text(
                                    text = "Sampaikan laporan perundungan dengan aman",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CleanWhite.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Buka formulir laporan",
                            tint = CleanWhite
                        )
                    }
                }
            }

            // Grid 3 Menu Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeQuickMenuCard(
                    title = "Chat Aman",
                    subtitle = "Bicara dengan AI Assistant",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    accent = MintPrimary,
                    bg = MintSoft,
                    onClick = onGoToAiChat,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("menu_chat_aman")
                )
                HomeQuickMenuCard(
                    title = "Riwayat Laporan",
                    subtitle = "Lihat perkembangan laporanmu",
                    icon = Icons.Filled.Description,
                    accent = SkyBluePrimary,
                    bg = SkyBlueSoft,
                    onClick = onGoToHistory,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("menu_riwayat_laporan")
                )
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenHelp() }
                    .testTag("menu_bantuan")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SkyBlueSoft,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.MenuBook,
                                    contentDescription = "Bantuan",
                                    tint = SkyBluePrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Bantuan",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Panduan, informasi, dan dukungan",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = SkyBluePrimary
                    )
                }
            }

            // Tombol Darurat Merah
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onOpenSos() }
                    .testTag("tombol_darurat_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(EmergencyRed, EmergencyRedBright)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = CleanWhite.copy(alpha = 0.22f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "Tombol Darurat",
                                        tint = CleanWhite,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tombol Darurat",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CleanWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Butuh bantuan segera? Tekan di sini!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CleanWhite.copy(alpha = 0.92f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = CleanWhite
                        )
                    }
                }
            }

            // Trust & Philosophy Strip: Listen – Understand – Protect – Empower
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Filosofi TICARE AI: Listen – Understand – Protect – Empower",
                        style = MaterialTheme.typography.labelLarge,
                        color = IndigoDeep
                    )
                    Text(
                        text = "Mendengarkan – Memahami – Melindungi – Memberdayakan • Technology with Empathy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TrustPillarCard(
                    title = "Aman",
                    desc = "Sadar Trauma (TIC)",
                    icon = Icons.Filled.VerifiedUser,
                    accent = MintPrimary,
                    bg = MintSoft,
                    modifier = Modifier.weight(1f)
                )
                TrustPillarCard(
                    title = "Privat",
                    desc = "Mode Kode Siswa",
                    icon = Icons.Filled.Lock,
                    accent = SkyBluePrimary,
                    bg = SkyBlueSoft,
                    modifier = Modifier.weight(1f)
                )
                TrustPillarCard(
                    title = "Nyaman",
                    desc = "Human-Centered AI",
                    icon = Icons.Filled.Favorite,
                    accent = LavenderPrimary,
                    bg = LavenderSoft,
                    modifier = Modifier.weight(1f)
                )
            }

            // Alur Pendampingan (5 Stage Info Stepper)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = IndigoDeep
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alur Pendampingan",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val steps = listOf(
                        Triple(Icons.Filled.Campaign, "1. Siswa Melapor", "Anonim / Kode Siswa atau terbuka"),
                        Triple(Icons.Filled.SmartToy, "2. AI Assistant Membantu", "Membantu & menganalisis risiko"),
                        Triple(Icons.Filled.SupportAgent, "3. Guru BK Menindaklanjuti", "Verifikasi dan jadwal konseling"),
                        Triple(Icons.Filled.Psychology, "4. Pendampingan", "Konseling, dukungan & pemulihan"),
                        Triple(Icons.Filled.Shield, "5. Lingkungan Sekolah Aman", "Sekolah lebih positif & inklusif")
                    )
                    steps.forEachIndexed { idx, (icon, title, desc) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (idx % 2 == 0) SkyBlueSoft else MintSoft,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = if (idx % 2 == 0) SkyBluePrimary else MintPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustPillarCard(
    title: String,
    desc: String,
    icon: ImageVector,
    accent: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = bg,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HomeQuickMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    bg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = bg,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = accent)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CreateReportScreen(
    draft: ReportDraft,
    isSubmitting: Boolean,
    lastCreatedTicket: String?,
    lastSubmittedRecipient: String?,
    linkedStudentCode: String,
    linkedStudentClass: String,
    defaultStudentName: String,
    defaultStudentClass: String,
    onDismissTicketDialog: () -> Unit,
    onSubmitReport: (
        Boolean, RecipientType, String, String, BullyingCategory,
        String, String, String, String, Double?, Double?, String, List<String>
    ) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedRecipientType by remember(draft) { mutableStateOf(draft.recipientType) }
    var selectedGuruBkOptionId by remember(draft) { mutableStateOf("bk_team") }
    var selectedGuruLainOptionId by remember(draft) { mutableStateOf("wali_kelas_saya") }
    var customTeacherName by remember { mutableStateOf("") }

    var selectedCategory by remember(draft) { mutableStateOf(draft.category) }
    var privacyCodeOnly by remember(draft) { mutableStateOf(draft.anonymous) }
    var studentName by remember(defaultStudentName) { mutableStateOf(defaultStudentName) }
    var studentClass by remember(defaultStudentClass) { mutableStateOf(defaultStudentClass) }
    var incidentTime by remember(draft) {
        mutableStateOf(
            draft.incidentTimeLabel.ifBlank {
                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
            }
        )
    }
    var locationArea by remember(draft) { mutableStateOf(draft.locationArea.ifBlank { "Kelas" }) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var gpsLoading by remember { mutableStateOf(false) }
    var chronology by remember(draft) { mutableStateOf(draft.chronology) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val selectedUris = remember { mutableStateListOf<Uri>() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        uris.forEach { uri ->
            if (selectedUris.size < 5 && uri !in selectedUris) {
                selectedUris.add(uri)
            }
        }
    }

    val gpsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        scope.launch {
            gpsLoading = true
            val gps = LocationHelper.fetchCurrentLocation(context)
            latitude = gps?.latitude
            longitude = gps?.longitude
            gpsLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (linkedStudentCode.isNotBlank()) {
                Surface(
                    color = MintSoft.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = MintPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kode Siswa Terverifikasi: $linkedStudentCode (Kelas ${linkedStudentClass.ifBlank { "-" }})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MintPrimary
                        )
                    }
                }
            } else {
                Text(
                    text = "Sampaikan dengan jujur dan aman. Identitasmu akan dijaga.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // 1. Tujuan Penerima Laporan (Radio Card Selectors: Guru BK vs Guru Lainnya)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "1. Pilih Tujuan Penerima Laporan*",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Pilih apakah laporanmu ingin ditangani langsung oleh Tim BK atau guru yang paling kamu percaya.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RecipientRadioCard(
                    selected = selectedRecipientType == RecipientType.GURU_BK,
                    title = RecipientType.GURU_BK.label,
                    subtitle = RecipientType.GURU_BK.subtitle,
                    badgeText = "Disarankan untuk Konseling",
                    icon = Icons.Filled.SupportAgent,
                    accent = LavenderPrimary,
                    softBg = LavenderSoft,
                    onClick = {
                        selectedRecipientType = RecipientType.GURU_BK
                        validationError = null
                    },
                    modifier = Modifier.testTag("recipient_card_guru_bk")
                )

                RecipientRadioCard(
                    selected = selectedRecipientType == RecipientType.GURU_LAINNYA,
                    title = RecipientType.GURU_LAINNYA.label,
                    subtitle = RecipientType.GURU_LAINNYA.subtitle,
                    badgeText = "Wali Kelas / Guru Mapel",
                    icon = Icons.Filled.School,
                    accent = SkyBluePrimary,
                    softBg = SkyBlueSoft,
                    onClick = {
                        selectedRecipientType = RecipientType.GURU_LAINNYA
                        validationError = null
                    },
                    modifier = Modifier.testTag("recipient_card_guru_lainnya")
                )
            }

            // Dynamic Sub-Selection Card based on chosen RecipientType
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnimatedVisibility(visible = selectedRecipientType == RecipientType.GURU_BK) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Pilih Konselor Tujuan (Tim BK)",
                                style = MaterialTheme.typography.titleSmall,
                                color = LavenderPrimary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DEFAULT_GURU_BK_OPTIONS.forEach { option ->
                                    FilterChip(
                                        selected = selectedGuruBkOptionId == option.id,
                                        onClick = { selectedGuruBkOptionId = option.id },
                                        label = { Text(option.name) },
                                        leadingIcon = if (selectedGuruBkOptionId == option.id) {
                                            {
                                                Icon(
                                                    Icons.Filled.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LavenderSoft,
                                            selectedLabelColor = LavenderPrimary,
                                            selectedLeadingIconColor = LavenderPrimary
                                        ),
                                        modifier = Modifier.testTag("bk_opt_${option.id}")
                                    )
                                }
                            }
                            Text(
                                text = "Opsi \"Tim BK (Siapa Saja yang Tersedia)\" memastikan laporan langsung masuk antrean prioritas agar cepat ditangani.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    AnimatedVisibility(visible = selectedRecipientType == RecipientType.GURU_LAINNYA) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val activeClassHint = linkedStudentClass.ifBlank { studentClass.trim() }
                            Text(
                                text = "Pilih Wali Kelas atau Guru Mata Pelajaran",
                                style = MaterialTheme.typography.titleSmall,
                                color = SkyBlueDark
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DEFAULT_GURU_LAINNYA_OPTIONS.forEach { option ->
                                    val displayLabel = if (option.id == "wali_kelas_saya" && activeClassHint.isNotBlank()) {
                                        "Wali Kelas $activeClassHint"
                                    } else {
                                        option.name
                                    }
                                    FilterChip(
                                        selected = selectedGuruLainOptionId == option.id,
                                        onClick = {
                                            selectedGuruLainOptionId = option.id
                                            validationError = null
                                        },
                                        label = { Text(displayLabel) },
                                        leadingIcon = if (selectedGuruLainOptionId == option.id) {
                                            {
                                                Icon(
                                                    Icons.Filled.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SkyBlueSoft,
                                            selectedLabelColor = SkyBlueDark,
                                            selectedLeadingIconColor = SkyBlueDark
                                        ),
                                        modifier = Modifier.testTag("guru_lain_opt_${option.id}")
                                    )
                                }
                                FilterChip(
                                    selected = selectedGuruLainOptionId == "custom_teacher",
                                    onClick = {
                                        selectedGuruLainOptionId = "custom_teacher"
                                        validationError = null
                                    },
                                    label = { Text("Tulis Nama Guru Lain") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SkyBlueSoft,
                                        selectedLabelColor = SkyBlueDark
                                    ),
                                    modifier = Modifier.testTag("guru_lain_opt_custom")
                                )
                            }

                            AnimatedVisibility(visible = selectedGuruLainOptionId == "custom_teacher") {
                                OutlinedTextField(
                                    value = customTeacherName,
                                    onValueChange = {
                                        customTeacherName = it
                                        validationError = null
                                    },
                                    label = { Text("Nama Wali Kelas / Guru Mapel yang Dituju*") },
                                    placeholder = { Text("Contoh: Bu Dewi (Guru Bahasa Indonesia)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_teacher_name_input")
                                )
                            }

                            Text(
                                text = "Guru penerima tetap dapat berkoordinasi dengan Guru BK secara rahasia bila kasus memerlukan konseling lanjutan.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 2. Kategori Perundungan
            Text(
                text = "2. Pilih Kategori Perundungan*",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            val categories = listOf(
                Triple(BullyingCategory.VERBAL, Icons.Filled.RecordVoiceOver, MintPrimary to MintSoft),
                Triple(BullyingCategory.FISIK, Icons.Filled.Warning, EmergencyRed to EmergencyRedSoft),
                Triple(BullyingCategory.SIBER, Icons.Filled.PhoneAndroid, SkyBluePrimary to SkyBlueSoft),
                Triple(BullyingCategory.SOSIAL, Icons.Filled.Groups, LavenderPrimary to LavenderSoft),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                categories.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { (cat, icon, colors) ->
                            BullyingCategoryCard(
                                category = cat,
                                icon = icon,
                                accent = colors.first,
                                softBg = colors.second,
                                isSelected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("cat_${cat.name.lowercase()}")
                            )
                        }
                    }
                }
                BullyingCategoryCard(
                    category = BullyingCategory.LAINNYA,
                    icon = Icons.Filled.MoreHoriz,
                    accent = IndigoDeep,
                    softBg = LavenderSoft.copy(alpha = 0.7f),
                    isSelected = selectedCategory == BullyingCategory.LAINNYA,
                    onClick = { selectedCategory = BullyingCategory.LAINNYA },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cat_${BullyingCategory.LAINNYA.name.lowercase()}")
                )
            }

            // 2. Toggle Mode Privasi (Tampilkan Kode Saja)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = "Mode Privasi",
                                tint = LavenderPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Mode Privasi (Tampilkan Kode Saja)", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = if (linkedStudentCode.isNotBlank()) {
                                        "Guru BK & Wali Kelas hanya melihat Kode Siswa ($linkedStudentCode) tanpa nama aslimu"
                                    } else {
                                        "Identitasmu tidak akan ditampilkan pada Guru BK dan pihak lain"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = privacyCodeOnly,
                            onCheckedChange = { privacyCodeOnly = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CleanWhite,
                                checkedTrackColor = MintPrimary
                            ),
                            modifier = Modifier.testTag("anonymous_toggle")
                        )
                    }

                    AnimatedVisibility(visible = !privacyCodeOnly) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = studentName,
                                onValueChange = { studentName = it },
                                label = { Text("Nama Lengkap Siswa (untuk panggilan konseling)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = studentClass,
                                onValueChange = { studentClass = it },
                                label = { Text("Kelas (untuk diteruskan ke Wali Kelas)") },
                                singleLine = true,
                                placeholder = { Text("Contoh: 8B / XI IPA 2") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 3. Waktu & Lokasi Kejadian
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Waktu & Lokasi Kejadian*", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = incidentTime,
                        onValueChange = { incidentTime = it },
                        label = { Text("Tanggal & Waktu Kejadian") },
                        leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.DateRange, contentDescription = "Pilih tanggal")
                                }
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(Icons.Filled.AccessTime, contentDescription = "Pilih jam")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Area Lokasi di Sekolah", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    val areas = listOf("Kelas", "Kantin", "Lapangan", "Toilet", "Luar Sekolah/Online")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        areas.forEach { area ->
                            FilterChip(
                                selected = locationArea == area,
                                onClick = { locationArea = area },
                                label = { Text(area) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SkyBlueSoft,
                                    selectedLabelColor = SkyBlueDark
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            gpsPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fetch_gps_button")
                    ) {
                        if (gpsLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mengambil Koordinat GPS...")
                        } else {
                            Icon(Icons.Filled.GpsFixed, contentDescription = null, tint = SkyBluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ambil Lokasi GPS Saya Saat Ini")
                        }
                    }

                    if (latitude != null && longitude != null) {
                        Surface(
                            color = MintSoft,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Koordinat GPS terlampir: ${"%.5f".format(latitude)}, ${"%.5f".format(longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MintPrimary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // 4. Kronologi & Unggah Bukti
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Kronologi & Unggah Bukti", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = chronology,
                        onValueChange = {
                            chronology = it
                            validationError = null
                        },
                        label = { Text("Ceritakan kejadian yang kamu alami atau lihat*") },
                        placeholder = { Text("Apa yang terjadi, siapa yang terlibat, dan bagaimana situasinya...") },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chronology_input")
                    )

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        },
                        enabled = selectedUris.size < 5,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pick_evidence_button")
                    ) {
                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unggah Foto/Video/Screenshot (${selectedUris.size}/5)")
                    }

                    if (selectedUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(selectedUris) { uri ->
                                Box(modifier = Modifier.size(72.dp)) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Lampiran bukti",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = EmergencyRed,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(22.dp)
                                            .clickable { selectedUris.remove(uri) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Hapus bukti",
                                                tint = CleanWhite,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (validationError != null) {
                Surface(
                    color = EmergencyRedSoft,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError.orEmpty(),
                        color = EmergencyRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val trimmed = chronology.trim()
                    if (trimmed.length < 5) {
                        validationError = "Mohon tuliskan kronologi minimal 5 karakter agar tim pendamping bisa memahami situasimu."
                        return@Button
                    }
                    if (!privacyCodeOnly && studentName.isBlank()) {
                        validationError = "Mohon isi nama siswa atau aktifkan kembali Mode Privasi."
                        return@Button
                    }

                    val (resolvedTeacherName, resolvedTeacherRole) = when (selectedRecipientType) {
                        RecipientType.GURU_BK -> {
                            val chosen = DEFAULT_GURU_BK_OPTIONS.firstOrNull { it.id == selectedGuruBkOptionId }
                                ?: DEFAULT_GURU_BK_OPTIONS.first()
                            chosen.name to chosen.roleCode
                        }
                        RecipientType.GURU_LAINNYA -> {
                            if (selectedGuruLainOptionId == "custom_teacher") {
                                val cleanCustom = customTeacherName.trim()
                                if (cleanCustom.length < 2) {
                                    validationError = "Mohon tulis nama guru penerima laporanmu."
                                    return@Button
                                }
                                cleanCustom to "GURU_MAPEL"
                            } else {
                                val chosen = DEFAULT_GURU_LAINNYA_OPTIONS.firstOrNull { it.id == selectedGuruLainOptionId }
                                    ?: DEFAULT_GURU_LAINNYA_OPTIONS.first()
                                val activeClassHint = linkedStudentClass.ifBlank { studentClass.trim() }
                                val name = if (chosen.id == "wali_kelas_saya" && activeClassHint.isNotBlank()) {
                                    "Wali Kelas $activeClassHint"
                                } else {
                                    chosen.name
                                }
                                name to chosen.roleCode
                            }
                        }
                    }

                    onSubmitReport(
                        privacyCodeOnly,
                        selectedRecipientType,
                        resolvedTeacherName,
                        resolvedTeacherRole,
                        selectedCategory,
                        studentName,
                        studentClass,
                        incidentTime,
                        locationArea,
                        latitude,
                        longitude,
                        trimmed,
                        selectedUris.mapIndexed { idx, uri ->
                            uri.lastPathSegment?.take(60) ?: "Bukti_${idx + 1}"
                        }
                    )
                },
                enabled = !isSubmitting,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderPrimary,
                    contentColor = CleanWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_report_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = CleanWhite,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Menganalisis AI & Mengirim...")
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kirim Laporan", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        val dateText = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(millis))
                        incidentTime = "$dateText, ${incidentTime.substringAfter(", ", "09:00")}"
                        showDatePicker = false
                    }
                ) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Pilih Waktu Kejadian") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val datePart = incidentTime.substringBefore(", ", SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date()))
                        val hh = "%02d".format(timePickerState.hour)
                        val mm = "%02d".format(timePickerState.minute)
                        incidentTime = "$datePart, $hh:$mm"
                        showTimePicker = false
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Batal") }
            }
        )
    }

    if (lastCreatedTicket != null) {
        AlertDialog(
            onDismissRequest = onDismissTicketDialog,
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MintPrimary,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text("Laporan Berhasil Dikirim!", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Simpan Kode Tiket Unik ini untuk memantau perkembangan laporanmu:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = LavenderSoft,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "#$lastCreatedTicket",
                            style = MaterialTheme.typography.headlineSmall,
                            color = LavenderPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                    if (!lastSubmittedRecipient.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MintSoft.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Ditujukan ke: $lastSubmittedRecipient",
                                style = MaterialTheme.typography.labelMedium,
                                color = MintPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismissTicketDialog) {
                    Text("Lihat Riwayat Laporan")
                }
            }
        )
    }
}

@Composable
private fun RecipientRadioCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    accent: Color,
    softBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) softBg.copy(alpha = 0.65f) else CleanWhite
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) accent else SlateBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(20.dp))
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (selected) CleanWhite else softBg,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Surface(
                        color = if (selected) CleanWhite else softBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = accent,
                    unselectedColor = TextMuted
                )
            )
        }
    }
}

@Composable
private fun BullyingCategoryCard(
    category: BullyingCategory,
    icon: ImageVector,
    accent: Color,
    softBg: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) softBg else CleanWhite
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) accent else SlateBorder
        ),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) CleanWhite else softBg,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = category.label, tint = accent, modifier = Modifier.size(20.dp))
                    }
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Dipilih",
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.label, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(category.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun AiAssistantScreen(
    messages: List<ChatMessage>,
    isBusy: Boolean,
    error: String?,
    onSendMessage: (String) -> Unit,
    onBuildDraft: () -> Unit,
    onConnectCounselor: () -> Unit,
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Surface(
            color = SkyBlueSoft.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RobotCompanionAvatar(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Sahabat Curhatmu", style = MaterialTheme.typography.titleSmall, color = SkyBlueDark)
                    Text(
                        text = "Ruang aman untuk bercerita & menyusun laporan terstruktur",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(messages, key = { it.id }) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isFromUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!msg.isFromUser) {
                        RobotCompanionAvatar(
                            modifier = Modifier
                                .padding(end = 8.dp, top = 4.dp)
                                .size(32.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (msg.isFromUser) 18.dp else 4.dp,
                            bottomEnd = if (msg.isFromUser) 4.dp else 18.dp
                        ),
                        color = if (msg.isFromUser) IndigoDeep else CleanWhite,
                        tonalElevation = if (msg.isFromUser) 0.dp else 2.dp,
                        modifier = Modifier.widthIn(max = 310.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (msg.text.isBlank() && msg.isStreaming) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sedang mengetik...", style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (msg.isFromUser) CleanWhite else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!error.isNullOrBlank()) {
            Surface(
                color = EmergencyRedSoft,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = error,
                    color = EmergencyRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Surface(color = CleanWhite, tonalElevation = 4.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBuildDraft,
                        enabled = !isBusy,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MintPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_build_draft_button")
                    ) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MintPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bantu susun laporan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = onConnectCounselor,
                        enabled = !isBusy,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, LavenderPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_connect_bk_button")
                    ) {
                        Icon(
                            Icons.Filled.SupportAgent,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Hubungkan ke Guru BK",
                            style = MaterialTheme.typography.labelSmall,
                            color = LavenderPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ketik pesanmu di sini...") },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = if (inputText.isNotBlank() && !isBusy) SkyBluePrimary else SlateBorder,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(enabled = inputText.isNotBlank() && !isBusy) {
                                val toSend = inputText
                                inputText = ""
                                onSendMessage(toSend)
                            }
                            .testTag("chat_send_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Kirim pesan",
                                tint = CleanWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportHistoryScreen(
    reportsState: UiState<List<BullyingReport>>,
    onCreateNew: () -> Unit
) {
    var ticketSearch by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {
            OutlinedTextField(
                value = ticketSearch,
                onValueChange = { ticketSearch = it },
                label = { Text("Cari dengan Kode Tiket Unik (misal LP-0428)") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ticket_search_input")
            )
            Spacer(modifier = Modifier.height(14.dp))

            when (reportsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EmergencyRedSoft),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = reportsState.message,
                            color = EmergencyRed,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is UiState.Success -> {
                    val query = ticketSearch.trim().removePrefix("#")
                    val filtered = if (query.isBlank()) {
                        reportsState.data
                    } else {
                        reportsState.data.filter {
                            it.ticketCode.contains(query, ignoreCase = true) ||
                                it.locationArea.contains(query, ignoreCase = true)
                        }
                    }

                    if (filtered.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = CleanWhite),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (query.isBlank()) "Belum ada laporan terkirim" else "Tiket #$query tidak ditemukan",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Jika kamu melihat atau mengalami perundungan, kamu bisa melaporkannya secara aman.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(onClick = onCreateNew) {
                                    Text("Buat Laporan Baru")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filtered, key = { it.id.ifBlank { it.ticketCode } }) { report ->
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "#${report.ticketCode} • ${report.categoryEnum().label}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary
                                            )
                                            StatusBadge(report.statusEnum())
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            PriorityBadge(report.priorityEnum())
                                            Text(
                                                text = "${report.locationArea} • ${report.incidentTimeLabel}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }

                                        Surface(
                                            color = LavenderSoft.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "Tujuan: ${report.displayRecipientLabel()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = LavenderPrimary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        FourStageStepper(currentStage = report.stageIndex.toInt())

                                        Surface(
                                            color = SkyBlueSoft.copy(alpha = 0.65f),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = "Pesan Resmi Guru BK:",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = SkyBlueDark
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = report.bkMessage.ifBlank {
                                                        "Laporan sedang dalam proses verifikasi aman oleh Guru BK."
                                                    },
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
                }
            }
        }
    }
}

@Composable
private fun StudentProfileScreen(
    userEmail: String,
    userName: String,
    userClass: String,
    linkedStudentCode: String,
    isVerifiedStaff: Boolean,
    soundAlertsEnabled: Boolean,
    onSaveProfile: (String, String) -> Unit,
    onResetStudentGate: () -> Unit,
    onToggleSoundAlerts: (Boolean) -> Unit,
    onSwitchToCounselor: () -> Unit,
    onSignOut: () -> Unit
) {
    var nameInput by remember(userName) { mutableStateOf(userName) }
    var classInput by remember(userClass) { mutableStateOf(userClass) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = LavenderSoft,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (linkedStudentCode.ifBlank { nameInput }).firstOrNull()?.uppercase() ?: "S",
                                style = MaterialTheme.typography.headlineSmall,
                                color = LavenderPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = linkedStudentCode.ifBlank { nameInput.ifBlank { "Sahabat Siswa" } },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(userEmail, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        if (linkedStudentCode.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(color = SkyBlueSoft, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "Kode Siswa: $linkedStudentCode • Kelas ${userClass.ifBlank { "-" }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SkyBlueDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (isVerifiedStaff) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(color = MintSoft, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "Akun Staf Guru BK Terverifikasi",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MintPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Penyamaran & Kode Siswa", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = if (linkedStudentCode.isNotBlank()) {
                            "Akun ini tertaut ke $linkedStudentCode. Guru BK hanya melihat kode ini saat Mode Privasi aktif."
                        } else {
                            "Belum terhubung ke Kode Siswa resmi sekolah."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedButton(
                        onClick = onResetStudentGate,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Badge, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (linkedStudentCode.isNotBlank()) "Ganti Kode Siswa (NISN & PIN)" else "Hubungkan Kode Siswa (NISN & PIN)"
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Data Profil Siswa", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Panggilan / Lengkap") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = classInput,
                        onValueChange = { classInput = it },
                        label = { Text("Kelas (Opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { onSaveProfile(nameInput, classInput) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Profil")
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Suara Notifikasi & Alarm SOS", style = MaterialTheme.typography.titleSmall)
                            Text("Aktifkan nada peringatan real-time", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(checked = soundAlertsEnabled, onCheckedChange = onToggleSoundAlerts)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder)
                    OutlinedButton(
                        onClick = onSwitchToCounselor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buka Dasbor Guru BK / Wali Kelas")
                    }
                }
            }

            OutlinedButton(
                onClick = onSignOut,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                border = BorderStroke(1.dp, EmergencyRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar dari Akun")
            }
        }
    }
}
