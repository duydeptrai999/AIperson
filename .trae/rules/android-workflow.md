---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.134Z"
---

#android-workflow
---
description: Quy trình phát triển Android theo blueprint không dùng script
globs: *.kt,*.java,*.xml
alwaysApply: false
---

## Quy Trình Blueprint-First
- ***BẮT BUỘC*** tạo blueprint trước khi viết code cho mỗi tính năng
- ***BẮT BUỘC*** kiểm tra module registry để tránh trùng lặp
- ***BẮT BUỘC*** cập nhật module registry sau khi hoàn thành tính năng
- ***BẮT BUỘC*** tuân thủ cấu trúc package tiêu chuẩn
- ***BẮT BUỘC*** sử dụng các base classes đã có
- ***NGHIÊM CẤM*** tạo code trùng lặp chức năng đã có

## Cấu Trúc Package Chuẩn
```
com.base.app/
├── base/                 # Base classes
│   ├── activity/         # Base Activities
│   ├── fragment/         # Base Fragments
│   ├── viewmodel/        # Base ViewModels
│   ├── adapter/          # Base Adapters
│   └── view/             # Base Custom Views
├── core/                 # Core modules
│   ├── di/               # Dependency Injection
│   ├── network/          # Network components
│   ├── storage/          # Local storage
│   ├── analytics/        # Analytics tracking
│   └── utils/            # Utility classes
├── data/                 # Data layer
│   ├── repository/       # Repositories implementation
│   ├── datasource/       # Data sources (remote, local)
│   ├── model/            # Data models (entities, DTOs)
│   └── mapper/           # Mappers
├── domain/               # Domain layer
│   ├── usecase/          # Use cases (business logic)
│   ├── model/            # Domain models
│   └── repository/       # Repository interfaces
└── ui/                   # UI layer
    ├── components/       # Shared UI components
    └── features/         # Feature packages
        ├── feature1/     # Tính năng 1
        ├── feature2/     # Tính năng 2
        └── ...
```

## Feature Blueprint Template
```markdown
# FEATURE BLUEPRINT: [Tên Tính Năng]

## MÔ TẢ
[Mô tả chi tiết về tính năng]

## DOMAIN LAYER
- Models:
  - [Model 1]: [Mô tả]
  - [Model 2]: [Mô tả]
- Use Cases:
  - [UseCase 1]: [Input/Output, mô tả]
  - [UseCase 2]: [Input/Output, mô tả]

## DATA LAYER
- Repository: [Tên repository]
- API Endpoints (nếu có):
  - [Endpoint 1]: [Method, URL pattern, params]
- Local Storage (nếu có):
  - [Entity/Table 1]: [Mô tả]

## UI LAYER
- ViewModels:
  - [ViewModel 1]: [States, Events]
- Screens:
  - [Screen 1]: [Layout, components]
- Components:
  - [Component 1]: [Mục đích, tái sử dụng]

## RESOURCES
- Strings: [Danh sách string keys]
- Colors: [Màu sắc cần thêm]
- Drawables: [Resources cần thiết]
```

## Quy Trình Làm Việc

### Bước 1: Tạo Feature Blueprint
1. Tạo file blueprint trong thư mục `blueprint/features/`
2. Đặt tên file: `[feature-name]-blueprint.md`
3. Sử dụng Feature Blueprint Template ở trên
4. Điền đầy đủ thông tin vào các section

### Bước 2: Tạo Cấu Trúc Package
1. Tạo package theo feature name trong `com.base.app.ui.features`
2. Tạo các sub-packages:
   - `ui/features/[feature]/presentation` (fragments, adapters)
   - `ui/features/[feature]/viewmodel`
   - `domain/usecase/[feature]` (nếu cần)
   - `data/repository/[feature]` (nếu cần)

### Bước 3: Triển Khai Theo Layer
1. Triển khai Domain Layer:
   - Tạo domain models
   - Tạo repository interfaces
   - Tạo use cases

2. Triển khai Data Layer:
   - Tạo data models
   - Tạo data sources (remote, local)
   - Tạo repository implementation

3. Triển khai UI Layer:
   - Tạo ViewModels
   - Tạo UI components
   - Tạo Screens/Fragments

### Bước 4: Cập Nhật Registry
1. Cập nhật `blueprint/module-registry.md` với các components mới
2. Cập nhật `blueprint/component-catalog.md` với UI components mới

## Quy Tắc Chống Trùng Lặp Code
- ***BẮT BUỘC*** trước khi tạo class/function/resource mới:
  1. Tìm kiếm trong project với pattern: `class/interface/fun [Tên]`
  2. Kiểm tra module-registry.md
  3. Kiểm tra component-catalog.md
  4. Tìm trong package `core/utils` trước khi tạo utility functions
  5. Tìm trong package `ui/components` trước khi tạo UI components mới
  6. Ưu tiên mở rộng code hiện có thay vì tạo mới

