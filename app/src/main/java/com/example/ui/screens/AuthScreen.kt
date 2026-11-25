package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.BuildConfig
import com.example.data.model.UserRoleMode
import com.example.data.repository.onGoogleSignInClicked
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.LavenderSoft
import com.example.ui.theme.MintPrimary
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueSoft
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthScreen(
    onAuthSuccess: (UserRoleMode) -> Unit,
    onPortalSelected: (UserRoleMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var selectedPortal by remember { mutableStateOf(UserRoleMode.STUDENT) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyBluePrimary, IndigoDeep, LavenderSoft)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                color = MintSoft.copy(alpha = 0.16f),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.12f, size.height * 0.38f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = CleanWhite.copy(alpha = 0.2f),
                    modifier = Modifier.size(88.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Logo Perisai TICARE AI",
                            tint = CleanWhite,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TICARE AI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CleanWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Trauma-Informed Counseling & Reporting Environment",
                    style = MaterialTheme.typography.labelMedium,
                    color = CleanWhite.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Technology with Empathy • Sekolah Aman, Siswa Nyaman, Tanpa Perundungan",
                    style = MaterialTheme.typography.bodySmall,
                    color = CleanWhite.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pilih Akses Masuk",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PortalChoiceChip(
                                title = "Portal Siswa",
                                subtitle = "NISN & PIN Akses",
                                icon = Icons.Filled.School,
                                selected = selectedPortal == UserRoleMode.STUDENT,
                                accent = SkyBluePrimary,
                                softBg = SkyBlueSoft,
                                onClick = { selectedPortal = UserRoleMode.STUDENT },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_mode_student")
                            )
                            PortalChoiceChip(
                                title = "Login Staf / BK",
                                subtitle = "Guru BK & Wali Kelas",
                                icon = Icons.Filled.AdminPanelSettings,
                                selected = selectedPortal == UserRoleMode.COUNSELOR,
                                accent = LavenderPrimary,
                                softBg = LavenderSoft,
                                onClick = { selectedPortal = UserRoleMode.COUNSELOR },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("auth_mode_staff")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (selectedPortal == UserRoleMode.STUDENT) {
                                "Masuk aman dengan Google, lalu masukkan NISN/Kode Siswa & PIN Akses agar laporanmu terverifikasi dan disamarkan otomatis."
                            } else {
                                "Khusus Guru BK & Wali Kelas terverifikasi. Masuk dengan akun Google staf sekolah untuk mengakses dasbor & database Kode Siswa."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AuthTrustRow(
                            icon = Icons.Filled.VerifiedUser,
                            tint = SkyBluePrimary,
                            bg = SkyBlueSoft,
                            title = "Trauma-Informed & Kode Siswa",
                            desc = "Pendampingan aman dengan penyamaran identitas (mis. SW-8B-014)."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AuthTrustRow(
                            icon = Icons.Filled.Lock,
                            tint = MintPrimary,
                            bg = MintSoft,
                            title = "Human-in-the-Loop (HITL)",
                            desc = "AI membantu menganalisis, Guru BK memvalidasi & mengambil keputusan."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AuthTrustRow(
                            icon = Icons.Filled.Favorite,
                            tint = LavenderPrimary,
                            bg = LavenderSoft,
                            title = "Listen – Understand – Protect – Empower",
                            desc = "Mendengarkan, Memahami, Melindungi, & Memberdayakan siswa."
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (errorMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = errorMessage.orEmpty(),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                errorMessage = null
                                isLoading = true
                                onPortalSelected(selectedPortal)
                                onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isLoading = false
                                        onAuthSuccess(selectedPortal)
                                    },
                                    onAuthError = { msg ->
                                        isLoading = false
                                        errorMessage = msg
                                    },
                                    scope = scope,
                                    onAuthCancelled = {
                                        isLoading = false
                                    }
                                )
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedPortal == UserRoleMode.STUDENT) SkyBluePrimary else LavenderPrimary,
                                contentColor = CleanWhite
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("google_sign_in_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = CleanWhite,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Menghubungkan...")
                            } else {
                                Text(
                                    text = if (selectedPortal == UserRoleMode.STUDENT) {
                                        "Masuk Portal Siswa (Google)"
                                    } else {
                                        "Login Staf / Guru BK (Google)"
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        if (selectedPortal == UserRoleMode.COUNSELOR) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = SkyBlueSoft.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Sesi staf menggunakan Google Sign-In terverifikasi. Untuk mengaktifkan login Email/Password tradisional, aktifkan provider tersebut di Firebase Console.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SkyBlueDark,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        val releaseIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/andipriyantokoGT/TICARE-AI-1.0/releases/tag/latest-apk")
                        )
                        context.startActivity(releaseIntent)
                    },
                    modifier = Modifier.testTag("check_apk_update_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SystemUpdate,
                        contentDescription = "Cek Pembaruan APK",
                        tint = CleanWhite.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Versi ${BuildConfig.VERSION_NAME} • Cek Pembaruan APK",
                        style = MaterialTheme.typography.labelMedium,
                        color = CleanWhite.copy(alpha = 0.92f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortalChoiceChip(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    accent: Color,
    softBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) softBg else CleanWhite,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) accent else SlateBorder),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) accent else TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) accent else TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AuthTrustRow(
    icon: ImageVector,
    tint: Color,
    bg: Color,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg.copy(alpha = 0.55f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = CleanWhite,
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
