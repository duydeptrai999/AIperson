---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.174Z"
---

#context7-auto-workflow
---
title: Context7 Auto-Check Workflow
description: Tự động kiểm tra và áp dụng context từ Context7 MCP server
version: 2.0
status: active
globs: "*.*"
alwaysApply: true
---

# Context7 Auto-Check Workflow

## Mục đích
Tự động kiểm tra và áp dụng context liên quan đến dự án từ Context7 MCP server để cung cấp thông tin và đề xuất phù hợp với tech stack hiện tại.

## Điều kiện kích hoạt

### Môi trường hỗ trợ
- **Hoạt động trên tất cả các IDE hỗ trợ MCP**
- Ưu tiên sử dụng Context7 MCP khi có sẵn
- Fallback về ngữ cảnh đã lưu trong bộ nhớ cache hoặc các mẫu cơ bản khi MCP không khả dụng
- Ghi log khi không thể kết nối Context7 MCP

### Kích hoạt tự động
- **BẮT BUỘC**: Khi bắt đầu bất kỳ task nào (theo project-identity-enforcement)
- Khi bắt đầu phiên làm việc mới
- Khi phát hiện thay đổi trong tech stack
- Khi người dùng yêu cầu đề xuất hoặc giải pháp
- Khi gặp vấn đề cần tham khảo best practices
- **MỚI**: Trong quá trình brainstorm để tìm kiếm giải pháp tương tự
- **MỚI**: Trong quá trình planning để kiểm tra compatibility và best practices

## Nguyên Tắc Cơ Bản
- ***BẮT BUỘC*** tự động kiểm tra context dự án trước khi thực hiện bất kỳ task nào (chỉ trên Trae AI)
- ***BẮT BUỘC*** sử dụng Context7 MCP để lấy thông tin cập nhật về libraries/frameworks
- ***BẮT BUỘC*** cập nhật knowledge base khi phát hiện thay đổi trong tech stack
- ***BẮT BUỘC*** tích hợp vào tất cả workflows hiện có
- ***KHUYẾN NGHỊ*** cache context information để tránh gọi API quá nhiều
- ***NGHIÊM CẤM*** bỏ qua bước kiểm tra context khi người dùng không yêu cầu cụ thể

## Quy trình thực hiện

### Bước 1: Phân tích dự án (Enhanced)
- Đọc file `.project-identity` để xác định tech stack
- Phân tích cấu trúc thư mục và dependencies
- Xác định loại dự án (mobile, web, backend, etc.)
- **MỚI**: Kiểm tra giai đoạn dự án (brainstorm, planning, development, testing)
- **MỚI**: Phân tích context từ các file brainstorm và planning hiện có

### Bước 2: Truy vấn Context7 (Improved Error Handling)
- **TRY**: Gọi `resolve-library-id` cho các thư viện chính
- **TRY**: Gọi `get-library-docs` để lấy documentation
- **TRY**: Tìm kiếm best practices và patterns phù hợp
- **CATCH**: Log lỗi và fallback về local knowledge base
- **FINALLY**: Đảm bảo workflow tiếp tục dù Context7 fail

### Bước 3: Phân tích kết quả (Context-Aware)
- So sánh với cấu hình hiện tại
- Xác định gaps và opportunities
- Đánh giá compatibility và conflicts
- **MỚI**: Phân tích theo giai đoạn dự án hiện tại
- **MỚI**: Tích hợp với memory system để lưu insights

### Bước 4: Đề xuất (Stage-Specific)
- Cung cấp recommendations dựa trên context và giai đoạn dự án
- Đề xuất improvements và optimizations phù hợp
- Cảnh báo về potential issues
- **MỚI**: Đề xuất specific cho brainstorm phase
- **MỚI**: Đề xuất architecture patterns cho planning phase
- **MỚI**: Đề xuất implementation best practices cho development phase

### 2. Trae AI Environment Detection
```
Cách kiểm tra Trae AI environment:
- Kiểm tra có MCP Context7 server available không
- Verify Trae AI specific environment variables
- Test Context7 MCP connection trước khi sử dụng
- Fallback gracefully nếu không phải Trae AI
```

**Xử lý khi KHÔNG phải Trae AI:**
- Skip tất cả Context7 MCP calls
- Sử dụng cached context nếu có
- Thông báo user về limitation
- Continue với basic workflow không có Context7 enhancement

### 3. Context #validation-rules (Chỉ trên Trae AI)
- Kiểm tra compatibility giữa các libraries
- Xác nhận version constraints và dependencies
- Phát hiện deprecated patterns hoặc security issues
- Đề xuất updates nếu có version mới stable

### 4. Auto-Context Libraries List (Chỉ trên Trae AI)
```
Các libraries cần tự động kiểm tra context qua Context7 MCP:
- React/Vue/Angular (frontend frameworks)
- Node.js/Express/Fastify (backend frameworks) 
- Flutter/React Native (mobile frameworks)
- Database libraries (MongoDB, PostgreSQL, etc.)
- Authentication libraries (Auth0, Firebase Auth, etc.)
- State management (Redux, Zustand, Riverpod, etc.)
- Testing frameworks (Jest, Cypress, etc.)
- Build tools (Webpack, Vite, etc.)

Lưu ý: Trên các IDE khác, sử dụng cached context hoặc basic patterns
```

