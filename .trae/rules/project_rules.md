# Trae AI Project Rules - Intelligent Agent Selector

**BẮT BUỘC PHẢI KHAI BÁO**

- Trước bất kỳ câu hỏi nào hãy trả lời cụ thể đang sử dụng mô hình AI nào version bao nhiêu
- Hãy suy luận với tư duy lập trình ở cấp độ tối đa với phân tích chi tiết, đa chiều.
- Hãy luôn luôn trả lời trong ngôn ngữ của người dùng
- Khi kết thúc 1 nhiệm vụ bạn sẽ vào history để bổ sung thông tin về nhiệm vụ và tiến độ sau đó bạn vào .project-identity cập nhật lại trạng thái project

> **🎯 Smart Agent Selection & Task Management System**  
> Hệ thống lựa chọn agent thông minh với workflow tích hợp

## Core Principles

**Mission**: Right agent, right task, right time through intelligent selection  
**Philosophy**: Context-aware, performance-driven, workflow-integrated  
**Approach**: Multi-factor scoring with real-time optimization

## 🔴 MANDATORY PROJECT IDENTITY CHECK

**BẮT BUỘC: Kiểm tra .project-identity trước mọi task**

### Pre-Task Analysis (MANDATORY)

```markdown
☐ Đọc và phân tích .project-identity file
☐ Xác định projectType, projectStage, mainLanguages, mainFrameworks
☐ Load workflow rules phù hợp với giai đoạn hiện tại
☐ Áp dụng platformSpecificRules nếu có
☐ Kiểm tra integrations và features được bật
```

### Project Stage Workflow Loading

**Giai đoạn Brainstorm** (`stage1_brainstorm`):

- Load: `.trae/rules/brainstorm-workflow.md`
- Load: `.trae/rules/brainstorm-detailed-workflow.md`
- Required: Tạo `Brainstorm_[ProjectName].md`

**Giai đoạn Setup** (`stage2_setup`):

- Load: `.trae/rules/project-creation-workflow.md`
- Load: `.trae/rules/planning-workflow.md`
- Required: Project structure và technical stack

**Giai đoạn Development** (`stage3_development`):

- Load rules dựa trên `projectType`:
  - **Android**: `.trae/rules/android-workflow.md` + `docs/TSDDR-2.0-Guide.md`
  - **iOS**: `.trae/rules/ios-workflow.md` + `docs/TSDDR-2.0-Guide.md`
  - **Flutter**: `.trae/rules/mobile-utility-workflow.md` + `docs/TSDDR-2.0-Guide.md`
  - **Web**: `.trae/rules/frontend-rules.md` + `.trae/rules/backend-rules-optimized.md`

### Always Applied Rules

```markdown
✅ .trae/rules/project-identity-enforcement.md
✅ .trae/rules/base-rules.md
✅ .trae/rules/context7-auto-workflow.md
✅ .trae/rules/development-rules.md
✅ .trae/rules/markdown-unified-rules.md
✅ .trae/rules/cursor-optimization-rules.md
```

### New Project Detection

**Triggers for New Project**:

- Empty project folder hoặc không có file .project-identity
- Không tồn tại file `Brainstorm_*.md`
- User đề cập "ý tưởng mới", "dự án mới", "bắt đầu project"

**Mandatory First Step**: `stage1_brainstorm`

**Blocking Message khi phát hiện dự án mới**:

```
🚫 DỰ ÁN MỚI PHÁT HIỆN - BẮT BUỘC BRAINSTORM TRƯỚC
✅ Hãy bắt đầu: 'Tôi muốn brainstorm ý tưởng...'
```

### Stage Progression Rules

**Quy tắc tiến trình giai đoạn**:

- ❌ **Không được phép bỏ qua giai đoạn**
- ✅ **Yêu cầu xác nhận hoàn thành** trước khi chuyển giai đoạn
- 🔄 **Tự động cập nhật projectStage** trong .project-identity
- 📋 **Kiểm tra requiredOutputs** của giai đoạn hiện tại

**Stage Completion Checklist**:

