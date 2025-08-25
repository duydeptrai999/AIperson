# Development Standards & Code Quality Rules

> **📋 Detailed Development Standards**  
> Chi tiết các quy tắc phát triển, chất lượng code và best practices được tách ra từ project_rules.md

## Android Development Workflow

### Blueprint-First Development

**Phase 1: Requirements Analysis**

- Phân tích yêu cầu chi tiết từ stakeholders
- Xác định target users và use cases
- Định nghĩa functional và non-functional requirements
- Tạo user stories và acceptance criteria

**Phase 2: Technical Design**

- Thiết kế architecture tổng thể (MVVM, Clean Architecture)
- Định nghĩa data models và database schema
- Thiết kế API contracts và endpoints
- Lập kế hoạch module structure và dependencies

**Phase 3: UI/UX Blueprint**

- Tạo wireframes và mockups chi tiết
- Định nghĩa design system và component library
- Xác định navigation flow và user journey
- Thiết kế responsive layouts cho different screen sizes

**Phase 4: Implementation Planning**

- Chia nhỏ tasks theo priority và dependencies
- Estimate effort và timeline cho từng task
- Xác định testing strategy và quality gates
- Setup development environment và CI/CD pipeline

### Standard Package Structure

```
app/
├── src/main/java/com/company/app/
│   ├── data/
│   │   ├── local/          # Room database, SharedPreferences
│   │   ├── remote/         # API services, DTOs
│   │   └── repository/     # Repository implementations
│   ├── domain/
│   │   ├── model/          # Domain models
│   │   ├── repository/     # Repository interfaces
│   │   └── usecase/        # Business logic use cases
│   ├── presentation/
│   │   ├── ui/
│   │   │   ├── activity/   # Activities
│   │   │   ├── fragment/   # Fragments
│   │   │   ├── adapter/    # RecyclerView adapters
│   │   │   └── custom/     # Custom views
│   │   └── viewmodel/      # ViewModels
│   ├── di/                 # Dependency injection modules
│   └── util/               # Utility classes
└── src/main/res/
    ├── layout/             # XML layouts
    ├── values/             # Strings, colors, dimensions
    ├── drawable/           # Images, vector drawables
    └── menu/               # Menu resources
```

## Code Quality Standards

### Architecture

**MVVM Pattern**

- Model: Data layer với Repository pattern
- View: Activities, Fragments, Custom Views
- ViewModel: Business logic và state management
- Sử dụng LiveData/StateFlow cho data binding
- Implement proper separation of concerns

**Clean Architecture Principles**

- Domain layer độc lập với framework
- Data layer implement domain interfaces
- Presentation layer chỉ phụ thuộc vào domain
- Dependency inversion với Dagger/Hilt

**Design Patterns**

- Repository pattern cho data access
- Observer pattern cho reactive programming
- Factory pattern cho object creation
- Singleton pattern cho shared resources (cẩn thận với memory leaks)

### Security

**Data Protection**

- Mã hoá dữ liệu nhạy cảm (passwords, tokens, PII)
- Sử dụng Android Keystore cho sensitive data
- Implement certificate pinning cho network security
- Validate tất cả user inputs để tránh injection attacks

**Authentication & Authorization**

- Implement đúng cách OAuth 2.0/JWT authentication
- Sử dụng secure storage cho tokens
- Implement proper session management
- Handle token refresh và expiration

**Network Security**

- Sử dụng HTTPS cho mọi API endpoints
- Implement certificate pinning
- Validate SSL certificates
- Sử dụng network security config

**OWASP Compliance**

- Tuân thủ hướng dẫn OWASP Mobile Top 10
- Regular security audits và penetration testing
- Code obfuscation cho production builds
- Implement anti-tampering measures

### Performance

**Memory Management**

- Tránh memory leaks với proper lifecycle management
- Sử dụng weak references khi cần thiết
- Implement proper bitmap handling và caching
- Monitor memory usage với profiling tools

**Database Optimization**

- Tối ưu database queries để tránh N+1 problems
- Sử dụng indexes cho frequently queried columns
- Implement pagination cho large data sets
- Use background threads cho database operations

**Network Optimization**

- Implement caching cho API responses
- Sử dụng compression cho large payloads
- Implement retry mechanisms với exponential backoff
- Handle offline scenarios với local caching

**UI Performance**

- Lazy load components và data khi có thể
- Optimize RecyclerView với ViewHolder pattern
- Sử dụng ConstraintLayout để giảm view hierarchy depth
- Implement proper image loading với libraries như Glide/Picasso

### Error Handling

**Exception Management**

- Xử lý tất cả exceptions và errors gracefully
- Implement global exception handler
- Provide user-friendly error messages
- Log errors với sufficient context để debug

**Network Error Handling**

- Handle network timeouts và connection errors
- Implement retry mechanisms cho unstable operations
- Sử dụng circuit breakers cho external services
- Provide offline fallback options

**User Experience**

- Show appropriate loading states
- Provide clear error messages và recovery options
- Implement proper validation với real-time feedback
- Handle edge cases và unexpected scenarios

### Testing

**Unit Testing**

- Viết unit tests với high test coverage (>80%)
- Test business logic trong ViewModels và UseCases
- Mock dependencies với Mockito/MockK
- Follow AAA pattern (Arrange, Act, Assert)

