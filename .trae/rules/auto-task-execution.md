---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.128Z"
---

#auto-task-execution
# Auto Task #execution-rule - Spec-Driven Precision

## Mục đích
Rule này định nghĩa quy trình tự động thực hiện các task theo nguyên tắc **Spec-Driven Development** với **Surgical Precision** - ONE task at a time, atomic changes, clear reasoning before execution. Tích hợp đầy đủ với EARS requirements, Kiro task system, và Context7 validation.

## Spec-Driven Principles

### Core Loop (MANDATORY)
1. **Planning**: Analyze task requirements và EARS specifications
2. **Reasoning**: Document WHY và HOW trước khi thực hiện
3. **Taking Actions**: Execute ONE atomic change at a time
4. **Evaluating Results**: Validate against acceptance criteria

### Surgical #precision-rules
- **ONE TASK**: Chỉ thực hiện một task tại một thời điểm
- **ATOMIC CHANGES**: Mỗi thay đổi phải độc lập và có thể revert
- **CLEAR REASONING**: Document lý do cho mỗi quyết định
- **VALIDATION FIRST**: Kiểm tra trước và sau mỗi thay đổi

## Điều kiện áp dụng

### Khi nào áp dụng Auto Task Execution
- **BẮT BUỘC** có file `.kiro/specs/{project}/tasks.md` với task list đầy đủ
- **BẮT BUỘC** tasks phải có EARS requirements rõ ràng (THE SYSTEM SHALL...)
- **BẮT BUỘC** người dùng yêu cầu "thực hiện", "implement", "làm theo kế hoạch" hoặc tương tự
- **BẮT BUỘC** các Kiro task phải có status "Not Started" và acceptance criteria rõ ràng
- **BẮT BUỘC** không có task nào yêu cầu input bổ sung từ người dùng
- **BẮT BUỘC** dependencies của task đã được resolved hoặc completed
- **BẮT BUỘC** Context7 validation đã pass cho architecture decisions

### Khi nào KHÔNG áp dụng
- Kế hoạch chưa đầy đủ hoặc thiếu chi tiết
- Có task yêu cầu quyết định từ người dùng (ví dụ: lựa chọn design, API endpoints)
- Task có rủi ro cao (xóa dữ liệu, thay đổi cấu trúc lớn)
- Người dùng chỉ hỏi về kế hoạch mà không yêu cầu thực hiện

## Quy trình thực hiện (Spec-Driven Enhanced)

### Bước 0: Pre-Execution Analysis (NEW)
1. **Context Analysis**:
   - Load project context từ `.project-identity`
   - Analyze tech stack và existing patterns
   - Check for Context7 best practices
   
2. **Requirements Mapping**:
   - Map task IDs to EARS requirements
   - Identify acceptance criteria format
   - Check for ambiguous requirements

### Bước 1: Xác nhận điều kiện (Enhanced)
1. Kiểm tra file `.kiro/specs/{project}/tasks.md` tồn tại và có task list
2. Validate Kiro task format (ID, status, priority, dependencies, acceptance criteria)
3. **NEW**: Validate EARS requirements cho mỗi task
4. **NEW**: Check import dependencies với Import Analyzer
5. **NEW**: Validate async patterns với Async Validator
6. Xác định task nào có thể thực hiện dựa trên dependencies
7. Đánh giá mức độ rủi ro của từng task

### Bước 2: Thông báo bắt đầu
- Thông báo ngắn gọn về việc bắt đầu thực hiện kế hoạch
- Liệt kê số lượng task sẽ được thực hiện
- Không yêu cầu xác nhận từ người dùng

### Bước 3: Thực hiện với Surgical Precision

#### Phase 1: Planning (Per Task)
```
1. Extract EARS requirement: "WHEN [trigger] THE SYSTEM SHALL [action]"
2. Break down into atomic sub-tasks
3. Identify files to be modified
4. Plan test scenarios
```

