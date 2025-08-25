# InterstitialAdManager - Hướng dẫn sử dụng

## Tổng quan
`InterstitialAdManager` là một class quản lý quảng cáo Interstitial với hệ thống cache thông minh, giúp tối ưu hóa việc load và hiển thị quảng cáo.

## Tính năng chính

### 1. Cache Management
- **Stack Cache**: Lưu trữ quảng cáo dưới dạng `Pair<InterstitialAd, timestamp>`
- **Auto Cleanup**: Tự động dọn dẹp quảng cáo hết hiệu lực (10 phút)
- **Max Cache Size**: Giới hạn tối đa 3 quảng cáo trong cache

### 2. Smart Loading
- **Preload Strategy**: Load quảng cáo trước khi cần thiết
- **Duplicate Prevention**: Không load thêm nếu cache đã đầy hoặc còn quảng cáo hiệu lực
- **Background Loading**: Load quảng cáo mới trong background

### 3. Easy Integration
- **Simple API**: Chỉ cần 3 method chính: `loadInterAd`, `getInterAd`, `showInterAd`
- **Callback Support**: Hỗ trợ callback cho mọi operation
- **Error Handling**: Xử lý lỗi một cách graceful

## Cách sử dụng

### 1. Khởi tạo
```kotlin
// Tự động khởi tạo trong AdManager.initAdsAndUmp()
// Hoặc khởi tạo thủ công:
val interstitialManager = InterstitialAdManager.getInstance()
```

### 2. Load quảng cáo
```kotlin
// Load quảng cáo với callback
AdManager.loadInterAd(context) { success, message ->
    if (success) {
        Log.d("Ads", "Load thành công: $message")
    } else {
        Log.e("Ads", "Load thất bại: $message")
    }
}

// Load quảng cáo không cần callback
AdManager.loadInterAd(context)
```

### 3. Lấy quảng cáo
```kotlin
AdManager.getInterAd(context) { interstitialAd ->
    if (interstitialAd != null) {
        // Quảng cáo đã sẵn sàng, có thể show ngay
        showInterstitialAd(interstitialAd)
    } else {
        // Không có quảng cáo, xử lý logic khác
        continueWithoutAd()
    }
}
```

### 4. Hiển thị quảng cáo
```kotlin
AdManager.showInterAd(activity) { success ->
    if (success) {
        // User đã đóng quảng cáo, tiếp tục logic
        startNextLevel()
    } else {
        // Quảng cáo bị lỗi, xử lý fallback
        handleAdError()
    }
}
```

## Use Cases

### 1. Game Flow
```kotlin
fun onLevelComplete() {
    // Load quảng cáo trước
    AdManager.loadInterAd(this) { success, _ ->
        if (success) {
            // Show quảng cáo khi chuyển level
            AdManager.showInterAd(this) { adClosed ->
                if (adClosed) {
                    startNextLevel()
                } else {
                    startNextLevel() // Fallback
                }
            }
        } else {
            startNextLevel() // Không có quảng cáo
        }
    }
}
```

### 2. App Navigation
```kotlin
fun navigateToPremiumScreen() {
    AdManager.showInterAd(this) { success ->
        // Luôn chuyển màn hình, bất kể quảng cáo có thành công hay không
        startActivity(Intent(this, PremiumActivity::class.java))
    }
}
```

### 3. Preload Strategy
```kotlin
fun onAppStart() {
    // Load quảng cáo ngay khi app khởi động
    AdManager.loadInterAd(this)
    
    // Load thêm quảng cáo khi user tương tác
    binding.startButton.setOnClickListener {
        AdManager.loadInterAd(this)
    }
}
```

## Cache Management

### Kiểm tra cache
```kotlin
val cacheInfo = AdManager.getInterstitialCacheInfo()
Log.d("Ads", "Cache: $cacheInfo")
// Output: "Cache size: 2, Valid ads: 2"
```

### Xóa cache (debug only)
```kotlin
AdManager.clearInterstitialCache()
```

## Best Practices

### 1. Timing
- **Preload**: Load quảng cáo trước khi cần thiết
- **Background**: Load trong background, không block UI
- **Strategic**: Load ở những thời điểm user ít tương tác

### 2. User Experience
- **Non-blocking**: Không block user flow khi quảng cáo lỗi
- **Fallback**: Luôn có plan B khi quảng cáo không hoạt động
- **Frequency**: Không show quảng cáo quá thường xuyên

### 3. Performance
- **Cache Size**: Giữ cache size hợp lý (mặc định 3)
- **Validity**: Quảng cáo có hiệu lực 10 phút
- **Memory**: Tự động cleanup quảng cáo cũ

## Error Handling

### Load Errors
```kotlin
AdManager.loadInterAd(context) { success, message ->
    if (!success) {
        // Retry logic
        Handler(Looper.getMainLooper()).postDelayed({
            AdManager.loadInterAd(context)
        }, 5000) // Retry sau 5 giây
    }
}
```

### Show Errors
```kotlin
AdManager.showInterAd(activity) { success ->
    if (!success) {
        // Fallback logic
        continueWithoutAd()
    }
}
```

## Debug & Testing

### Test Mode
```kotlin
// Sử dụng test ad ID trong debug mode
// AdManager sẽ tự động detect debug mode
```

### Logging
```kotlin
// Tất cả operations đều có log
// Sử dụng tag "InterstitialAdManager" để filter
```

## Troubleshooting

### Quảng cáo không load
1. Kiểm tra internet connection
2. Kiểm tra ad ID có đúng không
3. Kiểm tra consent status
4. Kiểm tra test device configuration

### Quảng cáo không show
1. Kiểm tra cache có quảng cáo không
2. Kiểm tra quảng cáo có còn hiệu lực không
3. Kiểm tra activity context có valid không

### Performance issues
1. Giảm cache size nếu cần
2. Tăng validity duration nếu cần
3. Kiểm tra memory usage 