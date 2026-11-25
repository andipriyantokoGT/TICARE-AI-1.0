package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class BullyingCategory(val label: String, val subtitle: String) {
    VERBAL("Verbal", "Ucapan menyakitkan, ejekan, hinaan"),
    FISIK("Fisik", "Kekerasan fisik, dorongan, pemukulan"),
    SIBER("Siber", "Perundungan di media sosial & pesan"),
    SOSIAL("Sosial", "Pengucilan, penyebaran isu & fitnah"),
    LAINNYA("Lainnya", "Bentuk perundungan atau intimidasi lain");

    companion object {
        fun fromRaw(raw: String?): BullyingCategory =
            entries.firstOrNull { it.name == raw } ?: VERBAL
    }
}

enum class RecipientType(val label: String, val subtitle: String) {
    GURU_BK(
        label = "Guru BK",
        subtitle = "Penanganan konseling, mediasi, & tindak lanjut tim BK"
    ),
    GURU_LAINNYA(
        label = "Guru Lainnya",
        subtitle = "Wali Kelas atau Guru Mata Pelajaran yang paling dipercaya"
    );

    companion object {
        fun fromRaw(raw: String?): RecipientType =
            entries.firstOrNull { it.name == raw } ?: GURU_BK
    }
}

data class SchoolTeacherOption(
    val id: String,
    val name: String,
    val roleCode: String,
    val roleLabel: String,
)

val DEFAULT_GURU_BK_OPTIONS = listOf(
    SchoolTeacherOption("bk_team", "Tim BK (Siapa Saja yang Tersedia)", "GURU_BK", "Antrean Tim BK Sekolah"),
    SchoolTeacherOption("bk_rani", "Bu Rani, S.Pd.", "GURU_BK", "Guru Bimbingan Konseling"),
    SchoolTeacherOption("bk_hendra", "Pak Hendra, S.Pd.", "GURU_BK", "Konselor Pendampingan"),
)

val DEFAULT_GURU_LAINNYA_OPTIONS = listOf(
    SchoolTeacherOption("wali_kelas_saya", "Wali Kelas Saya", "WALI_KELAS", "Otomatis sesuai kelasmu"),
    SchoolTeacherOption("wali_budi", "Pak Budi, S.Pd.", "WALI_KELAS", "Wali Kelas"),
    SchoolTeacherOption("mapel_sari", "Bu Sari, M.Pd.", "GURU_MAPEL", "Guru Mata Pelajaran"),
    SchoolTeacherOption("mapel_dimas", "Pak Dimas, S.Pd.", "GURU_MAPEL", "Guru Mata Pelajaran / Pembina OSIS"),
)

enum class ReportStatus(val label: String) {
    BARU("Baru"),
    DIPROSES("Diproses"),
    TINDAK_LANJUT("Tindak Lanjut"),
    SELESAI("Selesai");

    companion object {
        fun fromRaw(raw: String?): ReportStatus =
            entries.firstOrNull { it.name == raw } ?: BARU
    }
}

enum class AiPriority(val label: String) {
    TINGGI("Tinggi"),
    SEDANG("Sedang"),
    RENDAH("Rendah");

    companion object {
        fun fromRaw(raw: String?): AiPriority =
            entries.firstOrNull { it.name == raw } ?: SEDANG
    }
}

enum class HumanDecision(val label: String) {
    MENUNGGU("Menunggu Keputusan BK"),
    DISETUJUI("Disetujui Guru BK"),
    DIMODIFIKASI("Dimodifikasi Guru BK"),
    DITOLAK("Ditolak / Perlu Verifikasi Ulang");

    companion object {
        fun fromRaw(raw: String?): HumanDecision =
            entries.firstOrNull { it.name == raw } ?: MENUNGGU
    }
}

enum class UserRoleMode {
    STUDENT,
    COUNSELOR;

    companion object {
        fun fromRaw(raw: String?): UserRoleMode =
            entries.firstOrNull { it.name == raw } ?: STUDENT
    }
}

