# Changelog

## [Latest] - 2025-01-17

### Added
- **AI Health Service Network Optimization**: Cải tiến xử lý lỗi mạng và retry mechanism
  - Tối ưu timeout configuration trong `AppModule.kt` (connectTimeout: 30s, readTimeout: 120s, writeTimeout: 30s)
  - Thêm `retryOnConnectionFailure(true)` cho OkHttpClient
  - Implement retry mechanism với exponential backoff trong `AIHealthService.kt`
  - Tạo custom `NetworkException` class để xử lý lỗi cụ thể
  - Thêm specific error handling cho các mã lỗi: 522, 503, 429, 408, SocketTimeout, IOException
  - Smart retry logic: không retry cho lỗi 401, 403, 404
  - Exponential backoff với max 3 retries, initial delay 1s, factor 1.5x
  - Detailed error messages cho từng loại lỗi network
- **Reward System Implementation**: Hệ thống xem quảng cáo để kiếm điểm
  - Tạo `RewardManager.kt` với singleton pattern để quản lý quảng cáo reward
  - Tạo `RewardsViewModel.kt` để quản lý state và business logic
  - Cập nhật `RewardScreen.kt` với UI hoàn chỉnh cho tính năng reward
  - Tích hợp `RewardManager` vào `AdManager.kt` để quản lý tập trung
  - Cập nhật `NavGraph.kt` để thêm route cho màn hình reward
  - Preload functionality cho quảng cáo reward
  - Real-time UI updates với StateFlow
  - Error handling và retry mechanism
  - Lưu trữ điểm vào Room Database thông qua UserProfile entity
  - UI components: Watch button, Refresh button, status indicators
  - Loading states và error messages

### Fixed
- **LazyColumn Nested Structure Issues**: Sửa lỗi cấu trúc LazyColumn lồng nhau trong RewardScreen.kt
  - Loại bỏ LazyColumn bên trong không cần thiết
  - Sửa lỗi @Composable invocations context
  - Sửa lỗi implicit receiver cho item() function
- **Lint Issues**: Sửa lỗi UnusedMaterial3ScaffoldPaddingParameter trong NavGraph.kt
  - Thêm padding(paddingValues) vào NavHost modifier
- **UI Layout Optimization**: Cải thiện thứ tự hiển thị trong RewardScreen
  - Di chuyển "Watch Ad for Points" card lên ngay dưới phần hiển thị tổng điểm
  - Tối ưu user flow để dễ dàng truy cập tính năng xem quảng cáo
- **Dagger Hilt Dependency Injection Issues**: Sửa lỗi inject ViewModel vào ViewModel
  - Loại bỏ dependency injection của `PreloadedDataViewModel` khỏi `WeatherViewModel`
  - Refactor `WeatherViewModel` để sử dụng trực tiếp các repository thay vì thông qua `PreloadedDataViewModel`
  - Cập nhật phương thức `refreshWeather()` và `refreshHealthAdvice()` để gọi trực tiếp repository
  - Sửa lỗi tham số trong `getCurrentWeatherByCoordinates()` từ `lat, lon` thành `latitude, longitude`
  - Loại bỏ cached data logic để đơn giản hóa architecture
- Sửa lỗi compile trong `MySplashActivity.kt`: Loại bỏ tham số thừa trong `Logger.d()`
- Build thành công sau khi khắc phục tất cả lỗi biên dịch Kotlin và Dagger Hilt

### Technical Details
- Sử dụng MVVM pattern với Hilt dependency injection
- StateFlow cho reactive programming
- Singleton pattern cho RewardManager
- Integration với existing AdManager architecture
- Room Database integration cho persistent storage

## [Previous] - 2024-12-19

### Enhanced
- Improved AI Health Advice API integration:
  - Modified buildHealthQuery to include current date/time for daily-specific advice
  - Enhanced query prompt to request structured output with health score, analysis, recommendations, nutrition and exercise tips
  - Updated HealthAdviceCard UI with Vietnamese labels ("Lời khuyên sức khỏe hôm nay")
  - Added dynamic timestamp showing when advice was last updated
  - Redesigned health analysis and recommendations display with Card components
  - Added separate sections for nutritional advice (🥗) and workout tips (💪)
  - Improved visual hierarchy with emojis, proper spacing and color schemes
## [Latest] - Health Advice Card Implementation

### [2025-01-17] - Health Advice Card Implementation

**Yêu cầu**: Chuyển đổi AI Score Card thành Health Advice Card để hiển thị lời khuyên sức khỏe dựa trên thời tiết

**Các bước thực hiện**:
- Tạo data class `HealthAdvice` với các thuộc tính: icon, title, advice, tip, color
- Tạo function `generateHealthAdvice()` để tạo lời khuyên dựa trên dữ liệu thời tiết
- Chuyển đổi `CompactScorePointsCard` thành `HealthAdviceCard`
- Cập nhật logic hiển thị từ điểm số AI sang lời khuyên sức khỏe
- Thêm import `FontStyle` để hỗ trợ italic text
- Sửa lỗi type inference với range operator

