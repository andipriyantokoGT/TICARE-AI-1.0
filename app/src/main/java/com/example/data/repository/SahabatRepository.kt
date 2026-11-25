package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.core.OperationType
import com.example.core.handleFirestoreError
import com.example.data.model.AiPriority
import com.example.data.model.AiTriageResult
import com.example.data.model.BullyingCategory
import com.example.data.model.BullyingReport
import com.example.data.model.CounselingSession
import com.example.data.model.EmergencyIdentityLog
import com.example.data.model.HumanDecision
import com.example.data.model.RecipientType
import com.example.data.model.ReportStatus
import com.example.data.model.SosAlert
import com.example.data.model.StudentAccess
import com.example.data.model.StudentMaster
import com.example.data.model.UserProfile
import com.example.data.model.UserRoleMode
import com.example.data.model.generateStudentCode
import com.example.data.model.normalizeClassToken
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class SahabatRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("Pengguna harus masuk dengan Google terlebih dahulu.")

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else null
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveUserProfile(
        displayName: String,
        className: String,
        preferredMode: UserRoleMode,
        linkedStudentCode: String? = null,
        linkedStudentClass: String? = null,
        assignedStaffClass: String? = null,
    ): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val cleanName = displayName.trim().take(80).ifBlank { "Sahabat Siswa" }
            val cleanClass = className.trim().take(40)
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            val existing = docRef.get().await()
            if (existing.exists()) {
                val updates = mutableMapOf<String, Any>(
                    "displayName" to cleanName,
                    "preferredMode" to preferredMode.name,
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
                if (cleanClass.isNotEmpty()) updates["className"] = cleanClass
                if (linkedStudentCode != null) updates["linkedStudentCode"] = linkedStudentCode.take(30)
                if (linkedStudentClass != null) updates["linkedStudentClass"] = linkedStudentClass.take(30)
                if (assignedStaffClass != null) updates["assignedStaffClass"] = assignedStaffClass.take(30)
                docRef.update(updates).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to cleanName,
                    "className" to cleanClass.ifEmpty { null },
                    "preferredMode" to preferredMode.name,
                    "linkedStudentCode" to linkedStudentCode?.take(30),
                    "linkedStudentClass" to linkedStudentClass?.take(30),
                    "assignedStaffClass" to assignedStaffClass?.take(30),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    // --- Master Students & Pseudonymization ---

    fun observeStudents(userId: String, staffScope: Boolean): Flow<List<StudentMaster>> = flow {
        val path = "students"
        val query = if (staffScope) {
            db.collection(path)
        } else {
            db.collection(path).whereEqualTo("claimedUid", userId)
        }
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StudentMaster::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.kodeSiswa }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createStudentMaster(
        nisn: String,
        namaLengkap: String,
        kelas: String,
        pinAkses: String,
        existingStudentsCountInClass: Int
    ): Result<StudentMaster> {
        var docPath = "students"
        return try {
            val uid = requireUserId()
            val cleanNisn = nisn.trim().replace(Regex("[^a-zA-Z0-9]"), "").take(30)
            val cleanName = namaLengkap.trim().take(100)
            val cleanKelas = kelas.trim().uppercase().take(30)
            require(cleanNisn.length >= 4) { "NISN minimal 4 digit." }
            require(cleanName.length >= 2) { "Nama lengkap minimal 2 karakter." }
            require(cleanKelas.isNotEmpty()) { "Kelas wajib diisi." }

            val cleanPin = pinAkses.trim().replace(Regex("[^a-zA-Z0-9]"), "").take(20)
                .ifBlank { "%06d".format(Random.nextInt(100000, 999999)) }
            require(cleanPin.length >= 4) { "PIN akses minimal 4 karakter." }

            val seq = (existingStudentsCountInClass + 1).coerceAtLeast(1)
            val kodeSiswa = generateStudentCode(cleanKelas, seq)
            val studentRef = db.collection("students").document(kodeSiswa)
            docPath = studentRef.path

            val studentPayload = mapOf(
                "nisn" to cleanNisn,
                "namaLengkap" to cleanName,
                "kelas" to cleanKelas,
                "kodeSiswa" to kodeSiswa,
                "pinAkses" to cleanPin,
                "claimedUid" to "",
                "createdByUid" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            )

            val nisnAccessId = "nisn_${cleanNisn}_$cleanPin"
            val codeAccessId = "code_${kodeSiswa}_$cleanPin"
            val accessPayload = mapOf(
                "studentDocId" to kodeSiswa,
                "kodeSiswa" to kodeSiswa,
                "kelas" to cleanKelas,
                "active" to true,
                "createdByUid" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
            )

            val batch = db.batch()
            batch.set(studentRef, studentPayload)
            batch.set(db.collection("student_access").document(nisnAccessId), accessPayload)
            batch.set(db.collection("student_access").document(codeAccessId), accessPayload)
            batch.commit().await()

            Result.success(
                StudentMaster(
                    id = kodeSiswa,
                    nisn = cleanNisn,
                    namaLengkap = cleanName,
                    kelas = cleanKelas,
                    kodeSiswa = kodeSiswa,
                    pinAkses = cleanPin,
                    claimedUid = "",
                    createdByUid = uid
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun importStudentsFromCsv(
        csvContent: String,
        existingStudents: List<StudentMaster>
    ): Result<Int> {
        return try {
            requireUserId()
            val lines = csvContent.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            if (lines.isEmpty()) return Result.failure(IllegalArgumentException("File CSV kosong."))

            val classCounts = existingStudents
                .groupBy { normalizeClassToken(it.kelas) }
                .mapValues { it.value.size }
                .toMutableMap()

            var imported = 0
            lines.forEachIndexed { idx, line ->
                val delimiter = if (line.contains(';')) ';' else ','
                val cols = line.split(delimiter).map { it.trim().trim('"') }
                if (idx == 0 && cols.firstOrNull()?.equals("nisn", ignoreCase = true) == true) {
                    return@forEachIndexed
                }
                if (cols.size >= 3) {
                    val nisn = cols[0]
                    val nama = cols[1]
                    val kelas = cols[2]
                    val pin = cols.getOrNull(3).orEmpty()
                    val token = normalizeClassToken(kelas)
                    val curCount = classCounts[token] ?: 0
                    val res = createStudentMaster(nisn, nama, kelas, pin, curCount)
                    if (res.isSuccess) {
                        classCounts[token] = curCount + 1
                        imported += 1
                    }
                }
            }
            if (imported == 0) {
                Result.failure(IllegalArgumentException("Tidak ada baris valid. Format: nisn,nama_lengkap,kelas,pin_akses"))
            } else {
                Result.success(imported)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "students")
            Result.failure(e)
        }
    }

    suspend fun verifyStudentCredentials(
        nisnOrCode: String,
        pinAkses: String
    ): Result<StudentAccess> {
        var docPath = "student_access"
        return try {
            val uid = requireUserId()
            val rawId = nisnOrCode.trim()
            val cleanPin = pinAkses.trim().replace(Regex("[^a-zA-Z0-9]"), "")
            require(rawId.length >= 4 && cleanPin.length >= 4) {
                "Masukkan NISN/Kode Siswa dan PIN Akses yang valid."
            }

            val normalizedUpper = rawId.uppercase()
            val candidateIds = listOf(
                "nisn_${rawId.replace(Regex("[^a-zA-Z0-9]"), "")}_$cleanPin",
                "code_${normalizedUpper}_$cleanPin"
            )

            var matched: StudentAccess? = null
            for (accessId in candidateIds) {
                docPath = "student_access/$accessId"
                val snap = db.collection("student_access").document(accessId).get().await()
                if (snap.exists()) {
                    val parsed = snap.toObject(
                        StudentAccess::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                    if (parsed != null && parsed.active) {
                        matched = parsed
                        break
                    }
                }
            }

            val access = matched
                ?: return Result.failure(IllegalArgumentException("NISN/Kode Siswa atau PIN Akses tidak cocok."))

            // Claim student document if unclaimed so the student owns their own pseudonymous link
            try {
                val studentRef = db.collection("students").document(access.studentDocId)
                studentRef.update(
                    mapOf(
                        "claimedUid" to uid,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (_: Exception) {
                // Already claimed by this user or another session; still save profile link below
            }

            saveUserProfile(
                displayName = access.kodeSiswa,
                className = access.kelas,
                preferredMode = UserRoleMode.STUDENT,
                linkedStudentCode = access.kodeSiswa,
                linkedStudentClass = access.kelas
            )

            Result.success(access)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docPath)
            Result.failure(e)
        }
    }

    // --- Emergency Identity Reveal Protocol (Guru BK Only) ---

    suspend fun revealEmergencyIdentity(
        report: BullyingReport,
        reason: String,
        openedByName: String
    ): Result<String> {
        val reportPath = "reports/${report.id}"
        return try {
            val uid = requireUserId()
            val cleanReason = reason.trim().take(500)
            require(cleanReason.length >= 5) { "Alasan darurat minimal 5 karakter." }

            val code = report.studentCode.trim()
            val resolvedIdentity = if (code.isNotBlank()) {
                val studentSnap = db.collection("students").document(code).get().await()
                val student = studentSnap.toObject(
                    StudentMaster::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                if (student != null && student.namaLengkap.isNotBlank()) {
                    "${student.namaLengkap} • NISN ${student.nisn} • Kelas ${student.kelas}"
                } else {
                    report.studentName.ifBlank { "Data master siswa $code tidak ditemukan" }
                }
            } else {
                report.studentName.ifBlank { "Pelapor anonim tanpa kode siswa" }
            }

            val logRef = db.collection("emergency_identity_logs").document()
            val batch = db.batch()
            batch.update(
                db.collection("reports").document(report.id),
                mapOf(
                    "emergencyRevealedName" to resolvedIdentity.take(100),
                    "emergencyRevealReason" to cleanReason,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(
                logRef,
                mapOf(
                    "reportId" to report.id,
                    "ticketCode" to report.ticketCode,
                    "studentCode" to code.ifBlank { "ANONIM" },
                    "reason" to cleanReason,
                    "openedByUid" to uid,
                    "openedByName" to openedByName.take(100),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(resolvedIdentity)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reportPath)
            Result.failure(e)
        }
    }

    fun observeEmergencyLogs(userId: String, staffScope: Boolean): Flow<List<EmergencyIdentityLog>> = flow {
        if (!staffScope) {
            emit(emptyList())
            return@flow
        }
        val path = "emergency_identity_logs"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        EmergencyIdentityLog::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    // --- Reports ---

    fun observeReports(userId: String, staffScope: Boolean): Flow<List<BullyingReport>> = flow {
        val path = "reports"
        val query = if (staffScope) {
            db.collection(path)
        } else {
            db.collection(path).whereEqualTo("reporterId", userId)
        }
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BullyingReport::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createReport(
        studentCode: String,
        studentClassCode: String,
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
        initialTriage: AiTriageResult?,
    ): Result<BullyingReport> {
        var docPath = "reports"
        return try {
            val uid = requireUserId()
            val cleanChronology = chronology.trim().take(3000)
            require(cleanChronology.length >= 5) { "Kronologi minimal 5 karakter." }

            val docRef = db.collection("reports").document()
            docPath = docRef.path
            val ticketCode = "LP-${Random.nextInt(1000, 9999)}"
            val cleanEvidence = evidenceNames.take(5).map { it.take(300) }
            val cleanTeacherName = assignedTeacherName.trim().take(100)
                .ifBlank {
                    if (recipientType == RecipientType.GURU_BK) {
                        "Tim BK (Siapa Saja yang Tersedia)"
                    } else {
                        "Wali Kelas"
                    }
                }
            val cleanTeacherRole = assignedTeacherRole.trim().take(40)
                .ifBlank {
                    if (recipientType == RecipientType.GURU_BK) "GURU_BK" else "GURU_MAPEL"
                }

            val priority = initialTriage?.priority ?: when (category) {
                BullyingCategory.FISIK -> AiPriority.TINGGI
                BullyingCategory.SIBER -> AiPriority.SEDANG
                else -> AiPriority.SEDANG
            }
            val summary = (initialTriage?.summary
                ?: "Laporan ${category.label} di $locationArea: ${cleanChronology.take(120)}")
                .take(1000)
            val recommendation = (initialTriage?.recommendation
                ?: "Lakukan verifikasi awal dan jadwalkan pendampingan bersama $cleanTeacherName.")
                .take(1500)
            val trendNote = (initialTriage?.trendNote
                ?: "Lokasi $locationArea memerlukan perhatian pengawasan tambahan.")
                .take(500)

            val safeStudentName = if (privacyCodeOnly) "" else studentName.trim().take(80)
            val safeStudentClass = if (privacyCodeOnly) studentClassCode.take(40) else studentClass.trim().take(40)
            val ackMessage = if (recipientType == RecipientType.GURU_BK) {
                "Laporanmu telah diterima aman dan diteruskan ke $cleanTeacherName."
            } else {
                "Laporanmu telah diterima aman dan diteruskan ke $cleanTeacherName."
            }

            val payload = mapOf(
                "reporterId" to uid,
                "ticketCode" to ticketCode,
                "studentCode" to studentCode.trim().take(30),
                "studentClassCode" to studentClassCode.trim().uppercase().take(30),
                "privacyCodeOnly" to privacyCodeOnly,
                "category" to category.name,
                "recipientType" to recipientType.name,
                "assignedTeacherName" to cleanTeacherName,
                "assignedTeacherRole" to cleanTeacherRole,
                "anonymous" to privacyCodeOnly,
                "studentName" to safeStudentName,
                "studentClass" to safeStudentClass,
                "incidentTimeLabel" to incidentTimeLabel.trim().take(80).ifBlank { "Hari ini" },
                "locationArea" to locationArea.trim().take(60).ifBlank { "Kelas" },
                "latitude" to latitude,
                "longitude" to longitude,
                "chronology" to cleanChronology,
                "evidenceNames" to cleanEvidence,
                "status" to ReportStatus.BARU.name,
                "stageIndex" to 0L,
                "bkMessage" to ackMessage,
                "waliKelasNotes" to "",
                "aiSummary" to summary,
                "aiPriority" to priority.name,
                "aiRecommendation" to recommendation,
                "aiTrendNote" to trendNote,
                "humanDecision" to HumanDecision.MENUNGGU.name,
                "humanDecisionNote" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            ).filterValues { it != null }

            docRef.set(payload).await()

            Result.success(
                BullyingReport(
                    id = docRef.id,
                    reporterId = uid,
                    ticketCode = ticketCode,
                    studentCode = studentCode,
                    studentClassCode = studentClassCode.uppercase(),
                    privacyCodeOnly = privacyCodeOnly,
                    category = category.name,
                    recipientType = recipientType.name,
                    assignedTeacherName = cleanTeacherName,
                    assignedTeacherRole = cleanTeacherRole,
                    anonymous = privacyCodeOnly,
                    studentName = safeStudentName,
                    studentClass = safeStudentClass,
                    incidentTimeLabel = incidentTimeLabel.ifBlank { "Hari ini" },
                    locationArea = locationArea.ifBlank { "Kelas" },
                    latitude = latitude,
                    longitude = longitude,
                    chronology = cleanChronology,
                    evidenceNames = cleanEvidence,
                    status = ReportStatus.BARU.name,
                    stageIndex = 0L,
                    bkMessage = ackMessage,
                    aiSummary = summary,
                    aiPriority = priority.name,
                    aiRecommendation = recommendation,
                    aiTrendNote = trendNote,
                    humanDecision = HumanDecision.MENUNGGU.name,
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun attachAiTriage(reportId: String, triage: AiTriageResult): Result<Unit> {
        val docPath = "reports/$reportId"
        return try {
            requireUserId()
            require(reportId.isNotBlank())
            db.collection("reports").document(reportId).update(
                mapOf(
                    "aiSummary" to triage.summary.take(1000),
                    "aiPriority" to triage.priority.name,
                    "aiRecommendation" to triage.recommendation.take(1500),
                    "aiTrendNote" to triage.trendNote.take(500),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateReportDecision(
        reportId: String,
        status: ReportStatus,
        stageIndex: Long,
        priority: AiPriority,
        decision: HumanDecision,
        decisionNote: String,
        bkMessage: String,
        waliKelasNotes: String,
    ): Result<Unit> {
        val docPath = "reports/$reportId"
        return try {
            requireUserId()
            require(reportId.isNotBlank())
            db.collection("reports").document(reportId).update(
                mapOf(
                    "status" to status.name,
                    "stageIndex" to stageIndex.coerceIn(0L, 3L),
                    "aiPriority" to priority.name,
                    "humanDecision" to decision.name,
                    "humanDecisionNote" to decisionNote.trim().take(1000),
                    "bkMessage" to bkMessage.trim().take(1000),
                    "waliKelasNotes" to waliKelasNotes.trim().take(1000),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    // --- SOS Alerts ---

    fun observeSosAlerts(userId: String, staffScope: Boolean): Flow<List<SosAlert>> = flow {
        val path = "sos_alerts"
        val query = if (staffScope) {
            db.collection(path)
        } else {
            db.collection(path).whereEqualTo("reporterId", userId)
        }
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        SosAlert::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun sendSosAlert(
        studentCode: String,
        studentClassCode: String,
        reporterName: String,
        locationLabel: String,
        latitude: Double,
        longitude: Double,
        hasGpsFix: Boolean
    ): Result<SosAlert> {
        var docPath = "sos_alerts"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("sos_alerts").document()
            docPath = docRef.path
            val cleanCode = studentCode.trim().take(30)
            val cleanClass = studentClassCode.trim().uppercase().take(30)
            val cleanName = if (cleanCode.isNotBlank()) cleanCode else reporterName.trim().take(80).ifBlank { "Siswa (Darurat)" }
            val cleanLocation = locationLabel.trim().take(160).ifBlank { "Area Sekolah" }
            val payload = mapOf(
                "reporterId" to uid,
                "studentCode" to cleanCode,
                "studentClassCode" to cleanClass,
                "reporterName" to cleanName,
                "locationLabel" to cleanLocation,
                "latitude" to latitude,
                "longitude" to longitude,
                "hasGpsFix" to hasGpsFix,
                "status" to "AKTIF",
                "handledBy" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            )
            docRef.set(payload).await()
            Result.success(
                SosAlert(
                    id = docRef.id,
                    reporterId = uid,
                    studentCode = cleanCode,
                    studentClassCode = cleanClass,
                    reporterName = cleanName,
                    locationLabel = cleanLocation,
                    latitude = latitude,
                    longitude = longitude,
                    hasGpsFix = hasGpsFix,
                    status = "AKTIF",
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateSosAlertStatus(
        alertId: String,
        status: String,
        handledBy: String
    ): Result<Unit> {
        val docPath = "sos_alerts/$alertId"
        return try {
            requireUserId()
            require(alertId.isNotBlank())
            db.collection("sos_alerts").document(alertId).update(
                mapOf(
                    "status" to status,
                    "handledBy" to handledBy.trim().take(100),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    // --- Counseling Sessions ---

    fun observeCounselingSessions(
        userId: String,
        staffScope: Boolean
    ): Flow<List<CounselingSession>> = flow {
        val path = "counseling_sessions"
        val query = if (staffScope) {
            db.collection(path)
        } else {
            db.collection(path).whereEqualTo("reporterId", userId)
        }
        emitAll(
            query.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CounselingSession::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createCounselingSession(
        reporterId: String,
        reportId: String,
        ticketCode: String,
        studentLabel: String,
        scheduleLabel: String,
        counselorName: String,
        notes: String
    ): Result<Unit> {
        var docPath = "counseling_sessions"
        return try {
            requireUserId()
            val docRef = db.collection("counseling_sessions").document()
            docPath = docRef.path
            val payload = mapOf(
                "reporterId" to reporterId,
                "reportId" to reportId.take(128),
                "ticketCode" to ticketCode.take(20),
                "studentLabel" to studentLabel.trim().take(80).ifBlank { "Siswa" },
                "scheduleLabel" to scheduleLabel.trim().take(80).ifBlank { "Hari ini, 13.30 WIB" },
                "counselorName" to counselorName.trim().take(80).ifBlank { "Bu Rani, S.Pd." },
                "stageIndex" to 1L,
                "notes" to notes.trim().take(1000),
                "status" to "TERJADWAL",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateCounselingSession(
        sessionId: String,
        stageIndex: Long,
        status: String,
        notes: String
    ): Result<Unit> {
        val docPath = "counseling_sessions/$sessionId"
        return try {
            requireUserId()
            require(sessionId.isNotBlank())
            db.collection("counseling_sessions").document(sessionId).update(
                mapOf(
                    "stageIndex" to stageIndex.coerceIn(0L, 3L),
                    "status" to status,
                    "notes" to notes.trim().take(1000),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }
}