#### Phase 2: Reasoning (Document Before Action)
```
## Task: [TASK-ID] - [Description]
### Why:
- Business reason: [Why this task is needed]
- Technical reason: [Why this approach]

### How:
- Step 1: [Specific action with file:line]
- Step 2: [Specific action with file:line]

### Validation:
- [ ] Unit test covers the change
- [ ] Integration test if needed
- [ ] Manual test scenario
```

#### Phase 3: Taking Actions (ONE at a time)
- **ATOMIC RULE**: One file, one change, one commit
- Update task status: "Not Started" → "In Progress"
- Execute the FIRST atomic change
- Validate the change works
- Commit with message: `feat(TASK-001): [specific change description]`
- Repeat for next atomic change

#### Phase 4: Evaluating Results
- Run tests after EACH atomic change
- Validate against acceptance criteria
- Check for side effects
- Update task status → "Completed" only when ALL criteria pass
- Document any deviations or issues

### Bước 4: Báo cáo kết quả
- Tóm tắt những gì đã hoàn thành
- Liệt kê file đã tạo/chỉnh sửa
- Thông báo nếu có task nào không thể hoàn thành

## Xử lý ngoại lệ (Spec-Driven Enhanced)

### Khi gặp lỗi trong quá trình thực hiện
1. **Immediate Stop**: Dừng ngay atomic action hiện tại
2. **Root Cause Analysis**:
   ```
   Error Context:
   - Task: [TASK-ID]
   - Action: [Specific action that failed]
   - File: [file:line where error occurred]
   - Error: [Exact error message]
   
   Potential Causes:
   1. [Hypothesis 1]
   2. [Hypothesis 2]
   
   Attempted Fix:
   - [What was tried]
   - [Result]
   ```
3. **Recovery Options**:
   - Revert last atomic change
   - Try alternative approach
   - Mark task as blocked with reason
4. **User Communication**:
   - Explain what failed and why
   - Present recovery options
   - Ask for guidance if unclear

### Khi task yêu cầu input bổ sung
- Dừng và yêu cầu input từ người dùng
- Giải thích rõ cần input gì và tại sao
- Sau khi có input, tiếp tục với task hiện tại

## Nguyên tắc an toàn (Surgical Precision)

### File Protection
- **BẮT BUỘC** tạo backup trước khi xóa/thay đổi file quan trọng
- **BẮT BUỘC** kiểm tra file tồn tại trước khi thao tác
- **BẮT BUỘC** commit sau MỖI atomic change (không phải sau task lớn)
- **BẮT BUỘC** validate file syntax sau mỗi edit
- **BẮT BUỘC** check for broken imports sau changes

### Atomic #change-rules
- **ONE FILE**: Chỉ modify một file per atomic action
- **ONE CONCEPT**: Mỗi change focus vào một concept
- **REVERSIBLE**: Mọi change phải dễ dàng revert
- **TESTABLE**: Mọi change phải có thể test độc lập

### Validation (Multi-Layer)

#### Syntax Validation (After EACH edit)
- Language-specific syntax check
- Import resolution validation
- Type checking (if applicable)

#### Semantic Validation (After EACH atomic change)
- Unit test for the specific change
- Integration test if interfaces changed
- Acceptance criteria partial check

#### System Validation (After task completion)
- Full acceptance criteria validation
- Cross-module integration tests
- Performance benchmarks if relevant
- Security validation if applicable

## Ví dụ sử dụng (Spec-Driven)

### Tình huống 1: Kế hoạch đầy đủ với EARS
```
User: "Thực hiện kế hoạch đã lập"

AI: "Phân tích kế hoạch với Spec-Driven approach:

📋 Tìm thấy 5 tasks với EARS requirements:
- TASK-001: WHEN user clicks login THE SYSTEM SHALL validate credentials
- TASK-002: THE SYSTEM SHALL hash passwords with bcrypt
...

🔍 Bắt đầu với TASK-001 (no dependencies):

## Planning TASK-001
EARS: WHEN user clicks login THE SYSTEM SHALL validate credentials
Atomic actions:
1. Create login form component
2. Add validation logic
3. Connect to auth service

## Reasoning
Why: Users need secure authentication
How: Implement form → validation → service integration

🔨 Executing atomic action 1/3: Create login form..."

[Thực hiện từng atomic action với validation]
```