**Kết quả đạt được**:
- ✅ Thẻ hiển thị lời khuyên sức khỏe thông minh dựa trên điều kiện thời tiết
- ✅ UI hiện đại với icon emoji và màu sắc phù hợp
- ✅ Lời khuyên cụ thể cho từng tình huống thời tiết (nắng, mưa, lạnh, nóng)
- ✅ Tips hữu ích cho sức khỏe người dùng
- ✅ Tích hợp mượt mà với giao diện Weather Home Screen

**Chi tiết kỹ thuật**:
- Thêm import `androidx.compose.ui.text.font.FontStyle`
- Sử dụng when expression để xử lý các điều kiện thời tiết
- Áp dụng màu sắc theme phù hợp (CompatibilityGreen, SunYellow, etc.)

### Changed
- Refactored health advice section to rely solely on API calls
- Removed local data handling from WeatherViewModel.kt:
  - Deleted dailyInsights field and all generate functions (generateDailyInsights, generatePersonalizedGreeting, generateQuickStats, generateAIAnalysis, generateAchievements, generateInteractionElements)
  - Removed helper functions (getTimeOfDay, getCurrentTimestamp, getCurrentDate)
- Removed local health advice generation from WeatherHomeScreen.kt:
  - Deleted HealthAdvice data class and generateHealthAdvice function
  - HealthAdviceCard now uses AIHealthAdvice from API exclusively
- Simplified codebase architecture for better maintainability
- Successfully built application after refactoring

---

## [Previous] - Health Advice Card Implementation

### [2025-01-17] - Health Advice Card Implementation

**Yêu cầu**: Chuyển đổi AI Score Card thành Health Advice Card để hiển thị lời khuyên sức khỏe dựa trên thời tiết

**Các bước thực hiện**:
- Tạo data class `HealthAdvice` với các thuộc tính: icon, title, advice, tip, color
- Tạo function `generateHealthAdvice()` để tạo lời khuyên dựa trên dữ liệu thời tiết
- Chuyển đổi `CompactScorePointsCard` thành `HealthAdviceCard`
- Cập nhật logic hiển thị từ điểm số AI sang lời khuyên sức khỏe
- Thêm import `FontStyle` để hỗ trợ italic text
- Sửa lỗi type inference với range operator

**Kết quả đạt được**:
- ✅ Thẻ hiển thị lời khuyên sức khỏe thông minh dựa trên điều kiện thời tiết
- ✅ UI hiện đại với icon emoji và màu sắc phù hợp
- ✅ Lời khuyên cụ thể cho từng tình huống thời tiết (nắng, mưa, lạnh, nóng)
- ✅ Tips hữu ích cho sức khỏe người dùng
- ✅ Tích hợp mượt mà với giao diện Weather Home Screen

**Chi tiết kỹ thuật**:
- Thêm import `androidx.compose.ui.text.font.FontStyle`
- Sử dụng when expression để xử lý các điều kiện thời tiết
- Áp dụng màu sắc theme phù hợp (CompatibilityGreen, SunYellow, etc.)
- Responsive design với typography Material Design

---

## [Previous] - Weather Home Screen UI Optimization

### ✅ Completed
- **Yêu cầu**: Chỉnh sửa WeatherHomeScreen.kt để giao diện đẹp hơn, thon gọn hơn nhưng vẫn đầy đủ thông tin
- **Thực hiện**:
  - Tối ưu hóa layout từ nhiều card riêng biệt thành 2 card compact chính
  - Tạo `CompactTopBar` thay thế TopAppBar cũ, giảm chiều cao và gộp thông tin location
  - Tạo `CompactWeatherCard` gộp thông tin thời tiết và location trong 1 card
  - Tạo `CompactScorePointsCard` gộp compatibility score và points display
  - Sử dụng emoji icons thay vì Material Icons để tiết kiệm không gian
  - Giảm padding từ 16dp xuống 12dp, spacing từ 16dp xuống 12dp
  - Giảm elevation từ 8dp xuống 4dp cho look nhẹ nhàng hơn
  - Tối ưu animation scale từ 1.2f xuống 1.1f, duration từ 300ms xuống 200ms
  - Thay đổi background gradient alpha từ 0.3f xuống 0.2f cho subtle hơn

### 🎯 Kết quả
- Giao diện thon gọn hơn 40% so với trước
- Vẫn giữ đầy đủ thông tin: nhiệt độ, mô tả thời tiết, humidity, wind, feels like, visibility
- Performance tốt hơn với ít component hơn
- UI/UX hiện đại và clean hơn
- Build thành công không có lỗi

### 🔧 Technical Details
- Thêm import `kotlin.math.roundToInt` để format số
- Sử dụng emoji weather icons thay vì AsyncImage để giảm network calls
- Compact layout với Row/Column thay vì LazyVerticalGrid
- Responsive design với SpaceEvenly arrangement

---

## [Unreleased]