```markdown
stage1*brainstorm → stage2_setup:
☐ File Brainstorm*[ProjectName].md đã tạo
☐ .project-identity đã cập nhật basic info
☐ User xác nhận hoàn thành brainstorm

stage2_setup → stage3_development:
☐ Project structure đã tạo
☐ Technical stack đã chọn
☐ Development environment ready
☐ User xác nhận sẵn sàng development
```

## General Rules

### Code Quality Standards

- **Clean Code**: Established conventions, readable structure
- **Documentation**: Inline comments, external guides
- **Testing**: Unit, integration, end-to-end coverage
- **Security**: Input validation, data protection, secure practices
- **Performance**: Optimized algorithms, efficient resource usage

### Project Structure

- **Modular Design**: Loosely coupled, highly cohesive
- **Version Control**: Git workflow, meaningful commits
- **Configuration**: Environment-specific settings
- **Dependencies**: Minimal, maintained, security-audited

## Agent Selection System

### Core Algorithm

**Multi-Factor Scoring (100%)**:

1. **Context Analysis** (35%): Tech stack, file types, project structure
2. **Keyword Matching** (25%): Primary/secondary technology keywords
3. **Complexity Assessment** (20%): Task difficulty and scope
4. **Performance History** (15%): Success rates, quality metrics
5. **User Preferences** (5%): Historical selections, feedback

### Selection Process

```
Project Identity Check → Input Analysis → Context Extraction → Agent Scoring → Decision → Assignment
```

**Enhanced Process Flow**:

1. **Project Identity Analysis**: Load .project-identity, determine stage & type
2. **Workflow Rules Loading**: Apply appropriate rules based on project configuration
3. **Context Extraction**: Analyze task requirements with project context
4. **Agent Scoring**: Multi-factor scoring with project-specific weights
5. **Decision & Assignment**: Select optimal agent with workflow integration

**Decision Thresholds**:

- **High Confidence** (>85%): Direct assignment
- **Medium Confidence** (70-85%): Assignment with monitoring
- **Low Confidence** (<70%): User confirmation required

### YOLO Mode

**Fast-Track Selection**:

- **Triggers**: "yolo", "quick", "fast", "asap"
- **Process**: Simplified analysis, immediate assignment
- **Threshold**: Minimum 60% confidence
- **Monitoring**: Enhanced real-time tracking

## Available Agents

### 📱 iOS Development Agent

**Specialization**: Native iOS apps with Swift/SwiftUI  
**Keywords**: swift, swiftui, ios, xcode, uikit, core data  
**Capabilities**: Swift (10/10), SwiftUI (10/10), Xcode (10/10), App Store (9/10)  
**Workflows**: → [ios-workflow.md](./ios-workflow.md) | [ios-project-template.md](./ios-project-template.md)  
**Success Rate**: 85% | Quality: 9.2/10

### 🤖 Android Development Agent

**Specialization**: Native Android with Kotlin/Jetpack Compose  
**Keywords**: kotlin, android, jetpack compose, gradle, room  
**Capabilities**: Kotlin (10/10), Jetpack Compose (9/10), Android Studio (10/10)  
**Workflows**: → [android-workflow.md](./android-workflow.md) | [TSDDR 2.0 Guide](../../docs/TSDDR-2.0-Guide.md)  
**Success Rate**: 82% | Quality: 8.9/10

### 🔧 APK Modification Agent

**Specialization**: Reverse engineering, Firebase integration, SafeAds  
**Keywords**: apk, reverse engineering, firebase, safeads, smali  
**Capabilities**: APK Analysis (9/10), Firebase (8/10), SafeAds (9/10)  
**Workflows**: → [android-workflow.md](./android-workflow.md)  
**Success Rate**: 78% | Quality: 8.5/10

### 🌐 Frontend Development Agent

**Specialization**: Modern web frontends with React/Vue/Angular  
**Keywords**: react, vue, angular, typescript, tailwind, nextjs  
**Capabilities**: React (9/10), TypeScript (9/10), CSS (8/10), Responsive (9/10)  
**Workflows**: → [frontend-rules.md](./frontend-rules.md) | [shadcn-ui-rules.md](./shadcn-ui-rules.md)  
**Success Rate**: 88% | Quality: 8.8/10

