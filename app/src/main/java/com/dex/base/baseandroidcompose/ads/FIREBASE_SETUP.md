# Firebase Setup Guide - Giải quyết lỗi API Key

## 🚨 **Vấn đề hiện tại:**
App đang gặp lỗi **"Please set a valid API key"** do file `google-services.json` không hợp lệ.

## 🔧 **Cách sửa lỗi:**

### **Cách 1: Sử dụng Firebase Console (Khuyến nghị)**

1. **Truy cập Firebase Console:**
   - Vào [https://console.firebase.google.com/](https://console.firebase.google.com/)
   - Đăng nhập bằng Google account

2. **Tạo project mới hoặc chọn project có sẵn:**
   - Click "Create a project" hoặc chọn project từ danh sách
   - Đặt tên project (ví dụ: "BaseAndroidCompose")

3. **Thêm Android app:**
   - Click "Add app" → "Android"
   - Package name: `com.dex.base.baseandroidcompose`
   - App nickname: "BaseAndroidCompose"
   - Click "Register app"

4. **Download google-services.json:**
   - Click "Download google-services.json"
   - Copy file này vào thư mục `app/` của project

5. **Enable các service cần thiết:**
   - **Analytics**: Bật trong Firebase Console
   - **Remote Config**: Bật trong Firebase Console
   - **Crashlytics**: Bật nếu cần (tùy chọn)

### **Cách 2: Tạm thời bỏ Firebase (Để test app)**

Nếu bạn chỉ muốn test app mà không cần Firebase:

1. **Comment out Firebase dependencies trong `app/build.gradle.kts`:**
   ```kotlin
   // Firebase
   //implementation(platform(libs.firebase.bom))
   //implementation(libs.firebase.analytics)
   //implementation(libs.firebase.config)
   //implementation(libs.firebase.auth)
   ```

2. **Comment out Firebase plugin:**
   ```kotlin
   //id("com.google.gms.google-services")
   ```

3. **Sửa `MySplashActivity.kt`:**
   ```kotlin
   // Comment out Firebase initialization
   // FirebaseApp.initializeApp(this)
   // firebaseAnalytics = Firebase.analytics
   ```

## 📱 **Test app sau khi sửa:**

1. **Build app:**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Install và chạy app:**
   - App sẽ không crash nữa
   - InterstitialAdManager sẽ hoạt động với ad IDs mặc định
   - Remote config sẽ sử dụng giá trị mặc định

## 🔑 **Cấu hình Remote Config (Sau khi có Firebase hợp lệ):**

1. **Vào Firebase Console → Remote Config**
2. **Tạo các parameter:**
   ```
   ADMOB_APP_ID: ca-app-pub-4183647288183037~2782349072
   ADMOB_BANNER_AD_ID: ca-app-pub-9821898502051437/1958618303
   ADMOB_INTERSTITIAL_AD_ID: ca-app-pub-9821898502051437/9645536639
   ADMOB_REWARDED_AD_ID: ca-app-pub-9821898502051437/8635691382
   ADMOB_APP_OPEN_AD_ID: ca-app-pub-9821898502051437/8332454968
   ADMOB_NATIVE_VIDEO_AD_ID: ca-app-pub-9821898502051437/7710456674
   ADMOB_NATIVE_AD_ID: ca-app-pub-9821898502051437/7710456674
   DELAY_TIME_RELOAD_AD: 2000
   RETRY_TIME_RELOAD_AD: 1
   ENABLE_OPEN_AD: true
   ```

3. **Publish changes**

## 🚀 **Kết quả mong đợi:**

- ✅ App không crash khi khởi động
- ✅ InterstitialAdManager hoạt động bình thường
- ✅ Quảng cáo load và show được
- ✅ Remote config hoạt động (nếu có Firebase hợp lệ)

## 📞 **Hỗ trợ:**

Nếu vẫn gặp vấn đề, hãy:
1. Kiểm tra log để xem lỗi cụ thể
2. Đảm bảo `google-services.json` đúng format
3. Kiểm tra Firebase project có được enable đúng service không 