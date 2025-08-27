package com.dex.base.baseandroidcompose.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utility class để xử lý location permissions và GPS settings
 * Hỗ trợ kiểm tra permission, GPS status và các utility functions
 */
object LocationPermissionHandler {
    
    /**
     * Kiểm tra xem app có location permissions không
     * @param context Application context
     * @return true nếu có ít nhất một location permission
     */
    fun hasLocationPermissions(context: Context): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        return fineLocationGranted || coarseLocationGranted
    }
    
    /**
     * Kiểm tra xem có fine location permission không (GPS chính xác)
     * @param context Application context
     * @return true nếu có ACCESS_FINE_LOCATION permission
     */
    fun hasFineLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Kiểm tra xem có coarse location permission không (vị trí gần đúng)
     * @param context Application context
     * @return true nếu có ACCESS_COARSE_LOCATION permission
     */
    fun hasCoarseLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Lấy danh sách permissions cần thiết cho location
     * @return Array of location permissions
     */
    fun getLocationPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    
    /**
     * Lấy danh sách permissions chưa được cấp
     * @param context Application context
     * @return Array of missing permissions
     */
    fun getMissingPermissions(context: Context): Array<String> {
        val missingPermissions = mutableListOf<String>()
        
        if (!hasFineLocationPermission(context)) {
            missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        if (!hasCoarseLocationPermission(context)) {
            missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        
        return missingPermissions.toTypedArray()
    }
    
    /**
     * Kiểm tra xem GPS có được bật không
     * @param context Application context
     * @return true nếu GPS hoặc Network provider được bật
     */
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    /**
     * Kiểm tra xem GPS provider có được bật không (chính xác hơn Network)
     * @param context Application context
     * @return true nếu GPS provider được bật
     */
    fun isGpsProviderEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    
    /**
     * Kiểm tra xem Network provider có được bật không
     * @param context Application context
     * @return true nếu Network provider được bật
     */
    fun isNetworkProviderEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    /**
     * Kiểm tra tổng thể xem có thể lấy location không
     * @param context Application context
     * @return true nếu có permission và GPS được bật
     */
    fun canGetLocation(context: Context): Boolean {
        return hasLocationPermissions(context) && isGpsEnabled(context)
    }
    
    /**
     * Lấy thông tin chi tiết về location status
     * @param context Application context
     * @return LocationStatus object chứa thông tin chi tiết
     */
    fun getLocationStatus(context: Context): LocationStatus {
        return LocationStatus(
            hasPermissions = hasLocationPermissions(context),
            hasFinePermission = hasFineLocationPermission(context),
            hasCoarsePermission = hasCoarseLocationPermission(context),
            isGpsEnabled = isGpsEnabled(context),
            isGpsProviderEnabled = isGpsProviderEnabled(context),
            isNetworkProviderEnabled = isNetworkProviderEnabled(context),
            canGetLocation = canGetLocation(context)
        )
    }
}

/**
 * Data class chứa thông tin chi tiết về location status
 */
data class LocationStatus(
    val hasPermissions: Boolean,
    val hasFinePermission: Boolean,
    val hasCoarsePermission: Boolean,
    val isGpsEnabled: Boolean,
    val isGpsProviderEnabled: Boolean,
    val isNetworkProviderEnabled: Boolean,
    val canGetLocation: Boolean
) {
    /**
     * Lấy thông báo lỗi phù hợp
     */
    fun getErrorMessage(): String? {
        return when {
            !hasPermissions -> "Location permissions not granted"
            !isGpsEnabled -> "GPS is disabled"
            else -> null
        }
    }
    
    /**
     * Lấy loại permission cần thiết
     */
    fun getRequiredAction(): LocationAction {
        return when {
            !hasPermissions -> LocationAction.REQUEST_PERMISSIONS
            !isGpsEnabled -> LocationAction.ENABLE_GPS
            else -> LocationAction.READY
        }
    }
}

/**
 * Enum định nghĩa các action cần thiết cho location
 */
enum class LocationAction {
    REQUEST_PERMISSIONS,
    ENABLE_GPS,
    READY
}