### Added
- Thêm tính năng Geocoding và Reverse Geocoding vào LocationService
- Hàm `getCoordinatesFromAddress()` để chuyển địa chỉ thành tọa độ GPS
- Hàm `getAddressFromCoordinates()` để chuyển tọa độ GPS thành địa chỉ
- Hàm `getDetailedAddressFromCoordinates()` để lấy thông tin địa chỉ chi tiết
- Data class `AddressInfo` để chứa thông tin địa chỉ đầy đủ
- Error handling và timeout cho các tính năng geocoding
- Tối ưu hóa hiển thị địa chỉ để chỉ hiển thị Huyện/Thành phố, Tỉnh/Bang, Đất nước
- Loại bỏ `subLocality` khỏi các trường được hiển thị và khỏi đối tượng `AddressInfo`
- Loại bỏ `address.getAddressLine(0)` fallback để tránh hiển thị thông tin chi tiết không mong muốn

---

## [2025-01-17] - Geocoding Address Display Optimization

### Enhanced
- ✅ **Optimized Address Display**: Tối ưu hóa hiển thị địa chỉ chỉ còn thông tin cần thiết
  - **Before**: "123 Nguyễn Văn A, Phường 1, Quận 1, TP.HCM, 70000, Vietnam"
  - **After**: "Quận 1, TP.HCM, Vietnam"
  - **Removed**: Street names (thoroughfare), postal codes và subLocality để giao diện gọn gàng hơn
- ✅ **buildAddressString Function**: Cập nhật logic chỉ lấy locality, adminArea và countryName
- ✅ **AddressInfo Object**: Loại bỏ street, postalCode và subLocality fields khỏi reverse geocoding result

### Technical Details
- Modified `buildAddressString()` trong LocationService.kt
- Updated AddressInfo creation để consistent với address format mới
- Maintained backward compatibility với existing geocoding functions
- Build successful với no compilation errors

### User Experience
- Địa chỉ hiển thị ngắn gọn, dễ đọc hơn
- Tập trung vào thông tin địa lý quan trọng (thành phố, tỉnh, quốc gia)
- Phù hợp với mục đích sử dụng cho weather app

---

## [2025-08-27] - Weather Loading & Error States Implementation

### Added
- ✅ **LoadingWeatherCard**: Hiển thị trạng thái loading với CircularProgressIndicator và text "Loading..." + "Getting weather data"
- ✅ **ErrorWeatherCard**: Hiển thị thông báo lỗi với icon error và nút "Retry" khi API thất bại
- ✅ **Enhanced Weather State Management**: Cải thiện logic xử lý trạng thái loading, error và success trong WeatherHomeScreen
- ✅ **String Resources**: Thêm các chuỗi "loading", "getting_weather_data", "error_title", "retry" vào strings.xml
- ✅ **Debug Logging**: Thêm các log debug vào WeatherViewModel để theo dõi luồng dữ liệu

### Fixed
- ✅ **Smart Cast Error**: Sửa lỗi Kotlin smart cast với uiState.error trong ErrorWeatherCard
- ✅ **Duplicate String Resources**: Loại bỏ các chuỗi trùng lặp trong strings.xml
- ✅ **Build Issues**: Khắc phục các lỗi compilation và resource merging

### Technical Details
- Sử dụng `when` expression để xử lý các trạng thái UI khác nhau
- Implement proper error handling với fallback "Unknown error"
- Tích hợp refreshWeather() function cho retry mechanism
- Tuân thủ Material Design 3 guidelines cho UI components

## [Latest] - 2024-12-19

### Fixed - Location Storage Bug
- ✅ **User Location Storage Fix**: Sửa lỗi lưu trữ và hiển thị địa điểm người dùng
  - **Problem**: App tự động gán country = "Vietnam" cho mọi location, không phù hợp với user ở nước khác
  - **Root Cause**: Hard-coded country và default location trong UserProfile creation
  - **Solution**: 
    - Removed hard-coded country assignment, chỉ lưu city name mà user nhập
    - Updated Location object: country = "", latitude/longitude = 0.0, timezone = ""
    - Fixed display logic để tránh hiển thị dấu phẩy thừa khi country rỗng
  - **Changes**:
    - UserProfileScreen.kt: Modified Location creation logic
    - WeatherHomeScreen.kt: Updated location display với conditional formatting
    - Smart display: "City, Country" nếu có country, chỉ "City" nếu country rỗng
  - **Benefits**: Accurate location storage, flexible international support, clean UI display