### Tình huống 2: Kế hoạch thiếu chi tiết
```
User: "Implement theo plan"
AI: "Kế hoạch hiện tại thiếu một số chi tiết. Tôi cần làm rõ..."
→ Yêu cầu bổ sung thông tin trước
```

### Tình huống 3: Task có rủi ro
```
User: "Làm theo kế hoạch"
AI: "Task 3 sẽ xóa database. Bạn có chắc chắn muốn tiếp tục?"
→ Yêu cầu xác nhận cho task rủi ro cao
```

## Spec-Driven Integration Points

### Kiro Task System
- **BẮT BUỘC** sử dụng [Kiro Task Execution](kiro-task-execution.md) làm engine chính
- **BẮT BUỘC** tuân thủ Kiro task format và validation rules
- **BẮT BUỘC** cập nhật task status trong `.kiro/specs/{project}/tasks.md`

### Planning Workflow
- **BẮT BUỘC** sử dụng [Planning Workflow](planning-workflow.md) để đảm bảo kế hoạch chất lượng
- **BẮT BUỘC** áp dụng [Planning #validation-rules](planning-validation-rules.md) để validate trước khi thực hiện
- **BẮT BUỘC** tuân thủ [Planning Enforcement](planning-enforcement.md) trong quá trình thực hiện

### File #protection-rules
- **BẮT BUỘC** tuân thủ [File #protection-rules](file-protection-rules.md) cho mọi thao tác file
- **BẮT BUỘC** áp dụng backup strategy trước những thay đổi lớn

### Development Control
- **BẮT BUỘC** theo [Development #control-rules](development-control-rules.md) để kiểm soát quy trình
- **BẮT BUỘC** đảm bảo code quality theo [#development-rules](development-rules.md)
- **BẮT BUỘC** apply [Import Analyzer](import-analyzer.md) sau mỗi file change
- **BẮT BUỘC** apply [Async Validator](async-validator.md) cho async code
- **BẮT BUỘC** use [Context Generator](context-generator.md) để maintain context
- **KHUYẾN NGHỊ** sử dụng [User Intent Analysis Workflow](user-intent-analysis-workflow.md) để hiểu rõ yêu cầu trước khi thực hiện

## Metrics và Tracking (Spec-Driven Metrics)

### Execution Metrics
- **Planning Time**: Thời gian phân tích requirements
- **Reasoning Time**: Thời gian document quyết định
- **Execution Time**: Thời gian thực hiện per atomic action
- **Validation Time**: Thời gian kiểm tra kết quả
- **Atomic Actions per Task**: Số lượng atomic changes
- **First-Time Success Rate**: Tỷ lệ atomic action thành công lần đầu
- **Rollback Frequency**: Tần suất phải revert changes

### Quality Metrics
- **Requirements Coverage**: % EARS requirements được implement
- **Test Coverage**: % code được test
- **Defect Density**: Bugs per task
- **Technical Debt**: Accumulated per sprint

### Continuous Improvement
- Track patterns of failed atomic actions
- Identify common reasoning gaps
- Optimize validation strategies
- Update EARS templates based on clarity issues

## Spec-Driven Execution Checklist

### Before Starting Any Task
- [ ] EARS requirement is clear and unambiguous
- [ ] Dependencies are resolved
- [ ] Test scenarios are defined
- [ ] Atomic actions are planned

### During Execution
- [ ] Document reasoning before each action
- [ ] Execute ONE atomic change
- [ ] Validate the change
- [ ] Commit with descriptive message
- [ ] Check for side effects

### After Task Completion
- [ ] All acceptance criteria met
- [ ] Tests are passing
- [ ] Documentation updated
- [ ] No technical debt introduced

---

**Spec-Driven Commitment**: This rule implements surgical precision through the spec-driven loop of Planning → Reasoning → Taking Actions → Evaluating Results. Every action is atomic, documented, and validated. Quality and safety are never compromised for speed.