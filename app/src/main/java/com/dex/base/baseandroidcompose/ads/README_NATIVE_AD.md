# NativeAd System - Hướng dẫn sử dụng

## Tổng quan

NativeAd System là một hệ thống quản lý quảng cáo Native với cache thông minh, được thiết kế để dễ dàng tích hợp vào Compose UI. **Design UI match 100% với layout XML `native_ad_view_with_media_medium.xml`**.

Hệ thống bao gồm:

- **NativeAdManager**: Quản lý cache và loading quảng cáo
- **NativeAdView**: Composable UI với design custom chính xác
- **AdManager Integration**: Wrapper methods để dễ sử dụng

## Tính năng chính

### 1. Cache Management
- **Cache Size**: Tối đa 2 quảng cáo trong cache
- **Validity Duration**: 15 phút hiệu lực cho mỗi quảng cáo
- **Auto Cleanup**: Tự động dọn dẹp quảng cáo hết hiệu lực
- **Thread Safety**: Sử dụng ConcurrentLinkedQueue

### 2. Smart Loading
- **Preload Strategy**: Load quảng cáo trước khi cần
- **Cache Check**: Kiểm tra cache trước khi load mới
- **Background Loading**: Không block UI thread

### 3. UI Integration
- **Composable Support**: Dễ dàng tích hợp vào Compose UI
- **Exact Design Match**: UI match 100% với XML layout design
- **Responsive Layout**: Tự động adapt với screen sizes

## Design Specifications

### Layout Structure (Match với XML)
```
┌─────────────────────────────────────────┐
│ [App Icon 54dp] [AD Label] [Headline] │
│           [Rating Bar 5★]              │
│           [Body Text]                  │
│           [MediaView 180dp]            │
│        [Call to Action Button]         │
│           [Bottom Divider]             │
└─────────────────────────────────────────┘
```

### UI Components Details
- **App Icon**: 54dp x 54dp với rounded corners
- **AD Label**: Badge "AD" màu #FFCC00, padding 3dp x 2.3dp
- **Headline**: Font size 15sp, bold, maxLines = 1
- **Body Text**: Font size 12sp, maxLines = 1
- **MediaView**: 180dp height với background #F5F5F5
- **CTA Button**: 56dp height với primary color, hoạt động khi click
- **Bottom Divider**: 1dp height với color #CCEBEBEB
- **Padding**: Horizontal 8dp, top/bottom 4dp
- **Overall Height**: 320dp
- **No Rating View**: Không hiển thị star rating
- **Full Width**: Không có Card wrapper, full width không bo góc

## Cách sử dụng

### Basic Usage
```kotlin
// Sử dụng mặc định (button ở bottom)
NativeAdView(
    modifier = Modifier.fillMaxWidth()
)

// Button ở top
NativeAdView(
    modifier = Modifier.fillMaxWidth(),
    enableButtonOnTop = true
)
```

### Parameters
- **modifier**: Modifier cho layout
- **enableButtonOnTop**: Boolean để điều khiển vị trí action button
  - `false` (mặc định): Button ở bottom, sau MediaView
  - `true`: Button ở top trên cùng, trước cả icon và label
- **onAdLoaded**: Callback khi ad load thành công
- **onAdFailedToLoad**: Callback khi ad load thất bại

### 2. UI Integration

```kotlin
// Trong Compose UI
@Composable
fun MyScreen() {
    Column {
        // Your content here
        
        // Native Ad ở cuối màn hình
        NativeAdView(
            modifier = Modifier.fillMaxWidth(),
            onAdLoaded = { nativeAd ->
                // Handle ad loaded successfully
            },
            onAdFailedToLoad = { error ->
                // Handle ad failed to load
            }
        )
    }
}
```

### 3. Manual Ad Management

```kotlin
// Preload quảng cáo
AdManager.preloadNativeAd(context) { success, message ->
    if (success) {
        Log.d("Ads", "Native ad preloaded: $message")
    } else {
        Log.e("Ads", "Failed to preload: $message")
    }
}

// Lấy quảng cáo từ cache
AdManager.getNativeAd(context) { nativeAd ->
    if (nativeAd != null) {
        // Use the native ad
        Log.d("Ads", "Got native ad: ${nativeAd.headline}")
    } else {
        // No ad available
        Log.e("Ads", "No native ad available")
    }
}
```

### 4. Cache Management

```kotlin
// Kiểm tra cache info
val cacheInfo = AdManager.getNativeAdCacheInfo()
Log.d("Ads", "Cache info: $cacheInfo")

// Xóa cache (để debug hoặc reset)
AdManager.clearNativeAdCache()

// Lấy số lượng quảng cáo còn hiệu lực
val adCount = AdManager.getNativeAdCount()
Log.d("Ads", "Valid ads: $adCount")
```

