# Project Identity Integration for Trae AI

## Mandatory Project Identity Check Protocol

### Pre-Work Requirements
- **BẮT BUỘC**: Kiểm tra `.project-identity` trước khi bắt đầu BẤT KỲ công việc nào
- **BẮT BUỘC**: Đọc phần "currentWorkingStatus" để tránh xung đột
- **BẮT BUỘC**: Kiểm tra "projectStage" và "workflowEnforcement" để biết ràng buộc hiện tại
- **BẮT BUỘC**: Load appropriate workflow rules dựa trên projectType và projectStage

### Work Declaration Protocol

#### Khi Bắt Đầu Công Việc
1. Cập nhật section "currentWorkingStatus" trong .project-identity:
   ```json
   "currentWorkingStatus": {
     "aiTool": "Trae",
     "workIntent": "Mô tả chi tiết ý định làm việc",
     "targetFiles": ["file1.js", "file2.md"],
     "status": "in_progress",
     "lastUpdate": "2024-12-20T11:00:00Z",
     "estimatedCompletion": "2024-12-20T12:00:00Z"
   }
   ```

2. Format mô tả ý định:
   - Rõ ràng, cụ thể về mục tiêu
   - Bao gồm scope của công việc
   - Ước tính thời gian hoàn thành

3. Target Files:
   - Ban đầu có thể để [] nếu chưa xác định
   - Cập nhật ngay khi tìm được file cụ thể
   - Liệt kê đầy đủ tất cả files sẽ chỉnh sửa

#### Trong Quá Trình Làm Việc
- **BẮT BUỘC**: Cập nhật "targetFiles" khi tìm được file cụ thể
- **BẮT BUỘC**: Cập nhật "lastUpdate" timestamp định kỳ
- **BẮT BUỘC**: Kiểm tra xung đột trước khi chỉnh sửa file
- **NGHIÊM CẤM**: Làm việc trên task đang được AI khác thực hiện

#### Sau Khi Hoàn Thành
- **BẮT BUỘC**: Xóa section "currentWorkingStatus" hoặc set status = "completed"
- **BẮT BUỘC**: Cập nhật "projectStage" nếu có tiến triển quan trọng
- **BẮT BUỘC**: Ghi lại thay đổi trong project history nếu cần

### JSON Validation Protocol
- **BẮT BUỘC**: Validate JSON syntax trước khi lưu .project-identity
- **BẮT BUỘC**: Backup file trước khi thực hiện thay đổi lớn
- **BẮT BUỘC**: Kiểm tra file integrity sau khi cập nhật

### Error Handling
- Nếu không thể truy cập .project-identity: Thông báo lỗi và dừng
- Nếu phát hiện xung đột: Thông báo và yêu cầu user quyết định
- Nếu JSON invalid: Restore từ backup và thông báo lỗi
- Nếu AI khác đang làm việc > 30 phút: Coi như timeout và cho phép override

### Integration với Trae Workflows
- Tích hợp check .project-identity vào tất cả Trae workflows
- Ưu tiên project identity check trước Context7 check
- Load workflow rules dựa trên projectType và projectStage
- Đảm bảo compatibility với existing Trae rules

## Example Usage

### Scenario 1: Feature Development
```json
"currentWorkingStatus": {
  "aiTool": "Trae",
  "workIntent": "Implementing user authentication module",
  "targetFiles": ["auth/login.js", "auth/register.js", "auth/middleware.js"],
  "status": "in_progress",
  "lastUpdate": "2024-12-20T11:00:00Z",
  "estimatedCompletion": "2024-12-20T13:00:00Z"
}
```

### Scenario 2: Bug Fix
```json
"currentWorkingStatus": {
  "aiTool": "Trae",
  "workIntent": "Fixing payment gateway timeout issue",
  "targetFiles": ["payment/gateway.js", "config/timeout.js"],
  "status": "in_progress",
  "lastUpdate": "2024-12-20T11:15:00Z",
  "estimatedCompletion": "2024-12-20T11:45:00Z"
}
```

### Scenario 3: Documentation Update
```json
"currentWorkingStatus": {
  "aiTool": "Trae",
  "workIntent": "Updating API documentation for v2.0",
  "targetFiles": ["docs/api-v2.md", "README.md"],
  "status": "in_progress",
  "lastUpdate": "2024-12-20T11:30:00Z",
  "estimatedCompletion": "2024-12-20T12:00:00Z"
}
```

## Compliance Verification
- **BẮT BUỘC**: Trae AI phải verify compliance với protocol này
- **BẮT BUỘC**: Log mọi project identity interactions để audit
- **BẮT BUỘC**: Report violations để continuous improvement
- **BẮT BUỘC**: Validate JSON format trong mọi cập nhật