- ***BẮT BUỘC*** khi gặp logic tương tự ở 2+ nơi:
  1. Trích xuất vào base class/util class
  2. Tạo extension function cho code dùng chung
  3. Cập nhật tất cả occurrences để sử dụng code dùng chung

- ***KHUYẾN NGHỊ*** khi thấy code tương tự nhưng khác biệt nhỏ:
  1. Tìm mẫu chung và tham số hóa sự khác biệt
  2. Tạo một version tổng quát có thể tùy chỉnh
  3. Tái cấu trúc code hiện tại để dùng version tổng quát

## Luồng Tạo Dự Án Android Tự Động

### Bước 1: Định Nghĩa Kiến Trúc Cốt Lõi
- ***BẮT BUỘC*** sử dụng **MVVM + Clean Architecture** với phân tách 3 lớp:
  * **UI Layer**: Composables + ViewModels
  * **Domain Layer**: Use Cases + Models + Repository Interfaces
  * **Data Layer**: Repository Implementations + Data Sources
- ***BẮT BUỘC*** đảm bảo **Unidirectional Data Flow**: UI → ViewModel → UseCase → Repository

#### Tech Stack Chuẩn:
```kotlin
// UI Framework
implementation "androidx.compose.ui:compose-ui:$compose_version"
implementation "androidx.compose.material3:material3:$material3_version"

// Dependency Injection - Chọn 1 trong 2:
implementation "com.google.dagger:hilt-android:$hilt_version" // Dự án lớn, kiểm soát chặt
// HOẶC
implementation "io.insert-koin:koin-android:$koin_version" // Đơn giản, APK nhỏ hơn

// Async Programming
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"

// Networking
implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
implementation "com.squareup.okhttp3:okhttp:$okhttp_version"

// Serialization
implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version"

// Database
implementation "androidx.room:room-runtime:$room_version"
implementation "androidx.room:room-ktx:$room_version"

// Image Loading
implementation "io.coil-kt:coil-compose:$coil_version"

// Logging
implementation "com.jakewharton.timber:timber:$timber_version"

// Permissions (Optional)
implementation "com.karumi:dexter:$dexter_version"
```

### Bước 2: Thiết Lập Build Configuration & Tối Ưu

#### Gradle Configuration (build.gradle :app)
```kotlin
android {
    buildTypes {
        release {
            minifyEnabled = true
            shrinkResources = true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    defaultConfig {
        // ABI Filtering - chỉ hỗ trợ ARM
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a'
        }
    }
    
    // Enable Android App Bundle
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}
```

#### Tối Ưu Resource:
- ***BẮT BUỘC*** sử dụng **Vector Drawables** cho icons
- ***BẮT BUỘC*** sử dụng **WebP** format cho images
- ***BẮT BUỘC*** auto-optimize images khi import vào project

### Bước 3: Quy Tắc Sinh Code Cho AI

#### Component Reusability:
```kotlin
// Base Composables
@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: ButtonType = ButtonType.PRIMARY
) { /* Implementation */ }

// Base ViewModel
abstract class BaseViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    protected fun handleError(error: Throwable) {
        Timber.e(error, "Error in ${this::class.simpleName}")
        _uiState.value = UiState.Error(error.message ?: "Unknown error")
    }
}

// Base UseCase
abstract class BaseUseCase<in P, R> {
    suspend operator fun invoke(parameters: P): Result<R> = runCatching {
        execute(parameters)
    }
    
    protected abstract suspend fun execute(parameters: P): R
}
```

#### Error Handling Pattern:
```kotlin
// Repository Pattern with Error Handling
class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) : IUserRepository {
    
    override suspend fun getUser(id: String): Result<User> = runCatching {
        try {
            val remoteUser = remoteDataSource.getUser(id)
            localDataSource.saveUser(remoteUser)
            remoteUser
        } catch (e: Exception) {
            Timber.w(e, "Failed to fetch from remote, trying local")
            localDataSource.getUser(id) ?: throw e
        }
    }
}
```

#### State Management Pattern:
```kotlin
class FeatureViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel() {
    
    private val _userState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userState = _userState.asStateFlow()
    
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _userState.value = UserUiState.Loading
            getUserUseCase(userId)
                .onSuccess { user ->
                    _userState.value = UserUiState.Success(user)
                }
                .onFailure { error ->
                    handleError(error)
                    _userState.value = UserUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
}
```

### Bước 4: Testing Setup