## UI Components

### NativeAdView Composable

```kotlin
@Composable
fun NativeAdView(
    modifier: Modifier = Modifier,
    onAdLoaded: ((NativeAd) -> Unit)? = null,
    onAdFailedToLoad: ((String) -> Unit)? = null
)
```

**Features (Match với XML):**
- **App Icon**: Icon thật từ NativeAd.icon (54dp x 54dp) với fallback emoji 🎮
- **AD Label**: Badge "AD" màu #FFCC00 với padding chính xác
- **Headline**: Tên app quảng cáo (15sp, bold) từ NativeAd.headline
- **Body Text**: Mô tả app quảng cáo (12sp, maxLines = 2) từ NativeAd.body
- **MediaView**: 180dp height với background #F5F5F5, hiển thị media content thật từ NativeAd.mediaContent
- **Call to Action Button**: 46dp height với rounded corners (8dp), Compose theme primary color, hoạt động khi click, text từ NativeAd.callToAction
- **Bottom Divider**: 1dp height với color #CCEBEBEB, chỉ hiển thị khi button ở bottom
- **Full Width Layout**: Không có Card wrapper, full width không bo góc
- **Smart Spacing**: Mỗi item có margin left/right 8dp riêng biệt, divider có margin bottom 4dp
- **Flexible Button Position**: Có thể đặt button ở top trên cùng hoặc bottom thông qua param `enableButtonOnTop`
- **Real Data Integration**: Lấy data thực từ NativeAd (headline, body, callToAction, icon, mediaContent)
- **Click Handling**: Action button và MediaView hoạt động khi click, Google Ads SDK tự động handle

**States:**
- **Loading**: Hiển thị CircularProgressIndicator
- **Error**: Hiển thị error message
- **Success**: Hiển thị quảng cáo với design chính xác

## Configuration

### Ad IDs
```kotlin
// Trong AdManager
var ADMOB_NATIVE_AD_ID = "ca-app-pub-9821898502051437/7710456674"

// Có thể override qua Firebase Remote Config
fun fetchAdId(remoteConfig: FirebaseRemoteConfig) {
    ADMOB_NATIVE_AD_ID = remoteConfig.getString("ADMOB_NATIVE_AD_ID")
}
```

### Cache Settings
```kotlin
// Trong NativeAdManager
private const val MAX_CACHE_SIZE = 2
private const val AD_VALIDITY_DURATION_MINUTES = 15L
```

### Native Ad Options
```kotlin
val nativeAdOptions = NativeAdOptions.Builder()
    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
    .setRequestCustomMuteThisAd(true)
    .setVideoOptions(
        VideoOptions.Builder()
            .setStartMuted(true)
            .build()
    )
    .build()
```

## Best Practices

### 1. Preload Strategy
```kotlin
// Preload sớm để có quảng cáo sẵn sàng
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AdManager.preloadNativeAd(this)
}

override fun onResume() {
    super.onResume()
    // Preload thêm nếu cần
    AdManager.preloadNativeAd(this)
}
```

### 2. Error Handling
```kotlin
NativeAdView(
    onAdFailedToLoad = { error ->
        // Hide ad container hoặc show fallback content
        Log.e("Ads", "Native ad failed: $error")
    }
)
```

### 3. Performance Optimization
```kotlin
// Không preload quá nhiều quảng cáo
// Cache size limit là 2, nên chỉ preload khi cần thiết

// Sử dụng LaunchedEffect để preload một lần
LaunchedEffect(Unit) {
    AdManager.preloadNativeAd(context)
}
```

### 4. UI Placement
```kotlin
// Đặt NativeAdView ở vị trí phù hợp
// Không đặt ở đầu màn hình (có thể gây khó chịu)
// Nên đặt ở cuối content hoặc giữa các section

Column {
    // Main content
    LazyColumn { ... }
    
    // Native Ad
    NativeAdView(modifier = Modifier.fillMaxWidth())
    
    // Footer content
}
```

## Testing

### Demo Methods
```kotlin
val demo = NativeAdDemo()

// Basic usage
demo.demoBasicUsage(activity)

// Cache management
demo.demoCacheManagement(activity)

// Performance testing
demo.demoPerformanceAndTiming(activity)

// Error handling
demo.demoErrorHandling(activity)

// UI integration
demo.demoUIIntegration(activity)

// Multiple ads
demo.demoMultipleAds(activity)
```

### Test Ad IDs
```kotlin
// Sử dụng test ad ID trong debug mode
if (Helper.isDebugMode()) {
    // Test ad ID sẽ được set tự động
}
```