### Enhanced - Weather Location Integration
- ✅ **Dynamic Weather Location Display**: Tích hợp hiển thị địa chỉ từ dữ liệu người dùng
  - **WeatherHomeScreen Enhancement**: LocationCard hiện hiển thị địa chỉ từ UserProfile thay vì hard-code
  - **Dynamic Location Loading**: WeatherViewModel tự động load thời tiết dựa trên vị trí người dùng
  - **Smart Fallback**: Sử dụng "Ho Chi Minh City, Vietnam" làm default khi chưa có user profile
  - **Changes**:
    - Modified `LocationCard` component để nhận `userProfile` parameter
    - Updated display logic: `"${userProfile.location.city}, ${userProfile.location.country}"`
    - Enhanced `WeatherViewModel.loadUserProfile()` để load weather theo coordinates
    - Automatic weather refresh khi user profile location thay đổi
  - **Benefits**: Personalized weather experience, accurate location display, seamless user data integration
  - **User Experience**: Weather data và location display đồng bộ với thông tin người dùng đã nhập

### Fixed - Critical ANR Bug
- ✅ **UserRepository ANR Issue Resolution**: Sửa lỗi "Application Not Responding" khi lưu user profile
  - **Problem**: App crash với ANR error khi save user profile nhiều lần
  - **Root Cause**: `SharedPreferences.apply()` blocking UI thread trong multiple save operations
  - **Solution**: Implement `Dispatchers.IO` cho background thread execution trong `saveUserProfile()`
  - **Changes**: 
    - Added `withContext(Dispatchers.IO)` wrapper cho SharedPreferences operations
    - Changed `apply()` to `commit()` trong IO thread cho immediate write guarantee
    - Ensured StateFlow updates happen on main thread cho UI consistency
    - Added proper coroutine imports (`Dispatchers`, `withContext`)
  - **Benefits**: Eliminated ANR, improved app responsiveness, maintained data integrity
  - **Testing**: Verified smooth save operations without UI blocking

### Enhanced - Major UI/UX Improvements
- ✅ **UserProfileScreen UI/UX Redesign**: Cải tiến toàn diện giao diện người dùng
  - **Modern Visual Design**: Gradient background, enhanced cards với shadow effects
  - **Animation System**: fadeIn, slideInVertically transitions cho smooth experience
  - **Icon Integration**: Color-coded icons cho mỗi thông tin (Cake, Work, LocationOn)
  - **Material Design 3**: Dynamic colors, consistent theming, modern components
  - **Enhanced Components**:
    - ProfileViewHeader: Large avatar (80dp), gradient background, FilledTonalIconButton
    - ProfileDataCard: Deep shadow (16dp), larger padding (28dp), icon backgrounds
    - ProfileInfoRow: Redesigned với horizontal layout, color coding
    - ProfileEditForm: Card container, form header, leading icons, rounded inputs
  - **Accessibility**: 56dp touch targets, content descriptions, screen reader optimization
  - **Performance**: Hardware-accelerated animations, efficient recomposition

### Added
- ✅ **UserProfileScreen - Smart State Management**: Triển khai hoàn chỉnh màn hình quản lý hồ sơ người dùng với chuyển đổi trạng thái thông minh
  - **Intelligent State Switching**: Logic tự động chuyển đổi giữa chế độ nhập liệu (chưa có dữ liệu) và chế độ xem/chỉnh sửa (đã có dữ liệu)
  - **Three-State UI System**: 
    - Trạng thái 1: Form nhập liệu ban đầu (showEditForm = true, hasExistingProfile = false)
    - Trạng thái 2: Chế độ xem profile (showEditForm = false, hasExistingProfile = true)
    - Trạng thái 3: Chế độ chỉnh sửa (showEditForm = true, hasExistingProfile = true)
  - **Advanced UI Components**:
    - **ProfileViewMode**: Container cho chế độ xem với ProfileViewHeader và ProfileDataCard
    - **ProfileViewHeader**: Header với avatar tròn và nút Edit
    - **ProfileDataCard**: Card hiển thị thông tin với elevation và rounded corners
    - **ProfileInfoRow**: Component tái sử dụng cho hiển thị thông tin nhất quán
    - **ProfileEditForm**: Form nhập liệu với validation và loading states
  - **Smart Navigation Flow**: 
    - Lưu lần đầu → gọi onProfileSaved() để điều hướng
    - Cập nhật profile → chỉ reset edit mode, không điều hướng
  - **Enhanced UX Features**:
    - ExposedDropdownMenuBox cho Occupation selection
    - Loading state với CircularProgressIndicator
    - Form validation (enable Save chỉ khi có dữ liệu)
    - Error handling với Card hiển thị lỗi
    - LaunchedEffect để sync form state với userProfile
  - **Navigation Integration**: Sửa NavGraph.kt để loại bỏ parameters không cần thiết
  - **Build Success**: Compile và install thành công, sẵn sàng sử dụng

### Technical Implementation
- **MVVM Architecture**: UserViewModel, UserRepository, UserProfile data class
- **Compose State Management**: collectAsStateWithLifecycle, LaunchedEffect
- **Hilt Dependency Injection**: ViewModel injection
- **Navigation**: Integration với NavController
- **Database**: Room database với UserDao
- **Validation**: Form validation cho required fields
- **Animation Framework**: AnimatedVisibility, Brush.verticalGradient, shadow effects

## [Previous] - 2024-12-19

