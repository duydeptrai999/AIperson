# Global Rules for All Projects

## 🔴 MANDATORY CODE QUALITY ENFORCEMENT

**_BẮT BUỘC_** tuân thủ 100% quy trình code quality mới. Không có exception nào được phép.

### Core Quality Rules (MANDATORY COMPLIANCE)

- **[Mandatory Code Quality Enforcement](mandatory-code-quality.md)** - TRAE specific enforcement
- **[AI Code Quality Automation](../../.trae/rules/ai-code-quality-automation.md)** - Manual workflow
- **[AI Manual Code Review Process](../../.trae/rules/ai-manual-code-review-process.md)** - Review protocols
- **[AI Execution Templates](../../.trae/rules/ai-execution-templates.md)** - Execution templates

## 🔥 Core Principles

### Communication

- Sử dụng tiếng Việt cho trò chuyện, tiếng Anh cho code/docs
- Trả lời rõ ràng, hỏi làm rõ khi cần
- Phân tích yêu cầu trước khi thực hiện

### Project Identity

- **🔴 BẮT BUỘC: Kiểm tra .project-identity trước mọi task**
- Load workflow rules phù hợp với projectType và projectStage

### Development Standards

- **🔴 BẮT BUỘC: Execute Pre-Code Analysis Workflow trước khi viết code**
- **🔴 BẮT BUỘC: Apply Real-Time Validation trong quá trình coding**
- **🔴 BẮT BUỘC: Run Post-Code Quality Check sau khi hoàn thành**
- Phân tích kỹ trước khi chỉnh sửa code
- Tập trung vào vấn đề chính, xác định root cause
- Thực hiện từng thay đổi lớn một cách cẩn thận
- Tuân thủ SOLID principles và clean code

### Safety & Documentation

- Tạo backup trước thay đổi lớn
- Ghi lại quyết định quan trọng vào docs/project/
- Cập nhật Codebase.md và Instruction.md
- Không tự ý tối ưu code ngoài yêu cầu

## 📁 File Organization

- **🔴 BẮT BUỘC: File .md trong `docs/project/`**
- `.cursor/rules/` - Development rules (nguồn chính thức)
- `instructions/` - Workflow và API docs
- `docs/templates/` - Template files

## 🔗 Specialized Rules

Các quy tắc chi tiết được định nghĩa trong:

### Core Development

- **[Project Identity Enforcement](.trae/rules/project-identity-enforcement.md)**
- **[Base Rules](.trae/rules/base-rules.md)**
- **[Development Rules](.trae/rules/development-rules.md)**
- **[File Protection Rules](.trae/rules/file-protection-rules.md)**

### Platform Specific

- **[Android Workflow](.trae/rules/android-workflow.md)**
- **[iOS Workflow](.trae/rules/ios-workflow.md)**
- **[Mobile Utility Workflow](.trae/rules/mobile-utility-workflow.md)**

### Project Management

- **[Planning Workflow](.trae/rules/planning-workflow.md)**
- **[Brainstorm Workflow](.trae/rules/brainstorm-detailed-workflow.md)**
- **[Memory Bank Workflow](.trae/rules/memory-bank-workflow.md)**

### Quality & Testing

- **[TSDDR 2.0 Mobile Workflow](../../docs/TSDDR-2.0-Guide.md)**
- **[Code Deduplication](.trae/rules/universal-code-deduplication.md)**
- **[Review Gate V2](.trae/rules/ReviewGateV2.md)**

> **📋 Xem danh sách đầy đủ trong `.cursor/rules/` directory**

## ⚠️ Enforcement

- **BẮT BUỘC** tuân thủ 100% rules trong `.cursor/rules/`
- **BẮT BUỘC** execute mandatory code quality workflow cho mọi code generation
- **NGHIÊM CẤM** tạo rules mới mà không sync với `.cursor/rules/`
- **NGHIÊM CẤM** bỏ qua Pre-Code Analysis, Real-Time Validation, Post-Code Quality Check
- Mọi customization phải được thực hiện trong `.cursor/rules/`

## 🚨 Critical Quality Gates

### Before Writing Code (MANDATORY)

```markdown
☐ Execute Pre-Code Analysis Workflow (30 seconds max)
☐ File structure scan và context understanding
☐ Load appropriate fix templates
☐ Configure confidence thresholds
```

### During Code Writing (MANDATORY)

```markdown
☐ Real-time import management
☐ Type safety enforcement
☐ Syntax validation
☐ Auto-fix application với confidence >= thresholds
```

### After Code Completion (MANDATORY)

```markdown
☐ Final import optimization
☐ Complete type safety check
☐ Syntax và style validation
☐ Security vulnerability scan
```

## 📊 Success Metrics (MANDATORY)

### Critical Success Indicators

```markdown
✅ Zero import errors
✅ Zero syntax errors
✅ Zero type safety violations
✅ All references resolved
✅ Consistent code style
```

### Performance Requirements

```markdown
📈 Import accuracy: > 95%
📈 Type safety coverage: > 98%
📈 Syntax error detection: > 99%
📈 Auto-fix success rate: > 90%
📈 False positive rate: < 5%
```

---

**🔴 ENFORCEMENT NOTICE**: Toàn bộ quy trình code quality này là MANDATORY và phải được execute cho mọi code generation. Không có exception nào được phép.
