package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.AiPriority
import com.example.data.model.BullyingCategory
import com.example.data.model.BullyingReport
import com.example.data.model.ReportStatus
import com.example.data.model.SosAlert
import com.example.data.model.UserRoleMode
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedBright
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.LavenderSoft
import com.example.ui.theme.MintBright
import com.example.ui.theme.MintPrimary
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SkyBlueBright
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

const val AI_TRIAGE_DISCLAIMER =
    "Prinsip TICARE AI (Human-in-the-Loop): AI membantu menganalisis sebagai triase awal, sedangkan Guru BK memvalidasi dan mengambil keputusan profesional."

@Composable
fun StudentTopHeader(
    title: String,
    subtitle: String,
    notificationCount: Int,
    roleMode: UserRoleMode,
    onToggleRole: () -> Unit,
    onQuickExit: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(SkyBluePrimary, IndigoDeep)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = CleanWhite.copy(alpha = 0.22f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Logo TICARE AI",
                            tint = CleanWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = CleanWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = CleanWhite.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CleanWhite.copy(alpha = 0.2f),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onToggleRole() }
                        .testTag("switch_role_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = "Ganti tampilan ke Dasbor Guru BK",
                            tint = CleanWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (roleMode == UserRoleMode.STUDENT) "Guru BK" else "Siswa",
                            style = MaterialTheme.typography.labelSmall,
                            color = CleanWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmergencyRedBright.copy(alpha = 0.92f),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onQuickExit() }
                        .testTag("quick_exit_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Keluar Cepat ke halaman aman",
                            tint = CleanWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Keluar",
                            style = MaterialTheme.typography.labelSmall,
                            color = CleanWhite
                        )
                    }
                }

                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("notification_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (notificationCount > 0) {
                                Badge(containerColor = EmergencyRedBright) {
                                    Text(
                                        text = notificationCount.coerceAtMost(99).toString(),
                                        color = CleanWhite
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifikasi laporan",
                            tint = CleanWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendlyCounselorIllustration(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CleanWhite.copy(alpha = 0.85f),
        border = BorderStroke(2.dp, CleanWhite),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        modifier = modifier.size(92.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_student_welcome),
            contentDescription = "Ilustrasi siswa ramah dan aman",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        )
    }
}

@Composable
fun RobotCompanionAvatar(modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = SkyBlueSoft,
        border = BorderStroke(1.5.dp, CleanWhite),
        shadowElevation = 2.dp,
        modifier = modifier.size(44.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_ai_robot_avatar),
            contentDescription = "Avatar AI Assistant Sahabat Curhatmu",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

@Composable
fun CounselorAvatar(modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = SkyBlueSoft,
        border = BorderStroke(1.5.dp, CleanWhite),
        shadowElevation = 2.dp,
        modifier = modifier.size(44.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_counselor_avatar),
            contentDescription = "Avatar Guru BK",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

@Composable
fun PriorityBadge(priority: AiPriority) {
    val (bg, fg) = when (priority) {
        AiPriority.TINGGI -> EmergencyRedSoft to EmergencyRed
        AiPriority.SEDANG -> WarningAmberSoft to WarningAmber
        AiPriority.RENDAH -> SuccessGreenSoft to SuccessGreen
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(status: ReportStatus) {
    val (bg, fg) = when (status) {
        ReportStatus.BARU -> SkyBlueSoft to SkyBlueDark
        ReportStatus.DIPROSES -> WarningAmberSoft to WarningAmber
        ReportStatus.TINDAK_LANJUT -> LavenderSoft to LavenderPrimary
        ReportStatus.SELESAI -> SuccessGreenSoft to SuccessGreen
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun FourStageStepper(currentStage: Int) {
    val stages = listOf("Verifikasi", "Konseling", "Pendampingan", "Selesai")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { idx, label ->
            val reached = idx <= currentStage
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                if (idx == 0) Color.Transparent
                                else if (reached) MintBright else SlateBorder
                            )
                    )
                    Surface(
                        shape = CircleShape,
                        color = if (reached) MintBright else SlateBorder,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (reached) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = label,
                                    tint = CleanWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                if (idx == stages.lastIndex) Color.Transparent
                                else if (idx < currentStage) MintBright else SlateBorder
                            )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reached) MintPrimary else TextMuted,
                    fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmergencySosDialog(
    isSending: Boolean,
    lastSentSos: SosAlert?,
    onSendSosWithGps: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        onSendSosWithGps()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CleanWhite),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = EmergencyRedSoft,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Peringatan Darurat",
                                    tint = EmergencyRed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Bantuan Darurat SOS",
                                style = MaterialTheme.typography.titleLarge,
                                color = EmergencyRed
                            )
                            Text(
                                text = "Respons cepat Guru BK & layanan darurat",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup dialog darurat")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    enabled = !isSending,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmergencyRed,
                        contentColor = CleanWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("send_sos_gps_button")
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            color = CleanWhite,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Mengambil GPS & Mengirim...")
                    } else {
                        Icon(Icons.Filled.GpsFixed, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kirim Sinyal Darurat + GPS ke Guru BK",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                if (lastSentSos != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = SuccessGreenSoft,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Sinyal Darurat Terkirim ke Dasbor Guru BK!",
                                style = MaterialTheme.typography.titleSmall,
                                color = SuccessGreen
                            )
                            Text(
                                text = "Koordinat: ${"%.5f".format(lastSentSos.latitude)}, ${"%.5f".format(lastSentSos.longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Panggilan Cepat 1-Klik",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                EmergencyDialRow(
                    icon = Icons.Filled.Security,
                    title = "Kepolisian",
                    number = "110",
                    accent = SkyBluePrimary,
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:110"))
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                EmergencyDialRow(
                    icon = Icons.Filled.LocalHospital,
                    title = "Ambulans / Rumah Sakit",
                    number = "119",
                    accent = EmergencyRed,
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                EmergencyDialRow(
                    icon = Icons.Filled.LocalHospital,
                    title = "Ambulans Gawat Darurat",
                    number = "118",
                    accent = EmergencyRedBright,
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:118"))
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                EmergencyDialRow(
                    icon = Icons.Filled.SupportAgent,
                    title = "Layanan SAPA 129 (KemenPPPA)",
                    number = "129",
                    accent = LavenderPrimary,
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:129"))
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                EmergencyDialRow(
                    icon = Icons.Filled.PhoneInTalk,
                    title = "Hotline Keamanan Sekolah",
                    number = "021-500-129",
                    accent = MintPrimary,
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:021500129"))
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EmergencyDialRow(
    icon: ImageVector,
    title: String,
    number: String,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SoftBackground,
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    Text("Hubungi $number", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accent.copy(alpha = 0.14f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(number, style = MaterialTheme.typography.labelMedium, color = accent)
                }
            }
        }
    }
}

@Composable
fun HelpGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Tentang & Panduan TICARE AI", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Technology with Empathy • Listen – Understand – Protect – Empower",
                    style = MaterialTheme.typography.labelSmall,
                    color = LavenderPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Surface(
                    color = SkyBlueSoft.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TICARE AI (Trauma-Informed Counseling and Reporting Environment berbasis AI): lingkungan pelaporan dan konseling sadar trauma yang berpusat pada manusia untuk pencegahan perundungan dan pendampingan siswa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SkyBlueDark,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("6 Landasan Utama TICARE AI", style = MaterialTheme.typography.titleSmall, color = IndigoDeep)
                Text(
                    text = "• Trauma-Informed Care: Pendampingan sensitif terhadap trauma siswa.\n" +
                        "• Artificial Intelligence: Analisis risiko awal & rekomendasi pendampingan.\n" +
                        "• Human-Centered AI: Mengutamakan keamanan dan martabat manusia.\n" +
                        "• Human-in-the-Loop: AI membantu menganalisis, Guru BK memvalidasi keputusan.\n" +
                        "• Geo-Safety Intelligence: Identifikasi lokasi dengan persetujuan saat darurat.\n" +
                        "• Student Well-being: Perlindungan dan kesejahteraan psikologis siswa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "1. Apa yang bisa dilaporkan?",
                    style = MaterialTheme.typography.titleSmall,
                    color = SkyBlueDark
                )
                Text(
                    text = "Segala bentuk perundungan verbal (ejekan/hinaan), fisik (dorongan/pemukulan), siber (pesan/sosial media), dan sosial (pengucilan/fitnah).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "2. Apakah identitasku aman?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MintPrimary
                )
                Text(
                    text = "Ya! Mode Privasi & penyamaran Kode Siswa menjaga kerahasiaan identitasmu sehingga nama aslimu tidak ditampilkan secara terbuka.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "3. Bagaimana jika situasi mendesak?",
                    style = MaterialTheme.typography.titleSmall,
                    color = EmergencyRed
                )
                Text(
                    text = "Tekan Tombol Darurat Merah di Beranda untuk mengirim koordinat GPS (Geo-Safety Intelligence) ke Guru BK atau menelepon 110 / 119 / SAPA 129.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Saya Mengerti")
            }
        }
    )
}

@Composable
fun NotificationsDialog(
    reports: List<BullyingReport>,
    sosAlerts: List<SosAlert>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Notifikasi Terkini", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                val activeSos = sosAlerts.filter { it.status == "AKTIF" }
                if (activeSos.isNotEmpty()) {
                    Text("Darurat SOS Aktif", style = MaterialTheme.typography.titleSmall, color = EmergencyRed)
                    Spacer(modifier = Modifier.height(6.dp))
                    activeSos.take(3).forEach { sos ->
                        Surface(
                            color = EmergencyRedSoft,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(sos.reporterName, style = MaterialTheme.typography.titleSmall, color = EmergencyRed)
                                Text(sos.locationLabel, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (reports.isEmpty()) {
                    Text(
                        text = "Belum ada pembaruan laporan terbaru.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                } else {
                    reports.take(5).forEach { report ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("#${report.ticketCode}", style = MaterialTheme.typography.titleSmall)
                                StatusBadge(report.statusEnum())
                            }
                            Text(
                                text = report.bkMessage.ifBlank { report.aiSummary },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        HorizontalDivider(color = SlateBorder)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun QuickExitSafeScreen(
    onReturnToApp: () -> Unit,
    onOpenGoogleBrowser: () -> Unit
) {
    BackHandler { onReturnToApp() }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CleanWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catatan Belajar Mandiri",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    TextButton(onClick = onReturnToApp) {
                        Text("Kembali", color = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rangkuman Materi IPA & Matematika", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "• Hukum Newton I, II, dan III tentang gerak benda dan gaya.\n" +
                                "• Persamaan linear dua variabel: metode eliminasi & substitusi.\n" +
                                "• Jadwal ulangan harian: Jumat pukul 08.00 WIB.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = onOpenGoogleBrowser,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buka Google Search di Browser")
            }
        }
    }
}

@Composable
fun TrendBarChart(
    reports: List<BullyingReport>,
    isMonthly: Boolean,
    modifier: Modifier = Modifier
) {
    val buckets = remember(reports, isMonthly) {
        if (isMonthly) {
            val labels = listOf("Jul", "Agu", "Sep", "Okt", "Nov")
            val baseCounts = MutableList(labels.size) { 0 }
            reports.forEachIndexed { idx, _ ->
                baseCounts[idx % labels.size] += 1
            }
            if (reports.isNotEmpty()) {
                baseCounts[labels.lastIndex] = maxOf(baseCounts.last(), reports.size)
            }
            labels.zip(baseCounts)
        } else {
            val labels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
            val counts = MutableList(labels.size) { 0 }
            reports.forEachIndexed { idx, _ ->
                counts[idx % labels.size] += 1
            }
            labels.zip(counts)
        }
    }
    val maxVal = (buckets.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            val barWidth = (size.width / (buckets.size * 2f)).coerceAtLeast(16f)
            val space = size.width / buckets.size
            // Horizontal gridlines
            repeat(3) { i ->
                val y = size.height * (i + 1) / 4f
                drawLine(
                    color = SlateBorder,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
            }
            val linePoints = mutableListOf<Offset>()
            buckets.forEachIndexed { index, (_, count) ->
                val ratio = count.toFloat() / maxVal.toFloat()
                val barHeight = (size.height * 0.85f * ratio).coerceAtLeast(8f)
                val xCenter = space * index + space / 2f
                val topY = size.height - barHeight
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(SkyBlueBright, LavenderPrimary)),
                    topLeft = Offset(xCenter - barWidth / 2f, topY),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                linePoints.add(Offset(xCenter, topY))
            }
            if (linePoints.size > 1) {
                val path = Path().apply {
                    moveTo(linePoints.first().x, linePoints.first().y)
                    linePoints.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = path,
                    color = MintPrimary,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            buckets.forEach { (label, count) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SkyBlueDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryDistributionChart(reports: List<BullyingReport>) {
    val total = reports.size.coerceAtLeast(1)
    val categories = listOf(
        Triple(BullyingCategory.VERBAL, SkyBluePrimary, "Verbal"),
        Triple(BullyingCategory.FISIK, EmergencyRedBright, "Fisik"),
        Triple(BullyingCategory.SIBER, LavenderPrimary, "Siber"),
        Triple(BullyingCategory.SOSIAL, MintPrimary, "Sosial")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { (cat, color, label) ->
            val count = reports.count { it.categoryEnum() == cat }
            val pct = if (reports.isEmpty()) 0 else (count * 100) / total
            val animProgress by animateFloatAsState(
                targetValue = if (reports.isEmpty()) 0f else count.toFloat() / total.toFloat(),
                label = "cat_progress_${cat.name}"
            )
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    Text(
                        text = "$count kasus ($pct%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateBorder.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animProgress.coerceIn(0f, 1f))
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchoolAreaHotspotMap(reports: List<BullyingReport>) {
    val areas = listOf("Kelas", "Kantin", "Lapangan", "Toilet", "Luar Sekolah/Online")
    val counts = areas.associateWith { area ->
        reports.count { it.locationArea.equals(area, ignoreCase = true) }
    }
    val maxCount = (counts.values.maxOrNull() ?: 1).coerceAtLeast(1)
    var selectedArea by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Pemetaan Area Rawan Kejadian Sekolah",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            areas.forEach { area ->
                val c = counts[area] ?: 0
                val intensity = c.toFloat() / maxCount.toFloat()
                val tileColor = when {
                    c == 0 -> SoftBackground
                    intensity >= 0.7f -> EmergencyRedSoft
                    intensity >= 0.35f -> WarningAmberSoft
                    else -> SkyBlueSoft
                }
                val accentColor = when {
                    c == 0 -> TextMuted
                    intensity >= 0.7f -> EmergencyRed
                    intensity >= 0.35f -> WarningAmber
                    else -> SkyBlueDark
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tileColor,
                    border = if (selectedArea == area) {
                        androidx.compose.foundation.BorderStroke(2.dp, accentColor)
                    } else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedArea = if (selectedArea == area) null else area }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = area,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = area,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "$c laporan",
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        val topArea = counts.maxByOrNull { it.value }
        if (topArea != null && topArea.value > 0) {
            Text(
                text = "Titik terbanyak: ${topArea.key} (${topArea.value} laporan). Pertimbangkan patroli guru piket pada jam istirahat.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
