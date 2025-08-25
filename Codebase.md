## Core Architecture

### Unified SplashActivity
- **Chức năng chính**: Quản lý toàn bộ flow từ splash đến intro
- **Screen Management**: Sử dụng sealed class Screen để quản lý trạng thái
- **Navigation Flow**: Splash → SelectLanguage → Intro → MainActivity
- **Ad Integration**: Interstitial ads trong splash, Native ads trong language và intro screens

### Screen States
- **SPLASH**: Màn hình khởi động với loading và ads
- **SELECT_LANGUAGE**: Màn hình chọn ngôn ngữ với conditional check button
- **INTRO**: Màn hình intro với 3 slides và navigation

### Key Features
- **Single Activity Pattern**: Tất cả screens được quản lý trong cùng một Activity
- **State Management**: Sử dụng private variables để track currentScreen và selectedLanguage
- **Ad Flow**: Interstitial ad trong splash, Native ad reload trong intro slides
- **Language Selection**: No auto-selection, user must choose manually 

### IntroScreen
- **Chức năng**: Màn hình intro với slide trượt
- **Features**:
  - **Full Height Layout**: Image chiếm toàn bộ chiều cao với ContentScale.Crop
  - **Gradient Overlay**: Lớp phủ trắng từ trong suốt đến 100% từ trên xuống dưới
  - **Text Overlay**: Description và Title nằm sát mép dưới với background gradient
  - **Navigation**: 3 slides với page indicator dots và Next/Complete buttons
  - **Button Styling**: Width 140dp, height 46dp, màu #0099cc, outline style
  - **NativeAdView**: Full width với enableOutlineButton = true

### NativeAdView
- **Chức năng**: Hiển thị native ads với custom UI
- **Features**:
  - **enableButtonOnTop**: Button ở top hoặc bottom
  - **enableOutlineButton**: Outline button style với border và background trong suốt
  - **Right Alignment**: Outline button căn sát lề phải để thẳng hàng với IntroScreen buttons
  - **Consistent Styling**: Cùng màu #0099cc, cùng kích thước và style
  - **Full Width Layout**: Không có container padding, individual item margins

### NativeAdViewWithRefresh
- **Chức năng**: NativeAdView với khả năng refresh native ad mới
- **Features**:
  - **Controller Pattern**: Trả về NativeAdViewController để điều khiển từ bên ngoài
  - **showNewNativeAd()**: Method để destroy native ad cũ và load native ad mới
  - **Memory Management**: Proper cleanup native ad cũ trước khi load mới
  - **Auto Refresh**: Tự động refresh khi được gọi từ controller
  - **Callback System**: onAdLoaded callback để track trạng thái

### NativeAdViewController
- **Chức năng**: Controller class để điều khiển NativeAdViewWithRefresh
- **Methods**:
  - **setRefreshCallback()**: Set callback function để refresh native ad
  - **showNewNativeAd()**: Trigger refresh native ad mới
- **Usage**: Được sử dụng trong IntroScreen để refresh native ad khi slide thay đổi 