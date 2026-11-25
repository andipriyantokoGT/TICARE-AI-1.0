package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest

private const val STAFF_BOOTSTRAP_EMAIL = "andipriyantoko3@gmail.com"
private const val EXPECTED_DEBUG_SHA1 = "6D:F9:80:00:6C:36:A7:E4:8B:5A:F9:3D:88:8A:16:CB:F1:B9:3D:83"

fun isVerifiedStaffUser(user: FirebaseUser?): Boolean {
    if (user == null) return false
    return user.isEmailVerified &&
        user.email.equals(STAFF_BOOTSTRAP_EMAIL, ignoreCase = true)
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    if (Firebase.auth.currentUser != null) {
        onAuthSuccess()
        return
    }
    val clientId = resolveWebClientId(context)
    if (clientId.isBlank()) {
        onUnauthenticated()
        return
    }
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    scope.launch {
        try {
            val cred = credentialManager.getCredential(context, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                Firebase.auth.signInWithCredential(
                    GoogleAuthProvider.getCredential(token, null)
                ).await()
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = resolveWebClientId(context)
    if (clientId.isBlank()) {
        onAuthError("Konfigurasi Google Sign-In (google-services.json) belum terpasang pada build APK ini.")
        return
    }
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val primaryRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()
    scope.launch {
        try {
            val activity = context.findActivity()
                ?: throw IllegalStateException("Context layar tidak mendukung dialog Google Sign-In.")
            val result = try {
                credentialManager.getCredential(activity, primaryRequest)
            } catch (primaryError: GetCredentialException) {
                val primaryDetail = extractCredentialErrorDetail(primaryError)
                if (shouldRetryWithGoogleIdOption(primaryDetail)) {
                    Log.w("Auth", "Primary GetSignInWithGoogleOption failed ($primaryDetail), retrying with GetGoogleIdOption")
                    val fallbackOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(clientId)
                        .setAutoSelectEnabled(false)
                        .build()
                    val fallbackRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(fallbackOption)
                        .build()
                    credentialManager.getCredential(activity, fallbackRequest)
                } else {
                    throw primaryError
                }
            }
            val cred = result.credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                Firebase.auth.signInWithCredential(
                    GoogleAuthProvider.getCredential(token, null)
                ).await()
                onAuthSuccess()
            } else {
                onAuthError("Tipe kredensial tidak didukung.")
            }
        } catch (e: GetCredentialCancellationException) {
            val detail = extractCredentialErrorDetail(e)
            Log.w("Auth", "Google Sign-In flow cancelled or dismissed: $detail", e)
            onAuthCancelled()
            onAuthError(buildDiagnosticAuthMessage(context, detail, isCancellation = true))
        } catch (e: GetCredentialException) {
            val detail = extractCredentialErrorDetail(e) ?: e.type
            Log.e("Auth", "Google Sign-In CredentialException (${e.type}): $detail", e)
            onAuthError(buildDiagnosticAuthMessage(context, detail, isCancellation = false))
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            val detail = e.localizedMessage?.takeIf { it.isNotBlank() }
                ?: e.message?.takeIf { it.isNotBlank() }
                ?: e::class.simpleName
                ?: "Kesalahan tidak diketahui"
            onAuthError("Gagal masuk dengan Google: $detail")
        }
    }
}

private fun extractCredentialErrorDetail(e: GetCredentialException): String? {
    return e.errorMessage?.toString()?.takeIf { it.isNotBlank() }
        ?: e.message?.takeIf { it.isNotBlank() }
}

private fun shouldRetryWithGoogleIdOption(detail: String?): Boolean {
    if (detail.isNullOrBlank()) return false
    val lower = detail.lowercase()
    return lower.contains("reauth") ||
        lower.contains("[16]") ||
        lower.contains("16:") ||
        lower.contains("developer")
}

private fun buildDiagnosticAuthMessage(
    context: Context,
    detail: String?,
    isCancellation: Boolean
): String {
    val actualSha1 = getAppSigningSha1(context)
    val shaMismatch = actualSha1 != null && !actualSha1.equals(EXPECTED_DEBUG_SHA1, ignoreCase = true)
    if (shaMismatch) {
        return "Gagal login Google ($detail). Sidik jari APK ini ($actualSha1) berbeda dari Firebase ($EXPECTED_DEBUG_SHA1). File debug.keystore.base64 / Secret DEBUG_KEYSTORE_BASE64 belum dipakai saat build di GitHub Actions."
    }
    if (detail != null && (detail.contains("reauth", ignoreCase = true) || detail.contains("16"))) {
        val shaNote = actualSha1 ?: "tidak terbaca"
        return "Proses login Google ditolak ($detail). Sidik jari APK sudah sesuai ($shaNote). Periksa akun Gmail di Setelan perangkat > Akun > Google (hapus lalu login ulang akun $STAFF_BOOTSTRAP_EMAIL) atau perbarui Google Play Services."
    }
    return if (isCancellation) {
        if (detail != null) {
            "Proses login Google dibatalkan/ditolak: $detail"
        } else {
            "Proses login Google dibatalkan. Pastikan akun Gmail sudah login di HP dan coba lagi."
        }
    } else {
        "Gagal login Google (${detail ?: "kesalahan tidak diketahui"})."
    }
}

private fun getAppSigningSha1(context: Context): String? {
    return try {
        val pm = context.packageManager
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signingInfo = info.signingInfo ?: return null
            if (signingInfo.hasMultipleSigners()) signingInfo.apkContentsSigners else signingInfo.signingCertificateHistory
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures
        }
        val certBytes = signatures?.firstOrNull()?.toByteArray() ?: return null
        val digest = MessageDigest.getInstance("SHA-1").digest(certBytes)
        digest.joinToString(":") { "%02X".format(it) }
    } catch (_: Exception) {
        null
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun signOut(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w("Auth", "Failed to clear credential state: ${e.message}")
            } finally {
                Firebase.auth.signOut()
                onSignOutComplete()
            }
        }
    }
}

private fun resolveWebClientId(context: Context): String {
    val resId = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )
    if (resId == 0) return ""
    return try {
        context.getString(resId).trim()
    } catch (_: Exception) {
        ""
    }
}
