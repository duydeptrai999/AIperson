# Help.md - Hướng dẫn sử dụng tính năng

## Weather Personalized App - AI Thời Tiết Cá Nhân Hóa

### Mô tả tính năng
Ứng dụng thời tiết thông minh sử dụng AI để cá nhân hóa thông tin dựa trên profile người dùng (tuổi, nơi sống, nghề nghiệp) và tạo hệ thống gamification với point/reward.

### Core Features

#### 1. AI Personalization Engine
- **Input**: Tuổi, nơi sống, nghề nghiệp của người dùng
- **Processing**: AI phân tích mức độ phù hợp của thời tiết với từng cá nhân
- **Output**: Weather Compatibility Score (0-100%) và điểm point tương ứng

#### 2. Smart Notification System
- **Frequency**: 1 notification/ngày tự động
- **Content**: "Hôm nay thời tiết phù hợp 85% với bạn! +7 points"
- **Timing**: Thông minh dựa trên lịch trình và sở thích người dùng
- **Point Earning**: Nhận point dựa trên Weather Compatibility Score

#### 3. Point & Reward System
- **Point Logic**: 
  - Perfect Weather (90-100%): 10 points
  - Good Weather (70-89%): 7 points
  - Average Weather (50-69%): 5 points
  - Poor Weather (30-49%): 3 points
  - Bad Weather (0-29%): 1 point
- **Reward Categories**: Weather tips, health advice, activity suggestions, fashion tips
- **Strategic Usage**: User có thể tích point để xem nhiều reward cùng lúc

### Cách hoạt động

#### Daily Flow
```
Morning: AI analyze weather + user profile → Calculate compatibility score → 
Send notification với point earned → User open app → View detailed reasoning → 
Spend points on rewards → Update AI learning
```

#### AI Personalization Logic
1. **Age-based Factors**:
   - Trẻ em (0-12): Ưu tiên an toàn, tránh thời tiết khắc nghiệt
   - Thanh niên (13-25): Ưu tiên hoạt động ngoài trời
   - Người trưởng thành (26-60): Cân bằng công việc và sức khỏe
   - Người cao tuổi (60+): Ưu tiên sức khỏe và an toàn

2. **Occupation-based Factors**:
   - Outdoor Jobs: Quan tâm nhiều đến thời tiết khắc nghiệt
   - Office Jobs: Quan tâm đến di chuyển và trang phục
   - Healthcare: Quan tâm đến tác động sức khỏe

3. **Location-based Factors**:
   - Climate zone, urban vs rural, coastal vs inland

### Technical Stack
- **Frontend**: Android Kotlin + Jetpack Compose
- **AI/ML**: TensorFlow Lite + Custom algorithms
- **Backend**: Firebase (FCM, Firestore, Auth)
- **Weather API**: OpenWeatherMap/WeatherAPI
- **Database**: Room (local) + Firebase (cloud)
- **Architecture**: MVVM + Clean Architecture

### User Experience
- **Onboarding**: Setup profile (age, location, occupation, preferences)
- **Daily Interaction**: Receive notification → Open app → View score reasoning → Browse rewards
- **Strategic Planning**: Save points for multiple rewards hoặc spend daily
- **Learning**: AI learns from user behavior để improve personalization

---

## NativeAdView Refresh Capability

### Mô tả tính năng
Tính năng `showNewNativeAd` cho phép refresh native ad mới trong `NativeAdView`, đặc biệt hữu ích cho IntroScreen khi user chuyển slide.

### Cách hoạt động
1. **NativeAdViewWithRefresh**: Composable mới hỗ trợ refresh native ad
2. **NativeAdViewController**: Controller class để điều khiển refresh từ bên ngoài
3. **Auto Refresh**: Tự động refresh khi slide thay đổi trong IntroScreen
4. **Memory Management**: Proper cleanup native ad cũ trước khi load mới
5. **UI State Management**: Sử dụng loading state để hiển thị trạng thái refresh

### Flow hoạt động chi tiết
```
User click NEXT → currentSlideIndex++ → LaunchedEffect trigger → 
showNewNativeAd() → Destroy old ad → Set loading=true → 
Load new ad → Set loading=false → Update UI với native ad mới
```

### Sử dụng trong IntroScreen
```kotlin
// Sử dụng NativeAdViewWithRefresh thay vì NativeAdView
val controller = NativeAdViewWithRefresh(
    modifier = Modifier.fillMaxWidth(),
    enableButtonOnTop = false,
    enableOutlineButton = true,
    onAdLoaded = { nativeAd ->
        Logger.d("IntroScreen: Native ad loaded: ${nativeAd != null}")
    }
)

// Lưu controller để có thể gọi showNewNativeAd
LaunchedEffect(controller) {
    nativeAdController = controller
}

// Tự động refresh khi slide thay đổi
LaunchedEffect(currentSlideIndex) {
    if (currentSlideIndex > 0 && nativeAdController != null) {
        nativeAdController?.showNewNativeAd()
    }
}
```

### States của NativeAdViewWithRefresh
1. **Loading State**: Hiển thị "Loading new native ad..." khi đang refresh
2. **Native Ad State**: Hiển thị native ad khi load thành công
3. **No Ad State**: Hiển thị "No native ad available" khi không có ad

### Lợi ích
- **Tăng Engagement**: Mỗi slide hiển thị native ad mới
- **Tăng Revenue**: Nhiều cơ hội hiển thị quảng cáo
- **Memory Safe**: Proper cleanup để tránh memory leak
- **User Experience**: Native ad mới cho mỗi slide, không bị lặp lại
- **Visual Feedback**: Loading state cho user biết đang refresh ad

### Technical Details
- **MVVM Pattern**: Sử dụng Controller pattern để quản lý state
- **State Management**: Sử dụng `isLoading` state để track refresh status
- **Callback System**: `onAdLoaded` callback để track trạng thái load ad
- **Compose Integration**: Sử dụng `LaunchedEffect` để trigger refresh
- **Ad Lifecycle**: Proper destroy và load native ad mới
- **UI Updates**: Compose tự động re-render khi state thay đổi 