package com.example.data.repository

import android.util.Log
import com.example.data.model.AiPriority
import com.example.data.model.AiTriageResult
import com.example.data.model.BullyingCategory
import com.example.data.model.ChatMessage
import com.example.data.model.ReportDraft
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

class AiRepository {
    private val companionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_FLASH,
            systemInstruction = content {
                text(
                    """
                    Kamu adalah 'Sahabat Curhatmu', asisten konselor AI berbasis Trauma-Informed Care di aplikasi TICARE AI (Trauma-Informed Counseling and Reporting Environment) untuk siswa sekolah di Indonesia.
                    Filosofimu: Listen – Understand – Protect – Empower (Mendengarkan – Memahami – Melindungi – Memberdayakan).
                    Tugasmu:
                    1. Validasi perasaan siswa dengan lembut, tenang, sadar trauma, dan tanpa menghakimi.
                    2. Bantu siswa merasa aman dan dorong mereka menceritakan detail secara perlahan untuk menyusun laporan: apa yang terjadi, siapa yang terlibat, kapan kejadiannya, dan di mana lokasinya.
                    3. Jika situasi terasa mengancam keselamatan fisik atau darurat, sarankan segera menekan Tombol Darurat atau menghubungi Guru BK.
                    4. Gunakan bahasa Indonesia yang ramah remaja, singkat, jelas (maksimal 3-4 kalimat per respons), dan penuh empati.
                    """.trimIndent()
                )
            }
        )
    }

    private val draftExtractorModel by lazy {
        val schema = Schema.obj(
            properties = mapOf(
                "category" to Schema.enumeration(
                    values = listOf("VERBAL", "FISIK", "SIBER", "SOSIAL"),
                    description = "Kategori perundungan yang paling sesuai"
                ),
                "locationArea" to Schema.enumeration(
                    values = listOf("Kelas", "Kantin", "Lapangan", "Toilet", "Luar Sekolah/Online"),
                    description = "Area lokasi kejadian di sekolah"
                ),
                "incidentTimeLabel" to Schema.string(
                    description = "Perkiraan waktu kejadian dalam bahasa Indonesia"
                ),
                "chronology" to Schema.string(
                    description = "Ringkasan kronologi kejadian yang terstruktur dan jelas dari sudut pandang pelapor"
                ),
                "anonymous" to Schema.boolean(
                    description = "True jika sebaiknya tetap anonim, false jika pelapor menyebut ingin terbuka"
                )
            )
        )
        Firebase.ai.generativeModel(
            modelName = MODEL_FLASH,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = schema
            }
        )
    }

    private val triageModel by lazy {
        val schema = Schema.obj(
            properties = mapOf(
                "summary" to Schema.string(
                    description = "Ringkasan singkat kronologi dalam 1-2 kalimat bahasa Indonesia"
                ),
                "priority" to Schema.enumeration(
                    values = listOf("TINGGI", "SEDANG", "RENDAH"),
                    description = "TINGGI jika ada kekerasan fisik, ancaman, keselamatan terancam, atau perundungan berulang berat; SEDANG jika verbal/siber/sosial mengganggu; RENDAH jika insiden ringan awal"
                ),
                "recommendation" to Schema.string(
                    description = "Rekomendasi tindakan awal konkret bagi Guru BK dan Wali Kelas dalam 2-3 poin singkat"
                ),
                "trendNote" to Schema.string(
                    description = "Catatan pola atau titik rawan pencegahan di sekolah dalam 1 kalimat"
                )
            )
        )
        Firebase.ai.generativeModel(
            modelName = MODEL_FLASH,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = schema
            }
        )
    }

    private val insightModel by lazy {
        Firebase.ai.generativeModel(modelName = MODEL_FLASH)
    }

    fun streamChatReply(
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<String> = flow {
        try {
            val chatHistory = history
                .filter { !it.isStreaming && it.text.isNotBlank() }
                .takeLast(12)
                .map { msg ->
                    content(if (msg.isFromUser) "user" else "model") {
                        text(msg.text)
                    }
                }
            val chat = companionModel.startChat(history = chatHistory)
            val accumulated = StringBuilder()
            withTimeout(AI_TIMEOUT_MS) {
                chat.sendMessageStream(userMessage).collect { chunk ->
                    val part = chunk.text.orEmpty()
                    if (part.isNotEmpty()) {
                        accumulated.append(part)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("AI tidak mengembalikan respons.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat streaming timed out", e)
            throw e
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat streaming failed", e)
            throw e
        }
    }

    suspend fun extractDraftFromChat(messages: List<ChatMessage>): ReportDraft {
        val transcript = messages
            .filter { it.text.isNotBlank() }
            .joinToString("\n") { msg ->
                val speaker = if (msg.isFromUser) "Siswa" else "Asisten"
                "$speaker: ${msg.text}"
            }
        val prompt = """
            Ekstrak draf laporan perundungan terstruktur dari percakapan siswa berikut.
            Jika ada detail yang belum disebut siswa (misalnya waktu), isi dengan nilai masuk akal seperti 'Hari ini' atau 'Baru saja' dan tulis kronologi sesuai informasi yang tersedia.
            
            Percakapan:
            $transcript
        """.trimIndent()

        return try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                draftExtractorModel.generateContent(prompt)
            }
            val rawJson = response.text ?: throw IllegalStateException("Respons AI kosong.")
            val json = JSONObject(rawJson)
            ReportDraft(
                category = BullyingCategory.fromRaw(json.optString("category", "VERBAL")),
                locationArea = json.optString("locationArea", "Kelas").ifBlank { "Kelas" },
                incidentTimeLabel = json.optString("incidentTimeLabel", "Hari ini"),
                chronology = json.optString("chronology", ""),
                anonymous = json.optBoolean("anonymous", true),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract draft from chat", e)
            throw e
        }
    }

    suspend fun analyzeReportTriage(
        category: BullyingCategory,
        locationArea: String,
        incidentTimeLabel: String,
        chronology: String
    ): AiTriageResult {
        val prompt = """
            Lakukan triase awal laporan perundungan siswa berikut untuk Guru BK:
            - Kategori: ${category.label}
            - Lokasi: $locationArea
            - Waktu: $incidentTimeLabel
            - Kronologi: $chronology
        """.trimIndent()

        return try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                triageModel.generateContent(prompt)
            }
            val rawJson = response.text ?: throw IllegalStateException("Respons triase AI kosong.")
            val json = JSONObject(rawJson)
            AiTriageResult(
                summary = json.optString("summary", "Laporan perundungan kategori ${category.label} di $locationArea."),
                priority = AiPriority.fromRaw(json.optString("priority", "SEDANG")),
                recommendation = json.optString(
                    "recommendation",
                    "Jadwalkan konseling awal dan verifikasi situasi bersama Wali Kelas."
                ),
                trendNote = json.optString(
                    "trendNote",
                    "Perlu pemantauan berkala di area $locationArea."
                ),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI triage failed", e)
            throw e
        }
    }

    suspend fun generatePreventionInsight(statsSummary: String): String {
        val prompt = """
            Kamu adalah konsultan pendidikan anti-perundungan untuk Guru BK sekolah di Indonesia.
            Berdasarkan ringkasan data laporan berikut, susun 3 rekomendasi program pencegahan yang konkret, empatik, dan mudah diterapkan di kelas (singkat, poin bernomor, bahasa Indonesia):
            
            $statsSummary
        """.trimIndent()

        return try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                insightModel.generateContent(prompt)
            }
            val text = response.text
            if (text.isNullOrBlank()) throw IllegalStateException("Insight AI kosong.")
            text
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Prevention insight failed", e)
            throw e
        }
    }

    private companion object {
        const val TAG = "AiRepository"
        const val MODEL_FLASH = "gemini-3.8-flash"
        const val AI_TIMEOUT_MS = 60_000L
    }
}
