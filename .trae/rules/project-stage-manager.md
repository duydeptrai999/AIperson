---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.169Z"
---

#project-stage-manager
---
description: 
globs: 
alwaysApply: false
---
# Project Stage #management-rules

## Nguyên Tắc Quản Lý Giai Đoạn
- ***BẮT BUỘC*** tuân theo đúng thứ tự giai đoạn: brainstorm → setup → development
- ***BẮT BUỘC*** hoàn thành giai đoạn hiện tại trước khi chuyển sang giai đoạn tiếp theo
- ***BẮT BUỘC*** cập nhật projectStage trong .project-identity khi chuyển giai đoạn
- ***BẮT BUỘC*** tạo file confirmation cho mỗi giai đoạn hoàn thành

## Stage 1: Brainstorm
### Mục Tiêu
- Thu thập và phân tích ý tưởng dự án
- Xác định đối tượng người dùng và nhu cầu
- Nghiên cứu đối thủ cạnh tranh
- Đánh giá rủi ro và khả thi

### Required Workflows
- `.trae/rules/brainstorm-workflow.md`
- `.trae/rules/brainstorm-detailed-workflow.md`

### Completion Criteria
- [x] File `Brainstorm_[ProjectName].md` được tạo
- [x] Hoàn thành 3 phases: Foundation → Structure → Advanced  
- [x] Risk assessment đã thực hiện

### Stage Transition
```json
{
  "projectStage": "setup",
  "completedStages": ["brainstorm"],
  "projectName": "[ActualProjectName]",
  "projectType": "[DetectedType]"
}
```

## Stage 2: Setup
### Mục Tiêu
- Thiết lập cấu trúc dự án
- Chọn tech stack phù hợp
- Cấu hình development environment
- Tạo project documentation
- Thiết lập Kiro task system

### Required Workflows
- `.trae/rules/project-creation-workflow.md`
- `.trae/rules/planning-workflow.md`
- `.trae/rules/kiro-task-execution.md`
- Platform-specific setup rules

### Completion Criteria
- [x] Project structure được tạo
- [x] Tech stack được xác định
- [x] Development environment ready
- [x] `Instruction.md` được tạo với roadmap
- [x] `.kiro/specs/{project}/` directory structure được tạo
- [x] `.kiro/specs/{project}/tasks.md` được tạo với initial tasks

### Stage Transition
```json
{
  "projectStage": "development",
  "completedStages": ["brainstorm", "setup"],
  "mainLanguages": ["actual languages"],
  "mainFrameworks": ["actual frameworks"]
}
```

## Stage 3: Development
### Mục Tiêu
- Thực hiện phát triển theo Kiro tasks
- Implement features theo priority và dependencies
- Testing và quality assurance
- Documentation và deployment

### Required Workflows
- `.trae/rules/kiro-task-execution.md` (Primary)
- `.trae/rules/auto-task-execution.md`
- `.trae/rules/development-rules.md`

### Platform-#specific-rules
- **Android**: `.trae/rules/android-workflow.md`
- **iOS**: `.trae/rules/ios-workflow.md`  
- **Flutter**: `.trae/rules/mobile-utility-workflow.md`
- **Web**: `.trae/rules/frontend-rules.md` + `.trae/rules/backend-rules-optimized.md`

### #continuous-rules
- Kiro task status tracking
- TDD workflows
- Code quality checks
- Regular progress reports

## Stage Detection Logic
```javascript
function detectCurrentStage() {
  // Check for Brainstorm files
  if (!fileExists("Brainstorm_*.md")) {
    return "brainstorm_required";
  }
  
  // Check for project structure
  if (!fileExists("Instruction.md") || !hasProjectStructure()) {
    return "setup_required";
  }
  
  return "development";
}
```

## Workflow Enforcement
### #blocking-rules
- Không thể bắt đầu setup khi chưa có Brainstorm file
- Không thể bắt đầu development khi chưa hoàn thành setup
- Phải có user confirmation trước khi chuyển stage

### Auto-Detection Triggers
- Empty project folder → Force brainstorm
- User mentions "ý tưởng mới" → Force brainstorm
- Missing Instruction.md → Force setup
- Invalid project structure → Force setup

## Stage Transition Notifications
```bash
# Telegram notification when stage completed
./scripts/notify.sh "Stage [STAGE_NAME] Completed" completed "Ready for next stage: [NEXT_STAGE]"
```

## Quality Gates
### Stage 1 → Stage 2
- Brainstorm file completeness check
- User confirmation received
- Basic project info defined

### Stage 2 → Stage 3  
- Project structure validation
- Tech stack compatibility check
- Development environment ready
- Kiro task system initialized
- Initial tasks defined in `.kiro/specs/{project}/tasks.md`

### Within Stage 3
- Kiro task completion tracking
- Regular milestone checks
- Code quality gates
- Testing coverage validation
- Task dependency validation