**Integration Testing**

- Test database operations với Room testing
- Test API integrations với MockWebServer
- Test Repository implementations
- Validate data transformations

**UI Testing**

- Implement UI tests với Espresso
- Test critical user flows và navigation
- Validate accessibility features
- Test different screen sizes và orientations

**Testing Strategy**

- Ưu tiên testing pyramid (nhiều unit tests, ít UI tests)
- Implement continuous testing trong CI/CD pipeline
- Regular regression testing
- Performance testing cho critical paths

## File Protection & Backup Rules

### Basic Principles

- **BẮT BUỘC** tạo backup trước khi xóa bất kỳ file hoặc thư mục nào
- **BẮT BUỘC** di chuyển file vào thư mục backup thay vì xóa trực tiếp
- **BẮT BUỘC** giữ cấu trúc thư mục khi backup để dễ dàng phục hồi sau này
- **BẮT BUỘC** ghi log mỗi khi di chuyển file vào backup
- **KHUYẾN NGHỊ** kiểm tra file trước khi xóa để đảm bảo không ảnh hưởng đến chức năng hiện có

### Backup Directory Structure

- Tạo thư mục `_backups` trong root của dự án (đã thêm vào .gitignore)
- Bên trong tạo cấu trúc theo dạng ngày: `_backups/YYYY-MM-DD/`
- Trong mỗi thư mục ngày, duy trì cấu trúc thư mục gốc để dễ dàng phục hồi
- Ví dụ: `src/components/Button.js` → `_backups/2024-05-10/src/components/Button.js`

### Backup Process

1. Xác định thời gian hiện tại để tạo thư mục backup nếu chưa tồn tại
2. Tạo thư mục cần thiết trong backup để giữ nguyên cấu trúc
3. Di chuyển file vào thư mục backup thay vì xóa trực tiếp
4. Cập nhật file log với thông tin: thời gian, đường dẫn gốc, lý do xóa
5. Thông báo cho người dùng về vị trí lưu backup

## Mockup Data Management

### Requirements

- Nếu dự án sử dụng bất kỳ dữ liệu giả lập nào, **BẮT BUỘC** tạo file MockupData.md
- Liệt kê chi tiết và cập nhật thường xuyên tất cả các phần của dự án đang sử dụng dữ liệu giả
- Phân loại dữ liệu giả lập theo mục đích sử dụng:
  - Dữ liệu demo cho client/stakeholders
  - Dữ liệu testing cho quá trình phát triển
  - Dữ liệu thay thế tạm thời cho API chưa sẵn sàng
  - Dữ liệu mẫu cho hướng dẫn/documentation

### Documentation Format

- Cho mỗi phần dữ liệu giả lập, ghi rõ:
  - Vị trí chính xác của file/code đang sử dụng dữ liệu giả
  - Cấu trúc dữ liệu của mockup và cấu trúc dữ liệu thật tương ứng
  - Phương thức khởi tạo và sử dụng dữ liệu giả
  - Kế hoạch và timeline để chuyển sang dữ liệu thật
  - Người chịu trách nhiệm cho việc thay thế dữ liệu giả

## Git Workflow

### Commit Convention

- Tuân thủ quy ước commit (feat, fix, docs, style, refactor...)
- Sử dụng tiếng Anh cho commit messages
- Format: `type(scope): description`
- Examples:
  - `feat(camera): add new filter effects`
  - `fix(ui): resolve layout issue in preview`
  - `docs(readme): update installation guide`

### Branch Management

- Sử dụng feature branches cho mỗi tính năng mới
- Merge vào main branch sau khi review
- Xóa feature branches sau khi merge thành công

## Internationalization (i18n)

### String Resources

- Luôn kiểm tra và thêm các chuỗi dịch hoặc resource khi tạo
- Sử dụng tiếng Anh làm ngôn ngữ mặc định
- Hỗ trợ đa ngôn ngữ với tiếng Anh làm fallback
- Tạo string keys có ý nghĩa và dễ hiểu

### Resource Management

- Tổ chức strings theo feature hoặc screen
- Sử dụng plurals cho các string có số lượng
- Implement proper formatting cho dates, numbers, currencies

## Common Kiro System Commands

### Project Analysis

```bash
# Kiểm tra trạng thái Kiro system
kiro status

# Phân tích định dạng task
kiro analyze tasks
```

### Task Management

```bash
# Kiểm tra phụ thuộc task
kiro check dependencies

# Theo dõi tiến độ
kiro track progress

# Xác thực tiêu chí chấp nhận
kiro validate acceptance-criteria
```

## Getting Started with Trae AI + Kiro

1. **BẮT BUỘC**: Kiểm tra Kiro system khi bắt đầu dự án
2. **BẮT BUỘC**: Sử dụng Kiro Dynamic Workflow cho tạo/cập nhật artifacts
3. **BẮT BUỘC**: Xác định operation mode phù hợp (CREATE_NEW/UPDATE_EXISTING/SUPPLEMENT_DATA/RESTRUCTURE)
4. Theo dõi task execution thông qua Trae AI interface
5. Sử dụng quality gates để đảm bảo chất lượng
6. Tận dụng auto-completion và dependency resolution
7. **KHUYẾN NGHỊ**: Sử dụng Dynamic Workflow cho iterative development và changing requirements
