package com.example

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.core.AlertNotifier
import com.example.core.configureAppCheck
import com.example.data.model.UserRoleMode
import com.example.data.repository.AiRepository
import com.example.data.repository.SahabatRepository
import com.example.data.repository.attemptAutoSignIn
import com.example.data.repository.authStateFlow
import com.example.data.repository.isVerifiedStaffUser
import com.example.data.repository.signOut
import com.example.ui.components.QuickExitSafeScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CounselorDashboardScreen
import com.example.ui.screens.StudentPortalShell
import com.example.ui.theme.SahabatAmanTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory Step 1: Configure Firebase App Check before UI, ViewModels, or Firebase calls
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            SahabatAmanTheme {
                SahabatAmanApp()
            }
        }
    }
}

@Composable
private fun SahabatAmanApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val auth = remember { Firebase.auth }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    var requestedPortalMode by remember { mutableStateOf<UserRoleMode?>(null) }

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {},
                onUnauthenticated = {},
                scope = scope
            )
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = { chosenMode ->
                requestedPortalMode = chosenMode
            }
        )
    } else {
        val uid = user.uid
        val verifiedStaff = isVerifiedStaffUser(user)
        val displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Sahabat Siswa"
        val userEmail = user.email.orEmpty()

        key(uid) {
            val viewModel: MainViewModel = viewModel(
                key = uid,
                factory = viewModelFactory {
                    initializer {
                        val app = checkNotNull(this[APPLICATION_KEY])
                        val dbId = app.getString(R.string.firestore_database_id)
                        val db = FirebaseFirestore.getInstance(dbId)
                        MainViewModel(
                            repository = SahabatRepository(db, auth),
                            aiRepository = AiRepository(),
                            currentUserId = uid,
                            isVerifiedStaff = verifiedStaff,
                            userDisplayName = displayName
                        )
                    }
                }
            )

            LaunchedEffect(requestedPortalMode, verifiedStaff) {
                val chosen = requestedPortalMode
                if (chosen != null) {
                    if (chosen == UserRoleMode.COUNSELOR) {
                        viewModel.setRoleMode(UserRoleMode.COUNSELOR)
                    } else {
                        viewModel.setRoleMode(UserRoleMode.STUDENT)
                    }
                    requestedPortalMode = null
                }
            }

            AuthenticatedRoot(
                viewModel = viewModel,
                userEmail = userEmail,
                userName = displayName,
                onSignOut = {
                    signOut(credentialManager, scope)
                }
            )
        }
    }
}