### 5. Context Memory Integration
```
Tích hợp với memory-bank-workflow:
- Lưu context check results vào memory_bank/context_session.md
- Track các thay đổi context trong session (bao gồm IDE environment info)
- Maintain context history cho debugging
- Auto-expire cached context sau 24h
- Đánh dấu context source (Trae AI Context7 vs cached/basic)
```

## Implementation Workflow

### Phase 1: Context Discovery
1. **Project Analysis**
   ```
   - Scan project files để identify tech stack
   - Parse package managers files
   - Detect framework patterns trong code
   - Identify custom libraries và internal modules
   ```

2. **Context7 Integration (Chỉ trên Trae AI)**
   ```
   - Kiểm tra Trae AI environment trước khi proceed
   - Resolve library IDs cho detected libraries qua Context7 MCP
   - Fetch latest documentation và best practices
   - Cache results trong session memory với IDE source tag
   - Set up auto-refresh triggers (chỉ khi có Context7 access)
   - Fallback to cached/basic context nếu không phải Trae AI
   ```

### Phase 2: Context Validation
1. **Compatibility Check**
   ```
   - Verify version compatibility matrix
   - Check for known conflicts
   - Validate dependency tree
   - Identify potential upgrade paths
   ```

2. **Best Practices Sync**
   ```
   - Compare current patterns với latest docs
   - Identify deprecated usage
   - Suggest modern alternatives
   - Update coding standards
   ```

### Phase 3: Context Application
1. **Workflow Enhancement**
   ```
   - Apply context-aware suggestions
   - Use library-specific patterns
   - Follow framework conventions
   - Implement security best practices
   ```

2. **Documentation Update**
   ```
   - Update Codebase.md với new context
   - Refresh API_Docs.md nếu cần
   - Update instruction files
   - Log context changes trong Decisions.md
   ```

## Context Check Triggers

### Automatic Triggers
- Khi bắt đầu new conversation/session
- Khi detect thay đổi trong package files
- Khi user mention new library/framework
- Khi gặp error liên quan đến outdated patterns
- Mỗi 4 giờ trong long-running sessions

### Manual Triggers
- User request context refresh
- Before major refactoring
- When adding new dependencies
- During code review process

## Context Storage Structure
```
memory_bank/
├── context_session.md          # Current session context
├── context_history/             # Historical context data
│   ├── 2024-01-15-context.md   # Daily context snapshots
│   └── ...
├── library_cache/               # Cached Context7 data
│   ├── react-18.2.0.md         # Library-specific context
│   ├── flutter-3.16.0.md
│   └── ...
└── context_config.md            # Context check configuration
```

## Xử lý lỗi (Enhanced)

### Khi Context7 MCP không khả dụng
- Ghi log cảnh báo với timestamp và error details
- Sử dụng cached context nếu có
- Fallback về knowledge base cơ bản
- **MỚI**: Tiếp tục workflow với reduced functionality
- **MỚI**: Retry mechanism với exponential backoff
- Thông báo hạn chế cho người dùng một cách transparent

### Khi không tìm thấy thông tin
- Ghi log để tracking và improvement
- Đề xuất alternative approaches
- **MỚI**: Tìm kiếm trong local project memory
- **MỚI**: Suggest manual research directions
- Yêu cầu người dùng cung cấp thêm context khi cần thiết

### Khi có conflicts
- Cảnh báo rõ ràng về conflicts với detailed explanation
- Đề xuất resolution strategies với pros/cons
- **MỚI**: Rank solutions theo priority và feasibility
- **MỚI**: Provide migration paths khi cần thay đổi
- Để người dùng quyết định final approach với đầy đủ thông tin

### Timeout và Performance Issues
- **MỚI**: Set reasonable timeouts cho Context7 calls
- **MỚI**: Implement caching để reduce repeated calls
- **MỚI**: Graceful degradation khi performance issues
- **MỚI**: Background refresh của cached data

## Performance Optimization
- Batch multiple library lookups
- Use async context checks
- Implement smart caching strategy
- Rate limit Context7 API calls
- Prioritize critical libraries first

## Tích hợp với workflows khác

### Brainstorm Workflow Integration
- **BẮT BUỘC**: Tự động kích hoạt trong brainstorm phase
- Tìm kiếm similar projects và solutions
- Đề xuất tech stack dựa trên requirements
- Cung cấp market research và competitor analysis
- Lưu insights vào brainstorm documents

### Planning Workflow Integration
- **BẮT BUỘC**: Kiểm tra compatibility của architecture choices
- Validate tech stack selections
- Đề xuất implementation patterns
- Kiểm tra dependencies và potential conflicts
- Cung cấp timeline estimates dựa trên complexity

### Development Workflow Integration
- Tự động kích hoạt khi cần
- Cung cấp context cho coding tasks
- Real-time best practices suggestions
- Code review assistance với industry standards

### Project Identity Enforcement Integration
- **BẮT BUỘC**: Chạy như một phần của project identity check
- Validate project constraints với Context7 knowledge
- Update project memory với new insights
- Ensure compliance với project-specific requirements

## Success Metrics
- Reduced context-related errors
- Faster development với up-to-date patterns
- Better code quality với current best practices
- Improved developer experience
- Fewer deprecated pattern usage

## Maintenance
- Weekly review của context check effectiveness
- Monthly update của auto-check library list
- Quarterly optimization của caching strategy
- Annual review của workflow integration