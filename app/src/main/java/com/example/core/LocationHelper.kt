package com.example.core

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

data class GpsCoordinate(
    val latitude: Double,
    val longitude: Double,
    val isLiveFix: Boolean
)

object LocationHelper {
    private const val TAG = "LocationHelper"

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocation(context: Context): GpsCoordinate? {
        return withTimeoutOrNull(8_000L) {
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()
                val live = suspendCancellableCoroutine<Location?> { cont ->
                    cont.invokeOnCancellation { cts.cancel() }
                    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        .addOnSuccessListener { loc ->
                            if (cont.isActive) cont.resume(loc)
                        }
                        .addOnFailureListener { err ->
                            Log.w(TAG, "getCurrentLocation failed: ${err.message}")
                            if (cont.isActive) cont.resume(null)
                        }
                }
                if (live != null) {
                    return@withTimeoutOrNull GpsCoordinate(live.latitude, live.longitude, true)
                }

                val last = suspendCancellableCoroutine<Location?> { cont ->
                    client.lastLocation
                        .addOnSuccessListener { loc ->
                            if (cont.isActive) cont.resume(loc)
                        }
                        .addOnFailureListener {
                            if (cont.isActive) cont.resume(null)
                        }
                }
                last?.let { GpsCoordinate(it.latitude, it.longitude, true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read GPS location", e)
                null
            }
        }
    }
}