@Composable
private fun AuthenticatedRoot(
    viewModel: MainViewModel,
    userEmail: String,
    userName: String,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val roleMode by viewModel.roleMode.collectAsStateWithLifecycle()
    val staffSubRole by viewModel.staffSubRole.collectAsStateWithLifecycle()
    val assignedStaffClass by viewModel.assignedStaffClass.collectAsStateWithLifecycle()
    val soundAlertsEnabled by viewModel.soundAlertsEnabled.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val reportsState by viewModel.reportsState.collectAsStateWithLifecycle()
    val sosAlertsState by viewModel.sosAlertsState.collectAsStateWithLifecycle()
    val counselingState by viewModel.counselingState.collectAsStateWithLifecycle()
    val studentsState by viewModel.studentsState.collectAsStateWithLifecycle()
    val emergencyLogsState by viewModel.emergencyLogsState.collectAsStateWithLifecycle()
    val reportDraft by viewModel.reportDraft.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatBusy by viewModel.isChatBusy.collectAsStateWithLifecycle()
    val chatError by viewModel.chatError.collectAsStateWithLifecycle()
    val isSubmittingReport by viewModel.isSubmittingReport.collectAsStateWithLifecycle()
    val lastCreatedTicket by viewModel.lastCreatedTicket.collectAsStateWithLifecycle()
    val lastSubmittedRecipient by viewModel.lastSubmittedRecipient.collectAsStateWithLifecycle()
    val isFetchingGps by viewModel.isFetchingGps.collectAsStateWithLifecycle()
    val isVerifyingStudent by viewModel.isVerifyingStudent.collectAsStateWithLifecycle()
    val studentLoginError by viewModel.studentLoginError.collectAsStateWithLifecycle()
    val isStudentGateBypassed by viewModel.isStudentGateBypassed.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val preventionInsight by viewModel.preventionInsight.collectAsStateWithLifecycle()
    val isGeneratingInsight by viewModel.isGeneratingInsight.collectAsStateWithLifecycle()
    val insightError by viewModel.insightError.collectAsStateWithLifecycle()

    var quickExitActive by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        AlertNotifier.ensureChannels(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val reportsList = (reportsState as? UiState.Success)?.data ?: emptyList()
    val sosList = (sosAlertsState as? UiState.Success)?.data ?: emptyList()

    LaunchedEffect(sosList, reportsList) {
        viewModel.observeRealTimeAlerts(context, sosList, reportsList)
    }

    if (quickExitActive) {
        QuickExitSafeScreen(
            onReturnToApp = { quickExitActive = false },
            onOpenGoogleBrowser = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                )
            }
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val defaultExpandedCounselor = maxWidth >= 840.dp && viewModel.isVerifiedStaff
        LaunchedEffect(defaultExpandedCounselor) {
            if (defaultExpandedCounselor && roleMode == UserRoleMode.STUDENT) {
                viewModel.setRoleMode(UserRoleMode.COUNSELOR)
            }
        }

        if (roleMode == UserRoleMode.COUNSELOR) {
            CounselorDashboardScreen(
                isVerifiedStaff = viewModel.isVerifiedStaff,
                staffSubRole = staffSubRole,
                assignedStaffClass = assignedStaffClass,
                soundAlertsEnabled = soundAlertsEnabled,
                reportsState = reportsState,
                sosAlertsState = sosAlertsState,
                counselingState = counselingState,
                studentsState = studentsState,
                emergencyLogsState = emergencyLogsState,
                preventionInsight = preventionInsight,
                isGeneratingInsight = isGeneratingInsight,
                insightError = insightError,
                statusBanner = statusBanner,
                onClearBanner = viewModel::clearBanner,
                onSwitchToStudent = { viewModel.setRoleMode(UserRoleMode.STUDENT) },
                onSelectStaffSubRole = viewModel::setStaffSubRole,
                onSelectAssignedClass = viewModel::setAssignedStaffClass,
                onToggleSoundAlerts = viewModel::toggleSoundAlerts,
                onResolveSos = viewModel::resolveSosAlert,
                onSaveHumanDecision = viewModel::saveHumanDecision,
                onRerunAiTriage = viewModel::rerunAiTriageForReport,
                onAdvanceCounselingStage = viewModel::advanceCounselingStage,
                onGenerateInsight = viewModel::generateAiPreventionInsight,
                onAddStudentManual = viewModel::addStudentManual,
                onImportStudentsCsv = viewModel::importStudentsCsv,
                onRevealEmergencyIdentity = viewModel::revealEmergencyIdentity
            )
        } else {
            StudentPortalShell(
                userEmail = userEmail,
                userName = userName,
                isVerifiedStaff = viewModel.isVerifiedStaff,
                userProfile = userProfile,
                reportsState = reportsState,
                sosAlertsState = sosAlertsState,
                reportDraft = reportDraft,
                chatMessages = chatMessages,
                isChatBusy = isChatBusy,
                chatError = chatError,
                isSubmittingReport = isSubmittingReport,
                lastCreatedTicket = lastCreatedTicket,
                lastSubmittedRecipient = lastSubmittedRecipient,
                isFetchingGps = isFetchingGps,
                isVerifyingStudent = isVerifyingStudent,
                studentLoginError = studentLoginError,
                isStudentGateBypassed = isStudentGateBypassed,
                soundAlertsEnabled = soundAlertsEnabled,
                statusBanner = statusBanner,
                onClearBanner = viewModel::clearBanner,
                onClearLastTicket = viewModel::clearLastTicket,
                onVerifyStudentLogin = viewModel::verifyStudentLogin,
                onBypassStudentGate = viewModel::bypassStudentGateForPreview,
                onResetStudentGate = viewModel::resetStudentCodeGate,
                onSwitchToCounselor = { viewModel.setRoleMode(UserRoleMode.COUNSELOR) },
                onQuickExit = { quickExitActive = true },
                onSendChat = viewModel::sendChatMessage,
                onBuildDraftFromChat = viewModel::buildReportDraftFromChat,
                onConnectCounselorFromChat = viewModel::requestCounselorFromChat,
                onSubmitReport = viewModel::submitReport,
                onTriggerSos = { cb ->
                    viewModel.triggerSosEmergency(context, onComplete = cb)
                },
                onSaveProfile = viewModel::saveProfile,
                onToggleSoundAlerts = viewModel::toggleSoundAlerts,
                onSignOut = onSignOut
            )
        }
    }
}