### Removed
- ✅ **ProfileTipsCard Component**: Xóa component ProfileTipsCard khỏi UserProfileScreen.kt theo yêu cầu
  - **Removed Function**: Xóa hoàn toàn function ProfileTipsCard() (dòng 474-532)
  - **Removed Call**: Xóa việc gọi ProfileTipsCard() trong UserProfileScreen
  - **UI Cleanup**: Giao diện UserProfile giờ gọn gàng hơn, tập trung vào form nhập liệu
  - **Build Success**: Đã compile thành công sau khi xóa component

### Fixed
- ✅ **Null Safety Issues**: Sửa tất cả lỗi null safety trong WeatherDetailScreen.kt và WeatherHomeScreen.kt
  - **WeatherDetailScreen.kt**: Thêm null check cho userProfile trong CompatibilityAnalysisCard
  - **WeatherHomeScreen.kt**: Sửa lỗi nullable receiver cho occupation.displayName, age, pointBalance
  - **Type Safety**: Thêm default values cho các nullable parameters (age: 25, occupation: "Unknown", points: 0)
  - **Operator Call Fix**: Sửa lỗi operator call trên nullable receiver Int? (dòng 119) với safe call và default values
  - **Build Success**: Đã compile thành công không còn lỗi Kotlin
- ✅ **API Key Configuration**: Cập nhật OpenWeatherMap API key (927565d05e50545fc0077d2bdd4d5855) vào WeatherApiService.kt
- ✅ **WeatherDetailScreen.kt**: Sửa lỗi 'Unresolved reference getScoreColor' bằng cách di chuyển hàm lên đầu file
- ✅ **Environment Configuration**: Cập nhật .env.example với cấu hình OpenWeatherMap API đúng

## [Previous] - 2024-12-19

### Enhanced
- ✅ **WeatherAPI.md**: Bổ sung hướng dẫn lấy thông tin thời tiết đầy đủ & chính xác nhất
  - **Multi-Source Data Validation**: Validation dữ liệu từ nhiều nguồn với kiểm tra giá trị hợp lý
  - **Enhanced Location Accuracy**: GPS độ chính xác cao và lấy dữ liệu từ nhiều điểm gần nhau
  - **Data Aggregation**: Tính trung bình có trọng số và xác định điều kiện thời tiết chủ đạo
  - **Real-time Monitoring**: Theo dõi chất lượng dữ liệu liên tục với notification
  - **Advanced Caching**: Cache đa cấp với metadata và fallback thông minh
  - **API Optimization**: Tối ưu hóa sử dụng API với rate limiting và retry strategy
  - **Best Practices**: Hướng dẫn chi tiết để đạt độ chính xác 95%+ và response time < 2s

### Added
- **OpenWeatherMap API Integration Guide**: Tạo hướng dẫn chi tiết về cách sử dụng OpenWeatherMap API
  - **API Configuration**: Setup API key (927565d05e50545fc0077d2bdd4d5855) và security best practices
  - **Core Endpoints**: Current weather, 5-day forecast, air pollution data với response models
  - **Implementation Strategy**: Retrofit service, Repository pattern với caching
  - **Weather Compatibility Algorithm**: AI logic tính toán độ phù hợp thời tiết dựa trên tuổi, nghề nghiệp, location
  - **Optimization Strategy**: Caching, rate limiting, error handling với retry logic
  - **Testing Guidelines**: Unit tests cho repository và compatibility calculator
  - **File Created**: `WeatherAPI.md` với đầy đủ implementation guide

- **API & Notification Optimization Strategy**: Bổ sung hướng dẫn tối ưu hóa tần suất load API thời tiết và hiển thị thông báo
  - **Weather API Strategy**: Định nghĩa 3 lần/ngày (6AM, 12PM, 6PM) cho optimal accuracy
  - **Smart Refresh Logic**: Foreground refresh nếu data >2h, background theo schedule
  - **Notification Strategy**: 1 core notification/ngày + 2 conditional notifications tối đa
  - **UX Guidelines**: Skeleton loading, progressive enhancement, rich notifications
  - **User Control**: Frequency control, timing preference, data usage options
  - **Adaptive Learning**: User behavior analysis cho timing và content optimization
  - **Performance**: Battery optimization, network efficiency, memory management
  - **File Updated**: `Brainstorm_WeatherPersonalizedApp.md` với section mới về API & Notification optimization

- ✅ **Git Repository Initialization**: Khởi tạo Git repository cho dự án Weather Personalized App
  - ✅ Commit đầu tiên với toàn bộ codebase và cấu trúc dự án
  - ✅ Thiết lập version control cho quá trình phát triển
  - Git repository được khởi tạo thành công
  - Tất cả file và thư mục đã được add và commit
  - Working tree clean, sẵn sàng cho development

