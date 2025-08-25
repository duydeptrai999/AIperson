# Codebase Summary - Ads Module

## Module/Package: ads

### InterstitialAdManager (Quản lý quảng cáo Interstitial với cache)
- **Chức năng chính**: Quản lý quảng cáo Interstitial với hệ thống cache thông minh
- **Design Pattern**: Singleton pattern với thread-safe initialization
- **Cache Strategy**: Stack cache với timestamp validation (10 phút hiệu lực)
- **Key Methods**:
  - `preloadInterAd()`: Load quảng cáo mới nếu cần thiết
  - `getInterAd()`: Lấy quảng cáo từ cache hoặc load mới
  - `showInterAd()`: Hiển thị quảng cáo với callback
  - `cleanupExpiredAds()`: Tự động dọn dẹp quảng cáo hết hiệu lực
  - `getCacheInfo()`: Lấy thông tin cache hiện tại
  - `clearCache()`: Xóa toàn bộ cache

### NativeAdManager (Quản lý quảng cáo Native với cache)
- **Chức năng chính**: Quản lý quảng cáo Native với hệ thống cache thông minh
- **Design Pattern**: Singleton pattern với thread-safe initialization
- **Cache Strategy**: Queue cache với timestamp validation (15 phút hiệu lực)
- **Key Methods**:
  - `preloadNativeAd()`: Load quảng cáo mới nếu cần thiết
  - `getNativeAd()`: Lấy quảng cáo từ cache hoặc load mới
  - `cleanupExpiredAds()`: Tự động dọn dẹp quảng cáo hết hiệu lực
  - `getCacheInfo()`: Lấy thông tin cache hiện tại
  - `clearCache()`: Xóa toàn bộ cache
  - `getValidAdCount()`: Lấy số lượng quảng cáo còn hiệu lực

### AdManager (Quản lý tổng thể quảng cáo)
- **Chức năng chính**: Quản lý toàn bộ hệ thống quảng cáo và UMP consent
- **Integration**: Tích hợp với InterstitialAdManager và NativeAdManager
- **Key Methods**:
  - `initAdsAndUmp()`: Khởi tạo Google Mobile Ads SDK và UMP
  - `fetchAdId()`: Lấy ad ID từ Firebase Remote Config
  - **Interstitial Methods**: `loadInterAd()`, `getInterAd()`, `showInterAd()`
  - **Native Methods**: `preloadNativeAd()`, `getNativeAd()`
  - **Cache Management**: `getInterstitialCacheInfo()`, `getNativeAdCacheInfo()`

### NativeAdView (Composable cho Native Ad)
- **Chức năng chính**: Composable để hiển thị quảng cáo Native với UI custom
- **UI Features**: 
  - App icon, AD label, title, subtitle
  - Star rating (3.5/5 stars)
  - Description text
  - MediaView với background trắng mờ nhẹ (180dp height)
- **States**: Loading, Error, Success
- **Integration**: Tự động load native ad từ AdManager

### SelectLanguageScreen (UI cho language selection)
- **Chức năng chính**: Màn hình chọn ngôn ngữ với tích hợp NativeAd
- **Features**: 
  - List languages từ LanguageData
  - Selection state với visual feedback
  - Install button
  - NativeAdView ở cuối màn hình
- **Language Support**: 9 ngôn ngữ (EN, PT-BR, JA, KO, DE, FR, HI, IT, VI)

### InterstitialAdDemo (Demo và testing)
- **Chức năng chính**: Cung cấp các ví dụ sử dụng và test cases
- **Demo Methods**:
  - `demoBasicUsage()`: Sử dụng cơ bản
  - `demoAdvancedUsage()`: Sử dụng nâng cao với cache management
  - `demoGetInterAd()`: Demo method getInterAd
  - `demoCacheManagement()`: Quản lý cache
  - `demoGameFlow()`: Sử dụng trong game flow
  - `demoErrorHandling()`: Xử lý lỗi

