package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.AlertNotifier
import com.example.core.LocationHelper
import com.example.data.model.AiPriority
import com.example.data.model.BullyingCategory
import com.example.data.model.BullyingReport
import com.example.data.model.ChatMessage
import com.example.data.model.CounselingSession
import com.example.data.model.EmergencyIdentityLog
import com.example.data.model.HumanDecision
import com.example.data.model.RecipientType
import com.example.data.model.ReportDraft
import com.example.data.model.ReportStatus
import com.example.data.model.SosAlert
import com.example.data.model.StaffSubRole
import com.example.data.model.StudentMaster
import com.example.data.model.UserProfile
import com.example.data.model.UserRoleMode
import com.example.data.model.normalizeClassToken
import com.example.data.repository.AiRepository
import com.example.data.repository.SahabatRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MainViewModel(
    private val repository: SahabatRepository,
    private val aiRepository: AiRepository,
    private val currentUserId: String,
    val isVerifiedStaff: Boolean,
    private val userDisplayName: String,
) : ViewModel() {

    private val _roleMode = MutableStateFlow(UserRoleMode.STUDENT)
    val roleMode: StateFlow<UserRoleMode> = _roleMode.asStateFlow()

    private val _staffSubRole = MutableStateFlow(StaffSubRole.GURU_BK)
    val staffSubRole: StateFlow<StaffSubRole> = _staffSubRole.asStateFlow()

    private val _assignedStaffClass = MutableStateFlow("8B")
    val assignedStaffClass: StateFlow<String> = _assignedStaffClass.asStateFlow()

    private val _soundAlertsEnabled = MutableStateFlow(true)
    val soundAlertsEnabled: StateFlow<Boolean> = _soundAlertsEnabled.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _lastCreatedTicket = MutableStateFlow<String?>(null)
    val lastCreatedTicket: StateFlow<String?> = _lastCreatedTicket.asStateFlow()

    private val _lastSubmittedRecipient = MutableStateFlow<String?>(null)
    val lastSubmittedRecipient: StateFlow<String?> = _lastSubmittedRecipient.asStateFlow()

    private val _isSubmittingReport = MutableStateFlow(false)
    val isSubmittingReport: StateFlow<Boolean> = _isSubmittingReport.asStateFlow()

    private val _isFetchingGps = MutableStateFlow(false)
    val isFetchingGps: StateFlow<Boolean> = _isFetchingGps.asStateFlow()

    private val _isVerifyingStudent = MutableStateFlow(false)
    val isVerifyingStudent: StateFlow<Boolean> = _isVerifyingStudent.asStateFlow()

    private val _studentLoginError = MutableStateFlow<String?>(null)
    val studentLoginError: StateFlow<String?> = _studentLoginError.asStateFlow()

    private val _isStudentGateBypassed = MutableStateFlow(false)
    val isStudentGateBypassed: StateFlow<Boolean> = _isStudentGateBypassed.asStateFlow()

    private val _reportDraft = MutableStateFlow(ReportDraft())
    val reportDraft: StateFlow<ReportDraft> = _reportDraft.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome_1",
                isFromUser = false,
                text = "Hai! Aku AI Assistant, sahabat amanmu di sini. Kamu mau melaporkan perundungan, curhat dulu, atau minta saran?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatBusy = MutableStateFlow(false)
    val isChatBusy: StateFlow<Boolean> = _isChatBusy.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private val _preventionInsight = MutableStateFlow<String?>(null)
    val preventionInsight: StateFlow<String?> = _preventionInsight.asStateFlow()

    private val _isGeneratingInsight = MutableStateFlow(false)
    val isGeneratingInsight: StateFlow<Boolean> = _isGeneratingInsight.asStateFlow()

    private val _insightError = MutableStateFlow<String?>(null)
    val insightError: StateFlow<String?> = _insightError.asStateFlow()

    private var seenActiveSosIds: Set<String> = emptySet()
    private var seenReportIds: Set<String> = emptySet()
    private var sosInitialized = false
    private var reportsInitialized = false

    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile(currentUserId)
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Profile stream error", firestoreError ?: error)
            emit(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    val studentsState: StateFlow<UiState<List<StudentMaster>>> =
        repository.observeStudents(currentUserId, isVerifiedStaff)
            .map<List<StudentMaster>, UiState<List<StudentMaster>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Students stream error", root)
                emit(UiState.Error(root.message ?: "Gagal memuat data master siswa."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val emergencyLogsState: StateFlow<UiState<List<EmergencyIdentityLog>>> =
        repository.observeEmergencyLogs(currentUserId, isVerifiedStaff)
            .map<List<EmergencyIdentityLog>, UiState<List<EmergencyIdentityLog>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                emit(UiState.Error(root.message ?: "Gagal memuat log darurat."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val reportsState: StateFlow<UiState<List<BullyingReport>>> =
        repository.observeReports(currentUserId, isVerifiedStaff)
            .map<List<BullyingReport>, UiState<List<BullyingReport>>> { list ->
                UiState.Success(list)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Reports stream error", root)
                emit(UiState.Error(root.message ?: "Gagal memuat laporan."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val sosAlertsState: StateFlow<UiState<List<SosAlert>>> =
        repository.observeSosAlerts(currentUserId, isVerifiedStaff)
            .map<List<SosAlert>, UiState<List<SosAlert>>> { list ->
                UiState.Success(list)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "SOS stream error", root)
                emit(UiState.Error(root.message ?: "Gagal memuat sinyal darurat."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val counselingState: StateFlow<UiState<List<CounselingSession>>> =
        repository.observeCounselingSessions(currentUserId, isVerifiedStaff)
            .map<List<CounselingSession>, UiState<List<CounselingSession>>> { list ->
                UiState.Success(list)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Counseling stream error", root)
                emit(UiState.Error(root.message ?: "Gagal memuat jadwal konseling."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    fun setRoleMode(mode: UserRoleMode) {
        _roleMode.value = mode
    }

    fun setStaffSubRole(role: StaffSubRole) {
        _staffSubRole.value = role
    }

    fun setAssignedStaffClass(kelas: String) {
        _assignedStaffClass.value = normalizeClassToken(kelas)
    }

    fun toggleSoundAlerts(enabled: Boolean) {
        _soundAlertsEnabled.value = enabled
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun clearLastTicket() {
        _lastCreatedTicket.value = null
        _lastSubmittedRecipient.value = null
    }

    fun bypassStudentGateForPreview() {
        _isStudentGateBypassed.value = true
    }

    fun resetStudentCodeGate() {
        _isStudentGateBypassed.value = false
    }

    fun verifyStudentLogin(nisnOrCode: String, pinAkses: String) {
        if (_isVerifyingStudent.value) return
        _studentLoginError.value = null
        _isVerifyingStudent.value = true
        viewModelScope.launch {
            try {
                val res = repository.verifyStudentCredentials(nisnOrCode, pinAkses)
                res.onSuccess { access ->
                    _isStudentGateBypassed.value = false
                    _statusBanner.value = "Berhasil masuk sebagai ${access.kodeSiswa} (Kelas ${access.kelas})."
                }.onFailure { err ->
                    _studentLoginError.value = err.localizedMessage ?: "Gagal memverifikasi Kode Siswa."
                }
            } finally {
                _isVerifyingStudent.value = false
            }
        }
    }

    fun addStudentManual(
        nisn: String,
        namaLengkap: String,
        kelas: String,
        pinAkses: String
    ) {
        viewModelScope.launch {
            val currentList = (studentsState.value as? UiState.Success)?.data ?: emptyList()
            val token = normalizeClassToken(kelas)
            val classCount = currentList.count { normalizeClassToken(it.kelas) == token }
            repository.createStudentMaster(nisn, namaLengkap, kelas, pinAkses, classCount)
                .onSuccess { created ->
                    _statusBanner.value = "Siswa ditambahkan: Kode ${created.kodeSiswa} | PIN ${created.pinAkses}"
                }
                .onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "Gagal menambah siswa."
                }
        }
    }

    fun importStudentsCsv(csvContent: String) {
        viewModelScope.launch {
            val currentList = (studentsState.value as? UiState.Success)?.data ?: emptyList()
            repository.importStudentsFromCsv(csvContent, currentList)
                .onSuccess { count ->
                    _statusBanner.value = "$count data siswa berhasil diimpor dan diberi Kode Siswa otomatis."
                }
                .onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "Gagal mengimpor file CSV."
                }
        }
    }

    fun revealEmergencyIdentity(report: BullyingReport, reason: String) {
        viewModelScope.launch {
            repository.revealEmergencyIdentity(
                report = report,
                reason = reason,
                openedByName = _staffSubRole.value.title
            ).onSuccess { identity ->
                _statusBanner.value = "Identitas darurat dibuka & dicatat: $identity"
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Gagal membuka identitas darurat."
            }
        }
    }

    fun observeRealTimeAlerts(context: Context, sosList: List<SosAlert>, reports: List<BullyingReport>) {
        val activeSos = sosList.filter { it.status == "AKTIF" }
        val currentSosIds = activeSos.map { it.id }.toSet()
        if (!sosInitialized) {
            seenActiveSosIds = currentSosIds
            sosInitialized = true
        } else {
            val newlyAdded = activeSos.filter { it.id !in seenActiveSosIds }
            if (newlyAdded.isNotEmpty()) {
                val latest = newlyAdded.first()
                if (_soundAlertsEnabled.value) {
                    AlertNotifier.playSosAlarmSound()
                }
                AlertNotifier.postLocalNotification(
                    context = context,
                    isEmergency = true,
                    title = "DARURAT SOS: ${latest.studentCode.ifBlank { latest.reporterName }}",
                    body = "Lokasi: ${latest.locationLabel}. Segera buka Dasbor Guru BK.",
                    notificationId = latest.id.hashCode()
                )
            }
            seenActiveSosIds = currentSosIds
        }

        val currentReportIds = reports.map { it.id }.toSet()
        if (!reportsInitialized) {
            seenReportIds = currentReportIds
            reportsInitialized = true
        } else {
            val newReports = reports.filter { it.id !in seenReportIds && it.status == ReportStatus.BARU.name }
            if (newReports.isNotEmpty() && _roleMode.value == UserRoleMode.COUNSELOR) {
                val latest = newReports.first()
                if (_soundAlertsEnabled.value) {
                    AlertNotifier.playNewReportChime()
                }
                AlertNotifier.postLocalNotification(
                    context = context,
                    isEmergency = false,
                    title = "Laporan Baru #${latest.ticketCode}",
                    body = "Kode ${latest.studentCode.ifBlank { "Anonim" }} • ${latest.categoryEnum().label} • Prioritas ${latest.priorityEnum().label}",
                    notificationId = latest.id.hashCode()
                )
            }
            seenReportIds = currentReportIds
        }
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _isChatBusy.value) return
        _chatError.value = null

        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isFromUser = true,
            text = trimmed
        )
        val assistantMsgId = "a_${System.currentTimeMillis() + 1}"
        val priorHistory = _chatMessages.value

        _chatMessages.value = priorHistory + userMsg + ChatMessage(
            id = assistantMsgId,
            isFromUser = false,
            text = "",
            isStreaming = true
        )
        _isChatBusy.value = true

        viewModelScope.launch {
            try {
                aiRepository.streamChatReply(priorHistory, trimmed).collect { partial ->
                    _chatMessages.value = _chatMessages.value.map { msg ->
                        if (msg.id == assistantMsgId) {
                            msg.copy(text = partial, isStreaming = true)
                        } else msg
                    }
                }
                _chatMessages.value = _chatMessages.value.map { msg ->
                    if (msg.id == assistantMsgId) msg.copy(isStreaming = false) else msg
                }
            } catch (e: TimeoutCancellationException) {
                _chatMessages.value = _chatMessages.value.filterNot { it.id == assistantMsgId }
                _chatError.value = "Koneksi AI timeout. Coba kirim ulang pesanmu."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value.filterNot { it.id == assistantMsgId }
                _chatError.value = e.localizedMessage ?: "Gagal menghubungi AI Assistant."
            } finally {
                _isChatBusy.value = false
            }
        }
    }

    fun buildReportDraftFromChat(onDraftReady: () -> Unit) {
        if (_isChatBusy.value) return
        _chatError.value = null
        _isChatBusy.value = true
        viewModelScope.launch {
            try {
                val draft = aiRepository.extractDraftFromChat(_chatMessages.value)
                _reportDraft.value = draft
                _statusBanner.value = "Draf laporan berhasil disusun dari percakapanmu."
                onDraftReady()
            } catch (e: TimeoutCancellationException) {
                _chatError.value = "Penyusunan draf AI timeout. Coba lagi."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _chatError.value = e.localizedMessage ?: "Gagal menyusun draf laporan."
            } finally {
                _isChatBusy.value = false
            }
        }
    }

    fun requestCounselorFromChat(onNavigateToReport: () -> Unit) {
        val currentChronology = _chatMessages.value
            .filter { it.isFromUser }
            .joinToString(" ") { it.text }
            .ifBlank { "Siswa meminta terhubung langsung dengan Guru BK untuk pendampingan." }
        _reportDraft.value = _reportDraft.value.copy(
            chronology = currentChronology.take(2000),
            anonymous = false
        )
        _statusBanner.value = "Siap menghubungkan ke Guru BK. Lengkapi dan kirim laporanmu."
        onNavigateToReport()
    }

    fun submitReport(
        privacyCodeOnly: Boolean,
        recipientType: RecipientType,
        assignedTeacherName: String,
        assignedTeacherRole: String,
        category: BullyingCategory,
        studentName: String,
        studentClass: String,
        incidentTimeLabel: String,
        locationArea: String,
        latitude: Double?,
        longitude: Double?,
        chronology: String,
        evidenceNames: List<String>,
    ) {
        if (_isSubmittingReport.value) return
        _isSubmittingReport.value = true

        viewModelScope.launch {
            try {
                val triage = try {
                    aiRepository.analyzeReportTriage(
                        category = category,
                        locationArea = locationArea,
                        incidentTimeLabel = incidentTimeLabel,
                        chronology = chronology
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "AI triage fallback used during submit", e)
                    null
                }

                val profile = userProfile.value
                val studentCode = profile?.linkedStudentCode.orEmpty()
                val studentClassCode = profile?.linkedStudentClass.orEmpty()
                    .ifBlank { normalizeClassToken(studentClass) }

                val result = repository.createReport(
                    studentCode = studentCode,
                    studentClassCode = studentClassCode,
                    privacyCodeOnly = privacyCodeOnly,
                    recipientType = recipientType,
                    assignedTeacherName = assignedTeacherName,
                    assignedTeacherRole = assignedTeacherRole,
                    category = category,
                    studentName = studentName,
                    studentClass = studentClass.ifBlank { studentClassCode },
                    incidentTimeLabel = incidentTimeLabel,
                    locationArea = locationArea,
                    latitude = latitude,
                    longitude = longitude,
                    chronology = chronology,
                    evidenceNames = evidenceNames,
                    initialTriage = triage
                )

                result.onSuccess { created ->
                    _lastSubmittedRecipient.value = created.displayRecipientLabel()
                    _lastCreatedTicket.value = created.ticketCode
                    _reportDraft.value = ReportDraft()
                    _statusBanner.value = "Laporan #${created.ticketCode} dikirim ke ${created.displayRecipientLabel()}."
                }.onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "Gagal mengirim laporan."
                }
            } finally {
                _isSubmittingReport.value = false
            }
        }
    }

    fun triggerSosEmergency(
        context: Context,
        locationLabel: String = "Area Sekolah (Tombol Darurat)",
        onComplete: (SosAlert?) -> Unit
    ) {
        if (_isFetchingGps.value) return
        _isFetchingGps.value = true
        viewModelScope.launch {
            try {
                val gps = LocationHelper.fetchCurrentLocation(context)
                val lat = gps?.latitude ?: -6.2088
                val lng = gps?.longitude ?: 106.8456
                val hasFix = gps?.isLiveFix == true
                val profile = userProfile.value
                val studentCode = profile?.linkedStudentCode.orEmpty()
                val studentClassCode = profile?.linkedStudentClass.orEmpty()
                val label = if (hasFix) {
                    "$locationLabel • GPS Aktif"
                } else {
                    "$locationLabel • Perkiraan Lokasi"
                }
                val result = repository.sendSosAlert(
                    studentCode = studentCode,
                    studentClassCode = studentClassCode,
                    reporterName = studentCode.ifBlank { userDisplayName.ifBlank { "Siswa (Darurat)" } },
                    locationLabel = label,
                    latitude = lat,
                    longitude = lng,
                    hasGpsFix = hasFix
                )
                result.onSuccess { alert ->
                    if (_soundAlertsEnabled.value) {
                        AlertNotifier.playSosAlarmSound()
                    }
                    _statusBanner.value = "Sinyal Darurat SOS terkirim ke Guru BK!"
                    onComplete(alert)
                }.onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "Gagal mengirim SOS."
                    onComplete(null)
                }
            } finally {
                _isFetchingGps.value = false
            }
        }
    }

    fun resolveSosAlert(alertId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateSosAlertStatus(
                alertId = alertId,
                status = newStatus,
                handledBy = _staffSubRole.value.title
            ).onSuccess {
                _statusBanner.value = "Status sinyal darurat diperbarui menjadi $newStatus."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Gagal memperbarui SOS."
            }
        }
    }

    fun saveHumanDecision(
        report: BullyingReport,
        status: ReportStatus,
        priority: AiPriority,
        decision: HumanDecision,
        decisionNote: String,
        bkMessage: String,
        waliKelasNotes: String,
        createCounseling: Boolean,
        scheduleLabel: String
    ) {
        viewModelScope.launch {
            val stageIdx = when (status) {
                ReportStatus.BARU -> 0L
                ReportStatus.DIPROSES -> 1L
                ReportStatus.TINDAK_LANJUT -> 2L
                ReportStatus.SELESAI -> 3L
            }
            val result = repository.updateReportDecision(
                reportId = report.id,
                status = status,
                stageIndex = stageIdx,
                priority = priority,
                decision = decision,
                decisionNote = decisionNote,
                bkMessage = bkMessage,
                waliKelasNotes = waliKelasNotes
            )
            result.onSuccess {
                if (createCounseling) {
                    val label = if (!report.privacyCodeOnly && report.studentName.isNotBlank()) {
                        "${report.studentName} (${report.studentClass})"
                    } else {
                        "${report.studentCode.ifBlank { "Siswa Anonim" }} (#${report.ticketCode})"
                    }
                    repository.createCounselingSession(
                        reporterId = report.reporterId,
                        reportId = report.id,
                        ticketCode = report.ticketCode,
                        studentLabel = label,
                        scheduleLabel = scheduleLabel.ifBlank { "Hari ini, 13.30 WIB" },
                        counselorName = _staffSubRole.value.title,
                        notes = decisionNote.ifBlank { "Sesi konseling tindak lanjut laporan #${report.ticketCode}." }
                    )
                }
                _statusBanner.value = "Keputusan Guru BK untuk #${report.ticketCode} tersimpan."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Gagal menyimpan keputusan."
            }
        }
    }

    fun rerunAiTriageForReport(report: BullyingReport) {
        viewModelScope.launch {
            try {
                val triage = aiRepository.analyzeReportTriage(
                    category = report.categoryEnum(),
                    locationArea = report.locationArea,
                    incidentTimeLabel = report.incidentTimeLabel,
                    chronology = report.chronology
                )
                repository.attachAiTriage(report.id, triage).onSuccess {
                    _statusBanner.value = "Analisis AI untuk #${report.ticketCode} diperbarui."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusBanner.value = e.localizedMessage ?: "Gagal menjalankan analisis AI."
            }
        }
    }

    fun advanceCounselingStage(session: CounselingSession) {
        viewModelScope.launch {
            val nextStage = (session.stageIndex + 1L).coerceAtMost(3L)
            val nextStatus = when (nextStage) {
                0L, 1L -> "TERJADWAL"
                2L -> "BERLANGSUNG"
                else -> "SELESAI"
            }
            repository.updateCounselingSession(
                sessionId = session.id,
                stageIndex = nextStage,
                status = nextStatus,
                notes = session.notes
            ).onSuccess {
                _statusBanner.value = "Tahap pendampingan #${session.ticketCode} diperbarui."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Gagal memperbarui tahap."
            }
        }
    }

    fun generateAiPreventionInsight(reports: List<BullyingReport>) {
        if (_isGeneratingInsight.value) return
        _isGeneratingInsight.value = true
        _insightError.value = null
        viewModelScope.launch {
            try {
                val total = reports.size
                val byCat = BullyingCategory.entries.joinToString(", ") { cat ->
                    val count = reports.count { it.categoryEnum() == cat }
                    "${cat.label}: $count"
                }
                val byLoc = reports.groupingBy { it.locationArea }.eachCount()
                    .entries.sortedByDescending { it.value }
                    .joinToString(", ") { "${it.key}: ${it.value}" }
                val highCount = reports.count { it.priorityEnum() == AiPriority.TINGGI }
                val summary = "Total laporan: $total | Prioritas Tinggi: $highCount | Kategori: [$byCat] | Lokasi: [$byLoc]"
                val insight = aiRepository.generatePreventionInsight(summary)
                _preventionInsight.value = insight
            } catch (e: TimeoutCancellationException) {
                _insightError.value = "Analisis pencegahan AI timeout. Coba lagi."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _insightError.value = e.localizedMessage ?: "Gagal menyusun insight pencegahan."
            } finally {
                _isGeneratingInsight.value = false
            }
        }
    }

    fun saveProfile(displayName: String, className: String) {
        viewModelScope.launch {
            repository.saveUserProfile(displayName, className, _roleMode.value).onSuccess {
                _statusBanner.value = "Profil berhasil disimpan."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Gagal menyimpan profil."
            }
        }
    }

    private companion object {
        const val TAG = "MainViewModel"
    }
}
