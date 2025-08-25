---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.114Z"
---

#development-rules
---
description: General development rules applicable across various technologies
globs: *.js,*.jsx,*.ts,*.tsx,*.py,*.java,*.go,*.php,*.rb,*.swift,*.kt,*.dart,*.cs
alwaysApply: false
---

## Code Quality
- ***BẮT BUỘC*** implement code theo acceptance criteria định nghĩa trong Kiro tasks
- ***BẮT BUỘC*** validate implementation với task requirements trước khi commit
- Tuân thủ nguyên tắc SOLID
- Tạo unit tests cho tất cả chức năng quan trọng
- Sử dụng tên biến/hàm mô tả rõ ràng mục đích
- Tránh duplicate code, ưu tiên tái sử dụng
- Giới hạn độ phức tạp của hàm và lớp
- Sử dụng comments khi cần thiết để giải thích ý định
- Follow defensive programming practices

## Architecture
- Phân chia rõ ràng các layer (presentation, business logic, data)
- Sử dụng dependency injection để tách bạch các thành phần
- Ưu tiên composition over inheritance
- Thiết kế API dễ sử dụng và mở rộng
- Áp dụng Domain-Driven Design cho dự án phức tạp

## Security
- Validate tất cả input từ người dùng
- Sử dụng parameterized queries để tránh SQL injection
- Mã hoá dữ liệu nhạy cảm (passwords, tokens, PII)
- Implement đúng cách các authentication và authorization
- Tuân thủ hướng dẫn OWASP top 10
- Sử dụng HTTPS cho mọi API endpoints

## Performance
- Tối ưu database queries để tránh N+1 problems
- Implement caching cho dữ liệu tĩnh và truy vấn đắt
- Tránh blocking operations trong event loop
- Sử dụng pagination cho large data sets
- Lazy load components và modules khi có thể
- Profiling code để phát hiện bottlenecks

## Error Handling
- Xử lý tất cả exceptions và errors
- Cung cấp error messages hữu ích nhưng an toàn
- Log errors đúng cách với context đủ để debug
- Implement retry mechanisms cho unstable operations
- Sử dụng circuit breakers cho external services

## Testing
- Viết unit tests với test coverage cao
- Implement integration tests cho critical flows
- Sử dụng mocking để tách biệt dependencies
- Ưu tiên testing pyramids (nhiều unit tests, ít e2e tests)
- Viết tests dễ đọc và maintain

## API Design
- Sử dụng RESTful principles hoặc GraphQL
- Versioning APIs đúng cách
- Cung cấp documentation đầy đủ (Swagger/OpenAPI)
- Implement proper error responses với HTTP status codes
- Tuân thủ JSON standards

## Development Workflow
- ***BẮT BUỘC*** kiểm tra `.kiro/specs/{project}/tasks.md` trước khi bắt đầu development
- ***BẮT BUỘC*** thực hiện tasks theo thứ tự ưu tiên trong Kiro specifications
- ***BẮT BUỘC*** cập nhật task status trong `.kiro/specs/{project}/tasks.md` khi bắt đầu và hoàn thành
- ***BẮT BUỘC*** reference task IDs trong commit messages (format: `feat(TASK-001): implement feature`)
- ***BẮT BUỘC*** validate acceptance criteria trước khi mark task completed
- Sử dụng version control (Git)
- Follow conventional commits với Kiro task references
- Code reviews cho mọi non-trivial changes
- Continuous Integration/Continuous Deployment
- Đảm bảo backwards compatibility khi có thể

## Configuration
- Sử dụng environment variables cho config
- Tách biệt config từ code
- Implement config validation
- Sử dụng default values hợp lý
- Documentation đầy đủ về config options

@file ../README.md