# Changelog

## [Latest] - 2024-12-19

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