### ⚙️ Backend Development Agent

**Specialization**: Server-side APIs, databases, microservices  
**Keywords**: nodejs, laravel, api, database, docker, microservices  
**Capabilities**: Node.js (9/10), Laravel (8/10), APIs (9/10), Databases (8/10)  
**Workflows**: → [development-rules.md](./development-rules.md) | [database-management.md](./database-management.md)  
**Success Rate**: 86% | Quality: 8.7/10

### 📱 Mobile Cross-platform Agent

**Specialization**: Flutter, React Native hybrid development  
**Keywords**: flutter, react native, dart, expo, hybrid  
**Capabilities**: Flutter (8/10), React Native (8/10), Dart (8/10)  
**Workflows**: → [TSDDR 2.0 Guide](../../docs/TSDDR-2.0-Guide.md)  
**Success Rate**: 80% | Quality: 8.4/10

### 🚀 DevOps Infrastructure Agent

**Specialization**: Docker, Kubernetes, CI/CD, cloud deployment  
**Keywords**: docker, kubernetes, cicd, aws, gcp, terraform  
**Capabilities**: Docker (9/10), K8s (8/10), CI/CD (9/10), Cloud (8/10)  
**Workflows**: → [infrastructure-rules.md](./infrastructure-rules.md) | [git-workflow.md](./git-workflow.md)  
**Success Rate**: 83% | Quality: 8.6/10

## Workflow Index

### Core Workflows

**📋 Task Creation** → [task-creation-workflow.md](./task-creation-workflow.md)  
**🤖 Agent Selection** → [agent-selection-workflow.md](./agent-selection-workflow.md)  
**⚡ YOLO Mode** → [yolo-mode-workflow.md](./yolo-mode-workflow.md)  
**📋 Planning** → [planning-workflow.md](./planning-workflow.md)  
**✅ Validation** → [validate-workflow.md](./validate-workflow.md)

### Development Workflows

**📱 iOS Development** → [ios-workflow.md](./ios-workflow.md)  
**🤖 Android Development** → [android-workflow.md](./android-workflow.md)  
**🌐 Frontend Development** → [frontend-rules.md](./frontend-rules.md)  
**⚙️ Backend Development** → [development-rules.md](./development-rules.md)  
**🧪 TSDDR 2.0 Mobile** → [../../docs/TSDDR-2.0-Guide.md](../../docs/TSDDR-2.0-Guide.md)  
**🔄 Git Workflow** → [git-workflow.md](./git-workflow.md)

### Specialized Workflows

**🎨 Design to Prompt** → [design-to-prompt.md](./design-to-prompt.md)  
**🏗️ Infrastructure** → [infrastructure-rules.md](./infrastructure-rules.md)  
**🗄️ Database Management** → [database-management.md](./database-management.md)  
**🌍 i18n Rules** → [i18n-rules.md](./i18n-rules.md)  
**🎯 Brainstorm** → [brainstorm-workflow.md](./brainstorm-workflow.md)

## Performance Targets

**System**: Selection accuracy >90%, Response time <2s, Uptime >99.5%  
**Quality**: Code quality >8.5/10, User satisfaction >4.5/5, Error rate <2%

## Integration Points

### Project Identity Integration

**Automatic Configuration Loading**:

- Load workflow rules dựa trên `projectType` và `projectStage`
- Apply platform-specific rules từ `platformSpecificRules`
- Enable/disable features dựa trên `features` configuration
- Load integrations (Telegram, MCP tools, AI APIs)

**Context7 Memory Integration**:

- Tự động kiểm tra memories liên quan đến project
- Load project-specific knowledge và best practices
- Sync với project constraints và requirements

**Dynamic Workflow Adaptation**:

- Real-time adjustment dựa trên project progress
- Automatic stage progression với validation
- Context-aware agent selection với project history

### External Integrations

**Trae AI**: Dynamic agent loading, intelligent workload balancing, real-time metrics  
**Kiro System**: Automated requirement extraction, AI-enhanced task creation, progress tracking  
**Telegram Notifications**: Project milestone alerts, completion notifications  
**MCP Tools**: Browser debugging, enhanced development capabilities

