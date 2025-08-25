---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.165Z"
---

#task-update-workflow
# Task Update Workflow

## Overview

Quy trình tự động cập nhật task khi người dùng yêu cầu "cập nhật task" hoặc "refresh task". Hệ thống sẽ tự động phân tích codebase hiện tại và cập nhật danh sách task dựa trên trạng thái thực tế của dự án.

## Trigger Keywords

- "cập nhật task"
- "refresh task"
- "update task"
- "kiểm tra task"
- "check task status"
- "task progress"

## Automatic Workflow Process

### 1. Task Discovery Phase

**BẮT BUỘC** thực hiện các bước sau khi phát hiện trigger keywords:

1. **Scan Kiro Specs Directory**
   - Tìm kiếm tất cả file `tasks.md` trong `.kiro/specs/*/`
   - Đọc và parse nội dung các task hiện có
   - Xác định cấu trúc và format của tasks

2. **Analyze Current Codebase**
   - Quét toàn bộ codebase để xác định các tính năng đã implement
   - Kiểm tra các file đã tạo, functions đã viết
   - Phân tích git history để xem các thay đổi gần đây

### 2. Task Status Analysis

**BẮT BUỘC** phân tích trạng thái từng task:

1. **Completed Tasks Detection**
   - Kiểm tra xem code đã implement chưa
   - Verify các file cần thiết đã tồn tại
   - Kiểm tra tests đã pass chưa
   - Xác định mức độ hoàn thành (0%, 25%, 50%, 75%, 100%)

2. **Pending Tasks Identification**
   - Xác định tasks chưa bắt đầu
   - Phát hiện tasks đang trong quá trình thực hiện
   - Identify blocked tasks và dependencies

3. **New Tasks Discovery**
   - Phát hiện các tính năng mới cần thiết dựa trên codebase
   - Identify technical debt cần giải quyết
   - Suggest improvements và optimizations

### 3. Task Update Execution

**BẮT BUỘC** cập nhật tasks theo format chuẩn:

1. **Update Existing Tasks**
   ```markdown
   ## [Task Name] - [Status: COMPLETED/IN_PROGRESS/PENDING/BLOCKED]
   
   **Progress**: [0-100]%
   **Last Updated**: [Date]
   **Implementation Status**: [Details]
   
   ### What's Done:
   - [List completed items]
   
   ### What's Remaining:
   - [List pending items]
   
   ### Files Modified:
   - [List of files]
   
   ### Dependencies:
   - [List dependencies]
   ```

2. **Add New Tasks**
   - Tạo tasks mới dựa trên phân tích codebase
   - Assign priorities và dependencies
   - Estimate effort và timeline

3. **Archive Completed Tasks**
   - Di chuyển completed tasks vào section riêng
   - Maintain history cho reference
   - Update project progress metrics

### 4. Report Generation

**BẮT BUỘC** tạo báo cáo tổng hợp:

1. **Progress Summary**
   ```markdown
   # Project Progress Report
   
   **Generated**: [Timestamp]
   **Total Tasks**: [Number]
   **Completed**: [Number] ([Percentage]%)
   **In Progress**: [Number]
   **Pending**: [Number]
   **Blocked**: [Number]
   
   ## Recent Achievements:
   - [List recent completions]
   
   ## Next Priorities:
   - [List high priority pending tasks]
   
   ## Blockers & Issues:
   - [List blocked tasks and reasons]
   ```

2. **Recommendations**
   - Suggest next actions
   - Identify optimization opportunities
   - Recommend refactoring needs

## Implementation Guidelines

### File Structure Requirements

**BẮT BUỘC** maintain cấu trúc file chuẩn:

```
.kiro/specs/
├── [project-name]/
│   ├── tasks.md              # Main tasks file
│   ├── tasks-completed.md    # Archived completed tasks
│   ├── tasks-progress.md     # Progress tracking
│   └── tasks-report.md       # Latest report
```

### Task Status Definitions

- **PENDING**: Chưa bắt đầu, có thể bắt đầu ngay
- **IN_PROGRESS**: Đang thực hiện, có progress > 0%
- **BLOCKED**: Bị chặn bởi dependencies hoặc issues
- **COMPLETED**: Hoàn thành 100%, đã test và verify
- **ARCHIVED**: Completed tasks đã được archive

### Quality Gates

**BẮT BUỘC** đảm bảo chất lượng cập nhật:

1. **Accuracy Check**
   - Verify task status accuracy bằng code analysis
   - Cross-check với git history
   - Validate dependencies và blockers

2. **Completeness Validation**
   - Ensure tất cả tasks được account for
   - No missing critical tasks
   - Proper categorization và prioritization

3. **Format Consistency**
   - Consistent markdown formatting
   - Standardized status indicators
   - Proper timestamps và metadata

## Integration Points

### Kiro System Integration

- **Input**: Existing `.kiro/specs/*/tasks.md` files
- **Process**: Automated analysis và update
- **Output**: Updated tasks với current status
- **Notification**: Summary report cho user

### Git Integration

- **Commit Analysis**: Check recent commits cho progress
- **Branch Analysis**: Identify work in progress
- **Tag Analysis**: Milestone completion tracking

### Code Analysis Integration

- **File Existence**: Check required files
- **Function Implementation**: Verify code completion
- **Test Coverage**: Ensure tests exist và pass
- **Documentation**: Check docs are updated

## Error Handling

### Common Scenarios

1. **Missing Tasks File**
   - Create new tasks.md với discovered tasks
   - Initialize với project analysis
   - Set up proper structure

2. **Corrupted Task Format**
   - Backup existing file
   - Reformat theo chuẩn
   - Preserve existing data

3. **Conflicting Status**
   - Prioritize code analysis over manual status
   - Flag conflicts cho manual review
   - Provide recommendations

## Performance Considerations

- **Incremental Analysis**: Only analyze changed files
- **Caching**: Cache analysis results cho repeated runs
- **Parallel Processing**: Analyze multiple projects simultaneously
- **Smart Filtering**: Focus on relevant files và directories

## Security & Privacy

- **No Sensitive Data**: Avoid logging sensitive information
- **Local Processing**: All analysis done locally
- **Backup Safety**: Always backup before modifications
- **Access Control**: Respect file permissions

## Usage Examples

### Basic Usage
```
User: "cập nhật task"
System: [Automatically runs this workflow]
Output: Updated tasks.md với current progress
```

### Advanced Usage
```
User: "refresh task cho mobile app project"
System: [Targets specific project in .kiro/specs/mobile-app/]
Output: Detailed progress report với recommendations
```

## Success Metrics

- **Accuracy**: >95% correct task status detection
- **Completeness**: 100% task coverage
- **Performance**: <30 seconds cho medium projects
- **User Satisfaction**: Clear, actionable reports

## Maintenance

- **Regular Updates**: Keep workflow updated với new patterns
- **Performance Monitoring**: Track execution times
- **Accuracy Validation**: Periodic manual verification
- **User Feedback**: Incorporate user suggestions

---

**Note**: Workflow này được thiết kế để hoạt động seamlessly với existing Kiro system và có thể được triggered từ bất kỳ IDE nào thông qua các integration files.