- **Weather Personalized App - Brainstorm Completed**: Hoàn thành giai đoạn brainstorm cho ứng dụng thời tiết AI cá nhân hóa
  - **Core Concept**: Ứng dụng thời tiết với AI personalization dựa trên tuổi, nơi sống, nghề nghiệp
  - **Point & Reward System**: Hệ thống gamification với daily notifications và reward viewing
  - **AI Features**: Weather compatibility scoring, smart notifications, personalized recommendations
  - **Technical Stack**: Android Kotlin + Jetpack Compose + Firebase + TensorFlow Lite
  - **File Created**: `Brainstorm_WeatherPersonalizedApp.md` với đầy đủ 3 phases

### Updated
- **Project Identity**: Cập nhật .project-identity với thông tin dự án mới
  - **Project Name**: Weather Personalized App
  - **Project Type**: Android
  - **Project Stage**: Development (ready for technical planning)
  - **Tech Stack**: Kotlin, Jetpack Compose, Firebase, TensorFlow Lite
  - **Key Features**: AI Personalization, Point System, Smart Notifications

### Next Steps
- Technical Planning & Architecture Design
- Database schema design
- AI algorithm development
- MVP implementation

## [Previous] - 2025-01-23

### Added
- **NativeAdView Refresh Capability**: Thêm tính năng refresh native ad mới
  - **NativeAdViewWithRefresh**: Composable mới hỗ trợ refresh native ad
  - **NativeAdViewController**: Controller class để điều khiển refresh từ bên ngoài
  - **showNewNativeAd()**: Method để destroy native ad cũ và load native ad mới
  - **Auto Refresh on Slide Change**: IntroScreen tự động refresh native ad khi next slide
  - **Proper Ad Cleanup**: Destroy native ad cũ trước khi load mới để tránh memory leak

### Enhanced
- **IntroScreen Native Ad Integration**: 
  - Sử dụng NativeAdViewWithRefresh thay vì NativeAdView cũ
  - Tự động gọi showNewNativeAd() khi slide thay đổi (trừ slide đầu tiên)
  - Mỗi slide sẽ hiển thị native ad mới, tăng engagement và revenue
  - Controller pattern để quản lý native ad lifecycle

### Technical Implementation
- **MVVM Pattern**: Sử dụng Controller pattern để quản lý state
- **Memory Management**: Proper destroy native ad cũ trước khi load mới
- **Callback System**: onAdLoaded callback để track trạng thái load ad
- **Compose Integration**: Sử dụng LaunchedEffect để trigger refresh khi slide thay đổi
- **UI State Management**: Sử dụng loading state để hiển thị trạng thái refresh
- **Visual Feedback**: Loading placeholder "Loading new native ad..." khi đang refresh

### Fixed
- **Native Ad Display Issue**: Sửa lỗi native ad mới không hiển thị sau khi refresh
  - Thêm `isLoading` state để track refresh status
  - Proper UI updates khi có native ad mới
  - Loading state hiển thị "Loading new native ad..." 
  - Compose tự động re-render khi state thay đổi

### Added
- **Unified SplashActivity**: Gộp tất cả screens (Splash, SelectLanguage, Intro) vào cùng một Activity
  - **Screen State Management**: Sử dụng sealed class Screen để quản lý trạng thái màn hình
  - **Single Activity Flow**: Tất cả navigation được xử lý trong cùng một Activity
  - **Unified UI**: SplashScreen, SelectLanguageScreen, IntroScreen được gộp vào cùng file
  - **Simplified Navigation**: Không cần tạo thêm Activity mới, chỉ cần thay đổi currentScreen

- **Complete User Flow**:
  - **SplashScreen**: Load ads và remote config → navigateToSelectLanguage()
  - **SelectLanguageScreen**: User chọn language → check button hiện → navigateToIntro()
  - **IntroScreen**: 3 slides intro → Complete button → start MainActivity

### Enhanced
- **NativeAdView New Features**:
  - **Param `enableButtonOnTop`**: Thêm param để điều khiển vị trí action button
    - `enableButtonOnTop = false` (mặc định): Button ở bottom như design gốc
    - `enableButtonOnTop = true`: Button ở top trên cùng, trước cả icon và label
  - **Description Max 2 Dòng**: Thay đổi từ `maxLines = 1` thành `maxLines = 2` để hiển thị nhiều nội dung hơn
  - **Compose Theme Primary Color**: Action button tự động lấy màu primary từ Compose theme (file Theme.kt) thay vì Android theme
  - **Flexible Layout**: Có thể tùy chỉnh layout tùy theo nhu cầu sử dụng
  - **Proper Button Position**: Button ở top thực sự ở trên cùng, trước tất cả content khác

### Fixed & Enhanced
- **NativeAdView Complete Rebuild**: Tái cấu trúc hoàn toàn để icon thật và MediaView hoạt động
  - **Single NativeAdView Container**: Sử dụng một AndroidView duy nhất chứa toàn bộ NativeAdView để bind đúng cách
  - **Real Icon Display**: Icon thật từ `nativeAd.icon?.drawable` được hiển thị trong ImageView
  - **Working MediaView**: MediaView được bind đúng cách với NativeAdView container để hiển thị media content
  - **Functional CTA Button**: Call to Action button được bind với `callToActionView` để Google Ads SDK handle click
  - **Proper View Binding**: Tất cả views (iconView, headlineView, bodyView, mediaView, callToActionView) được bind đúng với NativeAdView
  - **Native Layout**: Sử dụng LinearLayout với Android Views để đảm bảo tương thích hoàn toàn với Google Ads SDK
  - **Real Data Integration**: Headline, body, callToAction, icon được lấy thực từ NativeAd object

