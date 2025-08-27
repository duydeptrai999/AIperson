package com.dex.base.baseandroidcompose.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.dex.base.baseandroidcompose.utils.LocationPermissionHandler
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service để xử lý GPS và lấy vị trí hiện tại
 * Sử dụng Google Play Services FusedLocationProviderClient để có độ chính xác cao
 * Hỗ trợ cả one-time location và continuous location updates
 */
class LocationService(private val context: Context) {
    
    companion object {
        private const val DEFAULT_TIMEOUT = 30_000L // 30 seconds
        private const val HIGH_ACCURACY_TIMEOUT = 15_000L // 15 seconds
        private const val DEFAULT_UPDATE_INTERVAL = 10_000L // 10 seconds
        private const val FASTEST_UPDATE_INTERVAL = 5_000L // 5 seconds
    }
    
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    private var locationCallback: LocationCallback? = null
    private var cancellationTokenSource: CancellationTokenSource? = null
    
    /**
     * Lấy vị trí hiện tại một lần (one-time location)
     * @param timeoutMs Timeout in milliseconds (default: 30s)
     * @return Location object hoặc null nếu không lấy được
     */
    suspend fun getCurrentLocation(timeoutMs: Long = DEFAULT_TIMEOUT): Location? {
        if (!LocationPermissionHandler.hasLocationPermissions(context)) {
            Logger.e("Location permissions not granted")
            throw SecurityException("Location permissions not granted")
        }
        
        if (!LocationPermissionHandler.isGpsEnabled(context)) {
            Logger.e("GPS is disabled")
            throw IllegalStateException("GPS is disabled")
        }
        
        return withTimeoutOrNull(timeoutMs) {
            getCurrentLocationInternal()
        }
    }
    
    /**
     * Lấy vị trí với độ chính xác cao
     * @param timeoutMs Timeout in milliseconds
     * @param minAccuracy Minimum accuracy in meters
     * @return Location object với độ chính xác yêu cầu
     */
    suspend fun getHighAccuracyLocation(
        timeoutMs: Long = HIGH_ACCURACY_TIMEOUT,
        minAccuracy: Float = 100f
    ): Location? {
        if (!LocationPermissionHandler.hasLocationPermissions(context)) {
            Logger.e("Location permissions not granted")
            throw SecurityException("Location permissions not granted")
        }
        
        return withTimeoutOrNull(timeoutMs) {
            getHighAccuracyLocationInternal(minAccuracy)
        }
    }
    
    /**
     * Bắt đầu continuous location updates
     * @param onLocationUpdate Callback khi có location mới
     * @param onError Callback khi có lỗi
     * @param updateIntervalMs Interval giữa các updates
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        onLocationUpdate: (Location) -> Unit,
        onError: (Exception) -> Unit,
        updateIntervalMs: Long = DEFAULT_UPDATE_INTERVAL
    ) {
        if (!LocationPermissionHandler.hasLocationPermissions(context)) {
            onError(SecurityException("Location permissions not granted"))
            return
        }
        
        if (!LocationPermissionHandler.isGpsEnabled(context)) {
            onError(IllegalStateException("GPS is disabled"))
            return
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            updateIntervalMs
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
            setWaitForAccurateLocation(true)
        }.build()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Logger.d("Location update: ${location.latitude}, ${location.longitude}")
                    onLocationUpdate(location)
                }
            }
            
            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    Logger.w("Location not available")
                    onError(IllegalStateException("Location not available"))
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Logger.d("Started location updates")
        } catch (e: Exception) {
            Logger.e("Error starting location updates: ${e.message}")
            onError(e)
        }
    }
    
    /**
     * Dừng location updates
     */
    @SuppressLint("MissingPermission")
    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
            Logger.d("Stopped location updates")
        }
        
        cancellationTokenSource?.cancel()
        cancellationTokenSource = null
    }
    
    /**
     * Kiểm tra xem GPS có được bật không
     * @return true nếu GPS được bật
     */
    fun isGpsEnabled(): Boolean {
        return LocationPermissionHandler.isGpsEnabled(context)
    }
    
    /**
     * Lấy last known location (cached)
     * @return Location object hoặc null
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!LocationPermissionHandler.hasLocationPermissions(context)) {
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    Logger.d("Last known location: $location")
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    Logger.e("Error getting last known location: ${exception.message}")
                    continuation.resume(null)
                }
        }
    }
    
    /**
     * Internal method để lấy current location
     */
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocationInternal(): Location? {
        return suspendCancellableCoroutine { continuation ->
            cancellationTokenSource = CancellationTokenSource()
            
            val currentLocationTask = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource!!.token
            )
            
            currentLocationTask
                .addOnSuccessListener { location ->
                    Logger.d("Current location: ${location?.latitude}, ${location?.longitude}")
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    Logger.e("Error getting current location: ${exception.message}")
                    continuation.resumeWithException(exception)
                }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource?.cancel()
            }
        }
    }
    
    /**
     * Internal method để lấy high accuracy location
     */
    @SuppressLint("MissingPermission")
    private suspend fun getHighAccuracyLocationInternal(minAccuracy: Float): Location? {
        return suspendCancellableCoroutine { continuation ->
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L
            ).apply {
                setMinUpdateIntervalMillis(500L)
                setWaitForAccurateLocation(true)
            }.build()
            
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    location?.let { loc ->
                        if (loc.accuracy <= minAccuracy) {
                            Logger.d("High accuracy location: ${loc.latitude}, ${loc.longitude}, accuracy: ${loc.accuracy}")
                            @SuppressLint("MissingPermission")
                            fusedLocationClient.removeLocationUpdates(this)
                            continuation.resume(loc)
                        }
                    }
                }
                
                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        @SuppressLint("MissingPermission")
                        fusedLocationClient.removeLocationUpdates(this)
                        continuation.resume(null)
                    }
                }
            }
            
            try {
                @SuppressLint("MissingPermission")
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
            
            continuation.invokeOnCancellation {
                @SuppressLint("MissingPermission")
                fusedLocationClient.removeLocationUpdates(callback)
            }
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopLocationUpdates()
    }
}

/**
 * Data class để wrap location result
 */
data class LocationResult(
    val location: Location?,
    val error: String?,
    val isSuccess: Boolean
) {
    companion object {
        fun success(location: Location) = LocationResult(location, null, true)
        fun error(message: String) = LocationResult(null, message, false)
    }
}