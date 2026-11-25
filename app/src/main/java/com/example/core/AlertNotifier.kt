package com.example.core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AlertNotifier {
    private const val TAG = "AlertNotifier"
    private const val CHANNEL_SOS = "sahabat_sos_channel"
    private const val CHANNEL_REPORTS = "sahabat_reports_channel"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val sosChannel = NotificationChannel(
                CHANNEL_SOS,
                "Alarm Darurat SOS Siswa",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi real-time saat siswa menekan Tombol Darurat SOS."
                enableVibration(true)
            }
            val reportChannel = NotificationChannel(
                CHANNEL_REPORTS,
                "Laporan Perundungan Baru",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi real-time saat laporan perundungan baru masuk."
            }
            nm.createNotificationChannel(sosChannel)
            nm.createNotificationChannel(reportChannel)
        }
    }

    fun playSosAlarmSound() {
        CoroutineScope(Dispatchers.Default).launch {
            var toneGen: ToneGenerator? = null
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 90)
                repeat(3) {
                    toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 280)
                    delay(350)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Unable to play SOS alarm tone", e)
            } finally {
                try {
                    toneGen?.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    fun playNewReportChime() {
        CoroutineScope(Dispatchers.Default).launch {
            var toneGen: ToneGenerator? = null
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
                delay(280)
            } catch (e: Exception) {
                Log.w(TAG, "Unable to play report chime", e)
            } finally {
                try {
                    toneGen?.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    fun postLocalNotification(
        context: Context,
        isEmergency: Boolean,
        title: String,
        body: String,
        notificationId: Int
    ) {
        ensureChannels(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
        try {
            val channelId = if (isEmergency) CHANNEL_SOS else CHANNEL_REPORTS
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(
                    if (isEmergency) NotificationCompat.PRIORITY_HIGH
                    else NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .build()
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, notification)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to post notification", e)
        }
    }
}