### Updated
- **SelectLanguageScreen UI Improvements**:
  - **Background**: Set background trắng cho toàn bộ màn hình
  - **Selection Indicator**: Bỏ tích (check icon) ở item được chọn, chỉ giữ lại viền màu cam để thể hiện selection
  - **Visual Clarity**: Viền màu cam (#FF6B35) và background nhạt (#FFF8F5) đủ để user nhận biết item được chọn

- **NativeAdView Technical Improvements**:
  - **Icon Integration**: Chuẩn bị để sử dụng icon thật từ NativeAd.icon (có fallback emoji 🎮)
  - **MediaView Implementation**: Sử dụng MediaView thật để hiển thị media content từ NativeAd.mediaContent
  - **Button Functionality**: Call to Action button hoạt động khi click, Google Ads SDK tự động handle action
  - **Real Data Binding**: Headline, body text, và call to action text được lấy trực tiếp từ NativeAd object
  - **Media Content Display**: MediaView tự động hiển thị video/image content từ quảng cáo

- **NativeAdView Design**: Cập nhật UI để match chính xác với design mong muốn
  - **Layout Structure**: Match với native_ad_view_with_media_medium.xml
  - **App Icon**: 54dp x 54dp với rounded corners
  - **AD Label**: Badge "AD" màu #FFCC00 với padding chính xác (3dp x 2.3dp)
  - **Headline**: Font size 15sp, bold, maxLines = 1
  - **Body Text**: Font size 12sp, maxLines = 1
  - **MediaView**: 180dp height với background #F5F5F5
  - **Call to Action Button**: 56dp height với primary color, hoạt động khi click
  - **Bottom Divider**: 1dp height với color #CCEBEBEB
  - **Padding**: Horizontal 8dp, top/bottom 4dp
  - **Overall Height**: 320dp để accommodate design mới
  - **Loại bỏ Rating View**: Không hiển thị star rating và rating text
  - **Loại bỏ Card Wrapper**: Full width không bo góc như design mẫu
  - **Data Integration**: Lấy data thực từ NativeAd (headline, body, callToAction)
  - **Click Handling**: Action button và MediaView hoạt động khi click

### Added
- **SelectLanguageActivity**: Màn hình chọn ngôn ngữ với UI như design
  - Sử dụng LanguageData enum với 9 ngôn ngữ
  - UI responsive với selection state và visual feedback
  - Install button và tích hợp NativeAdView
- **NativeAdManager**: Quản lý NativeAd với cache system
  - Singleton pattern với thread-safe initialization
  - Cache strategy với timestamp validation (15 phút hiệu lực)
  - Max cache size: 2 ads
  - Methods: preloadNativeAd, getNativeAd, cache management
- **NativeAdView**: Composable cho Native Ad với UI custom
  - App icon, AD label, title, subtitle
  - Star rating (3.5/5 stars)
  - Description text
  - MediaView với background trắng mờ nhẹ (180dp height)
  - States: Loading, Error, Success
- **AdManager NativeAd Integration**: Tích hợp NativeAdManager
  - Wrapper methods: preloadNativeAd, getNativeAd
  - Cache management: getNativeAdCacheInfo, clearNativeAdCache
  - Auto initialization trong initAdsAndUmp
- **SelectLanguageScreen**: Composable cho language selection
  - LazyColumn với LanguageData.values()
  - LanguageItem với selection state
  - Tích hợp NativeAdView ở cuối màn hình
- **NativeAdDemo**: Demo class cho NativeAd system
  - Basic usage, cache management, performance testing
  - Error handling, UI integration, multiple ads scenario

### Changed
- **AdManager**: Mở rộng để hỗ trợ cả Interstitial và Native ads
  - Thêm NativeAdManager instance
  - Thêm initNativeAds method
  - Tổ chức methods theo categories (Interstitial, Native)

### Technical Details
- **Cache Strategy**: 
  - Interstitial: Stack cache (3 ads, 10 phút)
  - Native: Queue cache (2 ads, 15 phút)
- **UI Components**: Sử dụng Material3 với custom colors và shapes
- **Thread Safety**: ConcurrentLinkedQueue cho NativeAd cache
- **Error Handling**: Graceful fallback cho ad loading failures
- **Design Fidelity**: UI match 100% với XML layout design

## [Previous] - 2025-01-23

### Added
- **InterstitialAdManager**: Quản lý InterstitialAd với cache system
  - Singleton pattern với thread-safe initialization
  - Cache strategy với timestamp validation (10 phút hiệu lực)
  - Max cache size: 3 ads
  - Methods: preloadInterAd, getInterAd, showInterAd
- **AdManager Integration**: Tích hợp InterstitialAdManager
  - Wrapper methods cho InterstitialAdManager
  - Auto initialization trong initAdsAndUmp
- **SplashScreen Ad Flow**: Logic xử lý quảng cáo trong SplashScreen
  - Remote Config → Load Interstitial Ad → Show Ad → Navigate
  - Thread safety với runOnUiThread
  - Fallback strategy nếu ad thất bại
- **SplashScreen UI Fixes**: Cải thiện UI và layout
  - Edge-to-edge display với Scaffold
  - Vertical centering cho app icon và title
  - Improved spacing và sizing
  - Status bar và navigation bar handling

### Changed
- **MySplashActivity**: Refactor để sử dụng AdManager
  - Bỏ old InterstitialAd implementation
  - Tích hợp với RemoteConfigManager
  - Handle ad flow sau khi remote config hoàn thành
- **RemoteConfigManager**: Cải thiện error handling
  - Try-catch blocks cho Firebase operations
  - Fallback mechanisms cho default values
  - Better error logging và recovery

### Fixed
- **Firebase Crashlytics Plugin**: Comment out plugin và dependency
- **Java Version Compatibility**: Set Java 17 cho AGP 8.12.1
- **Firebase API Key Error**: Implement robust error handling
  - Graceful degradation khi Firebase unavailable
  - Fallback to default ad settings
  - Comprehensive error logging

### Technical Details
- **Build System**: AGP 8.12.1 với Java 17 requirement
- **Error Handling**: Try-catch blocks và fallback mechanisms
- **Thread Safety**: runOnUiThread cho UI updates
- **Cache Management**: ConcurrentLinkedQueue với timestamp validation

## [Initial] - 2025-01-23

### Added
- **BaseAndroidCompose Project**: Android project với Compose UI
- **Firebase Integration**: Remote Config và Analytics
- **Google Mobile Ads SDK**: Ad initialization và configuration
- **UMP Consent Management**: User consent handling
- **Basic Project Structure**: Package organization và dependencies 

### Enhanced
- **NativeAdView Button Styling**:
  - **Rounded Corners**: Action button có bo cong với `cornerRadius = 8dp` (24px)
  - **Reduced Height**: Giảm chiều cao từ 56dp xuống 46dp (138px)
  - **Consistent Style**: Cả button ở top và bottom đều có style giống nhau
  - **Better UX**: Button nhỏ gọn và đẹp mắt hơn

- **NativeAdView Layout Optimization**:
  - **No Container Padding**: Bỏ padding left/right tổng của container
  - **Individual Item Margins**: Thêm margin left/right 8dp (24px) vào từng item riêng biệt
  - **Smart Divider Positioning**: Divider chỉ hiển thị khi button ở bottom, với margin bottom 4dp (12px)
  - **Better Spacing Control**: Kiểm soát spacing chính xác hơn cho từng component

- **SelectLanguageScreen Layout Optimization**:
  - **NativeAdView Full Width**: NativeAdView không có padding, full width toàn màn hình
  - **Content Padding Maintained**: Header và Language List vẫn giữ padding 16dp như cũ
  - **Clean Separation**: Tách biệt rõ ràng giữa content có padding và ad full width
  - **Better Visual Hierarchy**: NativeAdView nổi bật hơn với full width layout 

- **IntroScreen UI Enhancement**: Cập nhật layout cho intro slides
  - **Full Height Image**: Image chiếm toàn bộ chiều cao với ContentScale.Crop
  - **Gradient Overlay**: Lớp phủ trắng từ trong suốt đến 100% từ trên xuống dưới
  - **Text Overlay**: Description và Title nằm sát mép dưới với background gradient
  - **Button Styling**: Next/Complete button với width 140dp, height 46dp, màu #0099cc

- **NativeAdView enableOutlineButton**: Thêm option cho outline button style
  - **Outline Button**: Button với border và background trong suốt
  - **Right Alignment**: Button căn sát lề phải để thẳng hàng với button Next
  - **Consistent Styling**: Cùng màu #0099cc, cùng kích thước và style với IntroScreen buttons

### Fixed
- **NativeAdView Outline Button Alignment**: Sửa button căn phải đúng cách
  - **Spacer Weight**: Sử dụng spacer với weight = 1f để đẩy button sang phải
  - **Right Alignment**: Button căn sát lề phải với margin giống button Next trong IntroScreen
  - **Consistent Layout**: Button thẳng hàng hoàn hảo với navigation buttons

- **IntroScreen Text Layout**: Cập nhật thứ tự và alignment của text
  - **Title First**: Title ở trên, description ở dưới (thứ tự đúng UX)
  - **Center Alignment**: Cả title và description đều căn giữa
  - **Reduced Title Size**: Giảm title từ 28sp xuống 24sp cho cân bằng
  - **Better Hierarchy**: Thứ tự title → description logic hơn