#### Unit Test Template:
```kotlin
@ExperimentalCoroutinesTest
class FeatureViewModelTest {
    
    @Mock private lateinit var getUserUseCase: GetUserUseCase
    
    private lateinit var viewModel: FeatureViewModel
    private val testDispatcher = TestCoroutineDispatcher()
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = FeatureViewModel(getUserUseCase)
    }
    
    @Test
    fun `loadUser success updates state correctly`() = runTest {
        // Given
        val expectedUser = User("1", "John Doe")
        coEvery { getUserUseCase("1") } returns Result.success(expectedUser)
        
        // When
        viewModel.loadUser("1")
        
        // Then
        assertEquals(UserUiState.Success(expectedUser), viewModel.userState.value)
    }
}
```

#### UI Test Template:
```kotlin
@ExperimentalComposeUiApi
class FeatureScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun userScreen_displaysUserInfo_whenStateIsSuccess() {
        // Given
        val user = User("1", "John Doe")
        
        // When
        composeTestRule.setContent {
            UserScreen(
                uiState = UserUiState.Success(user),
                onAction = {}
            )
        }
        
        // Then
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }
}
```

### Bước 5: Performance Monitoring Setup

#### Firebase Integration:
```kotlin
// Application Class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        
        // Performance Monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
    }
}
```

#### Performance Traces:
```kotlin
// Repository with Performance Monitoring
class NetworkRepository {
    suspend fun fetchData(): Result<Data> {
        val trace = FirebasePerformance.getInstance().newTrace("fetch_data")
        trace.start()
        
        return try {
            val result = apiService.getData()
            trace.putAttribute("success", "true")
            Result.success(result)
        } catch (e: Exception) {
            trace.putAttribute("success", "false")
            trace.putAttribute("error", e.message ?: "unknown")
            Result.failure(e)
        } finally {
            trace.stop()
        }
    }
}
```

## Quy Tắc Tối Ưu APK Size

### Resource Optimization:
- ***BẮT BUỘC*** compress images trước khi add vào project
- ***BắT BUỘC*** sử dụng vector drawables thay vì multiple density images
- ***BẮT BUỘC*** remove unused resources với `shrinkResources = true`

### Code Optimization:
- ***BẮT BUỘC*** sử dụng R8/ProGuard để minify code
- ***BẮT BUỘC*** avoid reflection khi có thể
- ***BẮT BUỘC*** sử dụng `@Keep` annotation cho classes cần thiết

### Dependency Management:
- ***BẮT BUỘC*** regularly audit dependencies và remove unused ones
- ***KHUYẾN NGHỊ*** prefer lightweight alternatives khi có thể
- ***KHUYẾN NGHỊ*** sử dụng `implementation` thay vì `api` trong Gradle

## 🔴 CRITICAL: Error Prevention Integration

### **BẮT BUỘC** - Apply Error #prevention-rules
- **BẮT BUỘC** tuân thủ 100% Android Error Prevention Rules
- **BẮT BUỘC** sử dụng AI Android Quality Enforcer cho mọi code generation
- **BẮT BUỘC** áp dụng Real-time Error Detector trong quá trình development

### **Error Prevention Checklist (BẮT BUỘC 100%)**
- [ ] **Type Safety**: Không có type inference errors
- [ ] **Import Resolution**: Tất cả imports được resolve
- [ ] **Null Safety**: Sử dụng safe calls cho nullable types
- [ ] **Method Signatures**: Parameters và return types khớp
- [ ] **When Expressions**: Exhaustive với else branches
- [ ] **Resource Validation**: Tất cả R.* references tồn tại
- [ ] **Error Handling**: Try-catch cho risky operations
- [ ] **ViewBinding Cleanup**: Proper null assignment trong onDestroyView
- [ ] **Lifecycle Awareness**: Coroutines sử dụng appropriate scopes

## Danh Sách Kiểm Tra Chất Lượng
- [ ] **Error Prevention Compliance**: 100% tuân thủ error prevention rules
- [ ] MVVM + Clean Architecture được implement đúng
- [ ] Unidirectional Data Flow được đảm bảo
- [ ] R8/ProGuard configuration hoạt động
- [ ] Vector Drawables được sử dụng cho icons
- [ ] WebP format được sử dụng cho images
- [ ] Unit tests cover ViewModels và UseCases
- [ ] UI tests cover critical user flows
- [ ] Error handling được implement đầy đủ
- [ ] Performance monitoring được setup
- [ ] APK size được tối ưu (dưới ngưỡng target)
- [ ] Code tuân thủ cấu trúc package định nghĩa
- [ ] Classes được đặt đúng package
- [ ] Không có code trùng lặp chức năng
- [ ] Sử dụng các base classes khi thích hợp
- [ ] UI components được tái sử dụng khi có thể
- [ ] Blueprint được hoàn thiện trước khi viết code
- [ ] Module Registry được cập nhật sau khi hoàn thành
- [ ] **AI Quality Enforcer**: Passed all validation checks
- [ ] **Real-time Error Detection**: No critical issues detected