## Troubleshooting

### Common Issues

1. **Ad không load được**
   - Kiểm tra internet connection
   - Kiểm tra ad ID có đúng không
   - Kiểm tra Firebase configuration
   - Kiểm tra log để debug

2. **Cache không hoạt động**
   - Kiểm tra cache info: `AdManager.getNativeAdCacheInfo()`
   - Kiểm tra validity duration (15 phút)
   - Kiểm tra max cache size (2 ads)

3. **UI không hiển thị đúng design**
   - Kiểm tra NativeAdView có được gọi đúng không
   - Kiểm tra modifier và layout
   - Kiểm tra ad state (loading/error/success)
   - **Design match**: UI đã được cập nhật để match 100% với XML layout

### Debug Commands
```kotlin
// Kiểm tra cache
val cacheInfo = AdManager.getNativeAdCacheInfo()
Log.d("Ads", "Cache: $cacheInfo")

// Kiểm tra ad count
val adCount = AdManager.getNativeAdCount()
Log.d("Ads", "Valid ads: $adCount")

// Clear cache để test
AdManager.clearNativeAdCache()
```

## Performance Tips

1. **Preload sớm**: Preload quảng cáo ngay khi activity start
2. **Cache management**: Không preload quá nhiều quảng cáo
3. **Background loading**: Sử dụng background thread cho ad loading
4. **UI optimization**: Sử dụng LazyColumn để lazy load ads

## Integration Examples

### SelectLanguageScreen
```kotlin
@Composable
fun SelectLanguageScreen() {
    Column {
        // Language list
        LazyColumn { ... }
        
        // Install button
        Button { ... }
        
        // Native Ad với design chính xác
        NativeAdView(modifier = Modifier.fillMaxWidth())
    }
}
```

### MainActivity
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Preload native ads
        AdManager.preloadNativeAd(this)
        
        setContent {
            // Your UI
        }
    }
}
```

## Design Fidelity

**NativeAdView đã được cập nhật để match 100% với design mong muốn:**

- ✅ **Layout Structure**: Match với native_ad_view_with_media_medium.xml
- ✅ **Dimensions**: App icon 54dp, MediaView 180dp, Button 56dp
- ✅ **Colors**: AD label #FFCC00, MediaView #F5F5F5, Divider #CCEBEBEB
- ✅ **Typography**: Headline 15sp, Body 12sp
- ✅ **Spacing**: Padding 8dp horizontal, 4dp vertical
- ✅ **Overall Height**: 320dp để accommodate design mới
- ✅ **No Rating View**: Loại bỏ star rating và rating text
- ✅ **Full Width**: Không có Card wrapper, full width không bo góc
- ✅ **Real Data**: Lấy data thực từ NativeAd (headline, body, callToAction)
- ✅ **Click Handling**: Action button và MediaView hoạt động khi click

## Support

Nếu gặp vấn đề, hãy kiểm tra:
1. Log output để debug
2. Cache info và ad count
3. Network connection
4. Firebase configuration
5. Ad ID configuration
6. **Design match**: UI đã được cập nhật để match chính xác với XML layout

Hệ thống được thiết kế để graceful fallback khi có lỗi, nên app sẽ không crash khi quảng cáo có vấn đề. 

## Technical Implementation

**NativeAdView đã được rebuild hoàn toàn để đảm bảo hoạt động đúng cách:**

- **Single NativeAdView Container**: 
  - Sử dụng một AndroidView duy nhất chứa toàn bộ NativeAdView
  - Đảm bảo proper binding với Google Ads SDK
  - Không còn mix giữa Compose và AndroidView cho từng component

- **Proper View Binding**: Tất cả views được bind đúng cách
  - `iconView` → Icon của app quảng cáo từ `nativeAd.icon?.drawable`
  - `headlineView` → Headline từ `nativeAd.headline`
  - `bodyView` → Description từ `nativeAd.body`
  - `mediaView` → MediaView để hiển thị media content từ `nativeAd.mediaContent`
  - `callToActionView` → Button từ `nativeAd.callToAction`

- **Native Layout System**: 
  - Sử dụng LinearLayout với Android Views
  - Tương thích hoàn toàn với Google Ads SDK
  - Pixel-perfect layout matching với XML design

- **Real Data Integration**: 
  - Icon thật từ NativeAd được hiển thị
  - MediaView hoạt động và hiển thị content
  - Button clickable và Google Ads SDK tự động handle
  - Tất cả text content lấy từ NativeAd object

- **Performance & Reliability**:
  - Single AndroidView container giảm overhead
  - Proper SDK integration đảm bảo ad tracking
  - Error handling với fallback content 