### NativeAdDemo (Demo NativeAd system)
- **Chức năng chính**: Demo và test NativeAd system
- **Demo Methods**:
  - `demoBasicUsage()`: Sử dụng cơ bản
  - `demoCacheManagement()`: Quản lý cache
  - `demoPerformanceAndTiming()`: Performance và timing
  - `demoErrorHandling()`: Xử lý lỗi
  - `demoUIIntegration()`: Integration với UI
  - `demoMultipleAds()`: Multiple ads scenario

## Architecture Features

### 1. Cache Management
- **ConcurrentLinkedQueue**: Thread-safe queue implementation cho NativeAd
- **Timestamp Validation**: Tự động kiểm tra hiệu lực quảng cáo
- **Auto Cleanup**: Tự động dọn dẹp quảng cáo cũ
- **Max Size Limit**: Giới hạn cache size (Interstitial: 3, Native: 2)

### 2. Smart Loading Strategy
- **Preload Prevention**: Không load thêm nếu cache đã đầy
- **Validity Check**: Kiểm tra quảng cáo còn hiệu lực trước khi load
- **Background Loading**: Load quảng cáo không block UI

### 3. Error Handling
- **Graceful Fallback**: Xử lý lỗi một cách graceful
- **Callback Support**: Hỗ trợ callback cho mọi operation
- **Logging**: Comprehensive logging với Logger utility

### 4. Performance Optimization
- **Memory Management**: Tự động cleanup để tránh memory leak
- **Efficient Cache**: Sử dụng queue cho NativeAd, stack cho Interstitial
- **Lazy Loading**: Chỉ load khi cần thiết

### 5. UI Integration
- **Composable Support**: NativeAdView dễ dàng tích hợp vào Compose UI
- **Custom Design**: UI custom theo design với icon, label, rating, MediaView
- **Responsive Layout**: Tự động adapt với different screen sizes

## Usage Patterns

### 1. Basic Integration
```kotlin
// Trong Activity
AdManager.showInterAd(this) { success ->
    if (success) {
        // User đã đóng quảng cáo
        continueFlow()
    } else {
        // Quảng cáo bị lỗi
        continueFlow()
    }
}
```

### 2. Preload Strategy
```kotlin
// Trong onCreate hoặc onStart
AdManager.loadInterAd(this)
AdManager.preloadNativeAd(this)
```

### 3. Cache Management
```kotlin
// Kiểm tra cache
val interstitialCacheInfo = AdManager.getInterstitialCacheInfo()
val nativeAdCacheInfo = AdManager.getNativeAdCacheInfo()
Log.d("Ads", "Interstitial: $interstitialCacheInfo, Native: $nativeAdCacheInfo")
```

### 4. NativeAd UI Integration
```kotlin
// Trong Compose UI
NativeAdView(
    modifier = Modifier.fillMaxWidth(),
    onAdLoaded = { nativeAd ->
        // Handle ad loaded
    },
    onAdFailedToLoad = { error ->
        // Handle ad failed
    }
)
```

### 5. SelectLanguageScreen Usage
```kotlin
// Trong Activity
setContent {
    SelectLanguageScreen(
        onLanguageSelected = { language ->
            // Handle language selection
        },
        onInstallClicked = {
            // Handle install button
        }
    )
}
```

## Dependencies
- Google Mobile Ads SDK
- Firebase Remote Config
- UMP (User Messaging Platform)
- Kotlin Coroutines (cho async operations)
- ConcurrentLinkedQueue (cho thread-safe cache)
- Compose Material Icons (cho UI elements)

## Configuration
- **Ad IDs**: Được cấu hình trong AdManager và có thể override qua Remote Config
- **Test Mode**: Tự động detect debug mode và sử dụng test ad IDs
- **Cache Settings**: 
  - Interstitial: MAX_CACHE_SIZE = 3, AD_VALIDITY_DURATION = 10 phút
  - Native: MAX_CACHE_SIZE = 2, AD_VALIDITY_DURATION = 15 phút
- **UI Settings**: MediaView height = 180dp, translucent white background 