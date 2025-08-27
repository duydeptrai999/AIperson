package com.dex.base.baseandroidcompose.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import java.util.Locale
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    
    private val geocoder: Geocoder by lazy {
        Geocoder(context, Locale.getDefault())
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
     * Chuyển đổi địa chỉ thành tọa độ GPS (Geocoding)
     * @param address Địa chỉ cần chuyển đổi
     * @param maxResults Số lượng kết quả tối đa (default: 1)
     * @return Pair<latitude, longitude> hoặc null nếu không tìm thấy
     */
    suspend fun getCoordinatesFromAddress(
        address: String,
        maxResults: Int = 1
    ): Pair<Double, Double>? {
        if (address.isBlank()) {
            Logger.e("Address is empty")
            return null
        }
        
        return withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) {
                    Logger.e("Geocoder is not available on this device")
                    return@withContext null
                }
                
                val addresses = geocoder.getFromLocationName(address, maxResults)
                if (addresses?.isNotEmpty() == true) {
                    val location = addresses[0]
                    val lat = location.latitude
                    val lng = location.longitude
                    Logger.d("Geocoding success: $address -> ($lat, $lng)")
                    Pair(lat, lng)
                } else {
                    Logger.w("No coordinates found for address: $address")
                    null
                }
            } catch (e: Exception) {
                Logger.e("Geocoding error: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Chuyển đổi tọa độ GPS thành địa chỉ (Reverse Geocoding)
     * @param latitude Vĩ độ
     * @param longitude Kinh độ
     * @param maxResults Số lượng kết quả tối đa (default: 1)
     * @return Địa chỉ đầy đủ hoặc null nếu không tìm thấy
     */
    suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double,
        maxResults: Int = 1
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) {
                    Logger.e("Geocoder is not available on this device")
                    return@withContext null
                }
                
                val addresses = geocoder.getFromLocation(latitude, longitude, maxResults)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    val fullAddress = buildAddressString(address)
                    Logger.d("Reverse geocoding success: ($latitude, $longitude) -> $fullAddress")
                    fullAddress
                } else {
                    Logger.w("No address found for coordinates: ($latitude, $longitude)")
                    null
                }
            } catch (e: Exception) {
                Logger.e("Reverse geocoding error: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Lấy thông tin địa chỉ chi tiết từ tọa độ
     * @param latitude Vĩ độ
     * @param longitude Kinh độ
     * @return AddressInfo object chứa thông tin chi tiết
     */
    suspend fun getDetailedAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): AddressInfo? {
        return withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) {
                    Logger.e("Geocoder is not available on this device")
                    return@withContext null
                }
                
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    AddressInfo(
                        fullAddress = buildAddressString(address),
                        street = "", // Không sử dụng street
                        city = address.locality ?: "", // Chỉ lấy locality, không lấy subLocality
                        state = address.adminArea ?: "",
                        country = address.countryName ?: "",
                        postalCode = "", // Không sử dụng postal code
                        latitude = latitude,
                        longitude = longitude
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Logger.e("Detailed reverse geocoding error: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Xây dựng chuỗi địa chỉ từ Address object
     * Chỉ lấy thành phố/huyện, tỉnh/bang và đất nước
     */
    private fun buildAddressString(address: Address): String {
        val parts = mutableListOf<String>()
        
        // Lấy thành phố/xã, huyện, tỉnh/bang và đất nước
        address.locality?.let { parts.add(it) }
        address.subAdminArea?.let { parts.add(it) }
        address.adminArea?.let { parts.add(it) }
        address.countryName?.let { parts.add(it) }
        
        return parts.joinToString(", ")
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

/**
 * Data class chứa thông tin địa chỉ chi tiết
 */
data class AddressInfo(
    val fullAddress: String,
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double
) {
    /**
     * Lấy địa chỉ ngắn gọn (thành phố, quốc gia)
     */
    fun getShortAddress(): String {
        val parts = mutableListOf<String>()
        if (city.isNotBlank()) parts.add(city)
        if (country.isNotBlank()) parts.add(country)
        return parts.joinToString(", ")
    }
    
    /**
     * Kiểm tra xem địa chỉ có hợp lệ không
     */
    fun isValid(): Boolean {
        return fullAddress.isNotBlank() && latitude != 0.0 && longitude != 0.0
    }
}