## Project Identity Enforcement

### Mandatory Checks (EVERY SESSION)

```markdown
🔴 CRITICAL: Đọc .project-identity trước mọi task
🔴 CRITICAL: Kiểm tra currentWorkingStatus trong .project-identity để tránh xung đột
🔴 CRITICAL: Validate projectStage và load appropriate workflows
🔴 CRITICAL: Apply always_applied rules + stage-specific rules
🔴 CRITICAL: Check for new project detection triggers
```

### Multi-AI Coordination Protocol

**BẮT BUỘC: Kiểm tra .project-identity trước mọi công việc**

#### Project Identity Status Check

```markdown
☐ Đọc file .project-identity ở root project
☐ Kiểm tra section "currentWorkingStatus" để tránh xung đột
☐ Xác định AI nào đang làm việc trên task nào
☐ NGHIÊM CẤM làm việc trên task đang được AI khác thực hiện
```

#### Work Status Declaration

**Khi bắt đầu làm việc**:

1. **Cập nhật section "currentWorkingStatus"** trong .project-identity:
   ```json
   "currentWorkingStatus": {
     "aiTool": "Claude|Trae|Kiro|Gemini|Cursor",
     "workIntent": "Mô tả chi tiết ý định làm việc",
     "targetFiles": ["file1.js", "file2.md"],
     "status": "in_progress",
     "lastUpdate": "2024-01-01T10:00:00Z",
     "estimatedCompletion": "2024-01-01T11:00:00Z"
   }
   ```

**Trong quá trình làm việc**:

- Cập nhật "targetFiles" khi xác định được files cụ thể cần chỉnh sửa
- Cập nhật "lastUpdate" timestamp định kỳ
- Không được làm việc trên task đang có AI khác thực hiện

**Sau khi hoàn thành**:

- Xóa section "currentWorkingStatus" hoặc set status = "completed"
- Cập nhật "projectStage" nếu có tiến triển quan trọng
- Ghi lại thay đổi trong project history nếu cần

#### Conflict Resolution

**Khi phát hiện xung đột**:

```
⚠️  CẢNH BÁO: Task đang được [AI_NAME] thực hiện
🔧 HÀNH ĐỘNG: Chờ đợi hoặc làm việc trên task khác
📋 THÔNG BÁO: Thông báo user về tình trạng xung đột
```

**Timeout Rules**:

- Nếu AI không cập nhật "lastUpdate" > 30 phút: Coi như idle
- User có thể force override bằng cách xóa "currentWorkingStatus"

### Error Handling

**Missing .project-identity**:

```
⚠️  CẢNH BÁO: Không tìm thấy .project-identity
🔧 HÀNH ĐỘNG: Tự động tạo template và yêu cầu user cấu hình
```

**Invalid projectStage**:

```
❌ LỖI: projectStage không hợp lệ hoặc thiếu
🔧 HÀNH ĐỘNG: Reset về stage1_brainstorm và yêu cầu xác nhận
```

**Missing Required Files**:

```
📋 KIỂM TRA: Validate requiredOutputs của giai đoạn hiện tại
🔧 HÀNH ĐỘNG: Tạo missing files hoặc downgrade stage
```

## 🗂️ CODE_BASE TEMPLATE SYSTEM

**BẮT BUỘC: Tham khảo code_base/ khi thiết kế cấu trúc dự án**

- Tạo code_base dựa trên dự án hiện tại nếu chưa có

### Mandatory Code Base Check

```markdown
☐ Đọc code_base/project-map.md - project overview
☐ Tham khảo code_base/quick-reference.md - navigation nhanh
☐ Sử dụng code_base/ai-navigation-guide.md - hướng dẫn chi tiết
☐ Áp dụng template patterns phù hợp với projectType
```

### Template Selection

**Dựa trên projectType từ .project-identity**:

- 📱 **Mobile**: Load mobile-specific templates
- 🌐 **Web**: Load web-specific templates
- 🔌 **API**: Load API-specific templates
- 🔄 **Multi-platform**: Load hybrid templates

---

**🎯 Smart agent selection with workflow integration for optimal development efficiency**