enum class StaffSubRole(val title: String, val subtitle: String) {
    GURU_BK("Bu Rani, S.Pd.", "Guru Bimbingan Konseling"),
    WALI_KELAS("Pak Budi, S.Pd.", "Wali Kelas"),
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val className: String = "",
    val preferredMode: String = UserRoleMode.STUDENT.name,
    val linkedStudentCode: String = "",
    val linkedStudentClass: String = "",
    val assignedStaffClass: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class StudentMaster(
    @DocumentId val id: String = "",
    val nisn: String = "",
    val namaLengkap: String = "",
    val kelas: String = "",
    val kodeSiswa: String = "",
    val pinAkses: String = "",
    val claimedUid: String = "",
    val createdByUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun censoredName(): String = censorFullName(namaLengkap)
}

@Keep
@IgnoreExtraProperties
data class StudentAccess(
    @DocumentId val id: String = "",
    val studentDocId: String = "",
    val kodeSiswa: String = "",
    val kelas: String = "",
    val active: Boolean = true,
    val createdByUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class EmergencyIdentityLog(
    @DocumentId val id: String = "",
    val reportId: String = "",
    val ticketCode: String = "",
    val studentCode: String = "",
    val reason: String = "",
    val openedByUid: String = "",
    val openedByName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class BullyingReport(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val ticketCode: String = "",
    val studentCode: String = "",
    val studentClassCode: String = "",
    val privacyCodeOnly: Boolean = true,
    val category: String = BullyingCategory.VERBAL.name,
    val recipientType: String = RecipientType.GURU_BK.name,
    val assignedTeacherName: String = "Tim BK (Siapa Saja yang Tersedia)",
    val assignedTeacherRole: String = "GURU_BK",
    val anonymous: Boolean = true,
    val studentName: String = "",
    val studentClass: String = "",
    val incidentTimeLabel: String = "",
    val locationArea: String = "Kelas",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val chronology: String = "",
    val evidenceNames: List<String> = emptyList(),
    val status: String = ReportStatus.BARU.name,
    val stageIndex: Long = 0L,
    val bkMessage: String = "",
    val waliKelasNotes: String = "",
    val aiSummary: String = "",
    val aiPriority: String = AiPriority.SEDANG.name,
    val aiRecommendation: String = "",
    val aiTrendNote: String = "",
    val humanDecision: String = HumanDecision.MENUNGGU.name,
    val humanDecisionNote: String = "",
    val emergencyRevealedName: String = "",
    val emergencyRevealReason: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun categoryEnum(): BullyingCategory = BullyingCategory.fromRaw(category)
    fun recipientTypeEnum(): RecipientType = RecipientType.fromRaw(recipientType)
    fun statusEnum(): ReportStatus = ReportStatus.fromRaw(status)
    fun priorityEnum(): AiPriority = AiPriority.fromRaw(aiPriority)
    fun decisionEnum(): HumanDecision = HumanDecision.fromRaw(humanDecision)

    fun displayRecipientLabel(): String {
        val typeLabel = recipientTypeEnum().label
        val target = assignedTeacherName.ifBlank { "Tim BK (Siapa Saja yang Tersedia)" }
        return "$typeLabel • $target"
    }

    fun displayReporterLabel(): String {
        if (emergencyRevealedName.isNotBlank()) {
            return "$emergencyRevealedName [Dibuka Darurat]"
        }
        return if (!privacyCodeOnly && studentName.isNotBlank()) {
            val cls = studentClass.ifBlank { studentClassCode }
            val codeSuffix = if (studentCode.isNotBlank()) " • $studentCode" else ""
            "$studentName (${cls.ifBlank { "-" }})$codeSuffix"
        } else {
            val code = studentCode.ifBlank { "Anonim Terenkripsi" }
            val cls = if (studentClassCode.isNotBlank()) " • Kelas $studentClassCode" else ""
            "$code$cls (Mode Privasi)"
        }
    }
}

@Keep
@IgnoreExtraProperties
data class SosAlert(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val studentCode: String = "",
    val studentClassCode: String = "",
    val reporterName: String = "",
    val locationLabel: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val hasGpsFix: Boolean = false,
    val status: String = "AKTIF",
    val handledBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@Keep
@IgnoreExtraProperties
data class CounselingSession(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val reportId: String = "",
    val ticketCode: String = "",
    val studentLabel: String = "",
    val scheduleLabel: String = "",
    val counselorName: String = "",
    val stageIndex: Long = 0L,
    val notes: String = "",
    val status: String = "TERJADWAL",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

data class ChatMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false,
)

data class ReportDraft(
    val recipientType: RecipientType = RecipientType.GURU_BK,
    val assignedTeacherName: String = "Tim BK (Siapa Saja yang Tersedia)",
    val category: BullyingCategory = BullyingCategory.VERBAL,
    val locationArea: String = "Kelas",
    val incidentTimeLabel: String = "",
    val chronology: String = "",
    val anonymous: Boolean = true,
)

data class AiTriageResult(
    val summary: String,
    val priority: AiPriority,
    val recommendation: String,
    val trendNote: String,
)

fun censorFullName(fullName: String): String {
    val parts = fullName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    if (parts.isEmpty()) return "***"
    return parts.joinToString(" ") { word ->
        if (word.length <= 1) "*"
        else word.first() + "*".repeat((word.length - 1).coerceAtLeast(2))
    }
}

fun normalizeClassToken(kelas: String): String {
    val cleaned = kelas.uppercase().replace(Regex("[^A-Z0-9]"), "")
    return cleaned.ifBlank { "UMUM" }.take(8)
}

fun generateStudentCode(kelas: String, seqNumber: Int): String {
    val classToken = normalizeClassToken(kelas)
    val suffix = "%03d".format(seqNumber.coerceIn(1, 999))
    return "SW-$classToken-$suffix"
}
