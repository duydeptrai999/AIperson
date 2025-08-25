# Trae AI Project Rules - Agent Control System

> **🚀 TRAE AI AGENT CONTROL SYSTEM**  
> **BẮT BUỘC** sử dụng Trae AI Agent Selector System cho mọi task và workflow.  
> File này là hệ thống điều khiển tổng thể cho tất cả các agent trong Trae AI IDE.

---

# 🎯 TRAE AI AGENT SELECTOR SYSTEM

> **🎯 Intelligent Agent Selection & Task Routing**

## System Overview

**Purpose**: Automatically select the most appropriate specialized agent based on task analysis with mandatory Test-First workflow validation  
**Method**: TSDDR 2.0 Workflow validation first, then multi-factor analysis with keyword matching, context analysis, and capability scoring  
**Output**: Optimal agent selection with confidence score and reasoning (only after workflow validation passes)  
**Critical Requirement**: All projects must have TSDDR 2.0 Workflow and Kiro Specs structure before agent selection proceeds

## Selection Algorithm

### Context Analysis Engine

**File Type Detection**:
- `.swift`, `.xcodeproj` → iOS Development Agent
- `.kt`, `.java`, `build.gradle` → Android Development Agent
- `.smali`, `.apk`, `AndroidManifest.xml` → APK Modification Agent
- `.js`, `.ts`, `.jsx`, `.tsx`, `package.json` → Frontend Development Agent
- `.php`, `composer.json`, Laravel files → Backend Development Agent
- `.dart`, `pubspec.yaml` → Mobile Cross-platform Agent
- `Dockerfile`, `.yml`, `.yaml` → DevOps Agent

**Keyword Analysis**:
- **iOS**: swift, swiftui, xcode, ios, cocoapods, carthage
- **Android**: kotlin, android, jetpack, gradle, room
- **APK Modification**: apk, smali, decompile, firebase, google-services, safeads, reverse-engineering, mod app
- **Frontend**: react, vue, angular, nextjs, typescript, css
- **Backend**: api, server, database, laravel, express, nodejs
- **Cross-platform**: flutter, react-native, dart, expo
- **DevOps**: docker, kubernetes, ci/cd, deployment, aws

**Complexity Assessment**:
- **Simple**: Single file changes, bug fixes, minor updates
- **Medium**: Feature additions, refactoring, multi-file changes
- **Complex**: Architecture changes, new modules, system integration

### Agent Capability Matching

**Scoring Factors**:
1. **Keyword Match** (35%): Direct technology/framework alignment
2. **File Type** (25%): Project structure and file extensions
3. **Complexity** (20%): Agent's capability to handle task complexity
4. **Performance** (15%): Historical success rate and efficiency
5. **User Preference** (5%): Previous selections and feedback

**Selection Logic**:
```
if (score >= 0.75) → High Confidence Selection
if (score >= 0.55) → Medium Confidence Selection
if (score >= 0.35) → Low Confidence Selection
if (score < 0.35) → Request Clarification
```

## Agent Profiles

### iOS Development Agent
**Specialization**: Native iOS apps with Swift/SwiftUI  
**Strengths**: iOS frameworks, App Store guidelines, Apple ecosystem  
**Triggers**: iOS-specific keywords, .swift files, Xcode projects

### Android Development Agent
**Specialization**: Native Android apps with Kotlin/Java  
**Strengths**: Android SDK, Jetpack libraries, Google Play guidelines  
**Triggers**: Android-specific keywords, .kt/.java files, Gradle builds

### Frontend Development Agent
**Specialization**: Web frontend with React/Vue/Angular  
**Strengths**: Modern JavaScript, CSS frameworks, build tools  
**Triggers**: Frontend keywords, .js/.ts files, package.json

### Backend Development Agent
**Specialization**: Server-side APIs and databases  
**Strengths**: RESTful APIs, database design, server architecture  
**Triggers**: Backend keywords, API endpoints, database schemas

### Mobile Cross-platform Agent
**Specialization**: Flutter and React Native development  
**Strengths**: Cross-platform frameworks, shared codebase  
**Triggers**: Flutter/RN keywords, .dart files, pubspec.yaml

### APK Modification Agent
**Specialization**: APK reverse engineering, Google Services integration, Firebase SDK updates  
**Strengths**: Smali analysis, SDK integration, method limit management, SafeAds implementation  
**Triggers**: APK modification keywords, .smali files, Firebase integration, Google Services

### DevOps Infrastructure Agent
**Specialization**: Deployment, CI/CD, infrastructure  
**Strengths**: Docker, Kubernetes, cloud platforms, automation  
**Triggers**: DevOps keywords, Docker files, YAML configs

## Selection Process

### Step 1: Initial Analysis
1. **🔴 PRIORITY 1: TSDDR 2.0 Workflow Validation**
   - **MANDATORY FIRST CHECK**: Verify existence of TSDDR 2.0 Workflow documentation
   - **Kiro Specs Validation**: Check for workflow implementation in `.kiro/specs/` directory
   - **Action Required if Missing**: **STOP** agent selection process immediately

2. Parse user request for keywords and context
3. Analyze project structure and file types
4. Assess task complexity and scope
5. Identify primary technology stack

### Step 2: Agent Scoring
1. Calculate keyword match scores for each agent
2. Apply file type and context bonuses
3. Factor in complexity handling capabilities
4. Include performance and preference weights
5. Generate final confidence scores

### Step 3: Selection Decision
1. Select highest scoring agent if confidence > 0.6
2. Request clarification if all scores < 0.4
3. Provide alternative suggestions for medium confidence
4. Log selection reasoning and confidence level

---

# 🚀 YOLO MODE - Fast-Track Agent Selection

> **🚀 Fast-Track Agent Selection for Rapid Development**

## YOLO Mode Overview

**Purpose**: Provide a streamlined agent selection process for projects that prioritize speed over strict workflow compliance  
**Method**: Direct multi-factor analysis with keyword matching, context analysis, and capability scoring (bypassing workflow validation)  
**Philosophy**: "You Only Live Once" - Sometimes you need to move fast and break things

## YOLO Mode Activation

### Trigger Keywords
**Automatic YOLO Mode Detection**:
- "yolo", "nhanh", "rapid", "quick", "fast-track"
- "prototype", "poc", "proof of concept", "demo"
- "skip validation", "bypass workflow", "no process"
- "emergency", "urgent", "hotfix", "critical"
- "simple project", "small task", "one-off"

### YOLO Mode Characteristics
**What Gets Bypassed**:
- ❌ TSDDR 2.0 Workflow validation
- ❌ Mandatory `.kiro/specs/` structure checks
- ❌ Quality gates enforcement
- ❌ Workflow documentation requirements
- ❌ Multi-stage validation processes

**What Remains Active**:
- ✅ Agent capability matching
- ✅ Technology stack detection
- ✅ Keyword analysis
- ✅ File type recognition
- ✅ Complexity assessment
- ✅ Performance optimization

## YOLO Selection Process

### Step 1: Rapid Analysis (< 1 second)
1. **Skip Workflow Validation** - No TSDDR 2.0 checks
2. **Fast Keyword Scan** - Parse user request for technology indicators
3. **Quick File Analysis** - Scan project structure for file types
4. **Instant Complexity Check** - Basic assessment without deep analysis
5. **Direct Technology Stack ID** - Immediate stack identification

### Step 2: Lightning Agent Scoring (< 0.5 seconds)
1. Calculate keyword match scores for each agent
2. Apply file type bonuses
3. Skip complexity handling validation
4. Generate confidence scores
5. Select highest scoring agent immediately

### Step 3: Instant Decision (< 0.2 seconds)
1. **High Confidence (≥0.6)**: Immediate agent activation
2. **Medium Confidence (0.4-0.6)**: 5-second user confirmation window
3. **Low Confidence (<0.4)**: Best guess with warning message
4. **No Fallback Delays**: Always proceed with best available option

---

## 🔴 CRITICAL PRIORITY: TRAE AI AGENT CONTROL SYSTEM

### **BẮT BUỘC** Trae AI Agent Selection System

- **🔴 HIGHEST PRIORITY**: **[Agent Selector System](./../agents/agent-selector-system.md)** - **BẮT BUỘC**: Hệ thống lựa chọn agent tự động cho Trae AI
- **🔴 HIGHEST PRIORITY**: **[Agent Selector YOLO Mode](./../agents/agent-selector-yolo-mode.md)** - **BẮT BUỘC**: Chế độ nhanh cho development đơn giản
- **🔴 CRITICAL**: **[Task Creation Workflow](./task-creation-workflow.md)** - **BẮT BUỘC**: Quy trình tạo task tự động với AI expansion
- **🔴 CRITICAL**: **[Task Update Workflow](./task-update-workflow.md)** - **BẮT BUỘC**: Quy trình tự động cập nhật task

### Task Update Trigger Keywords (Trae AI AUTO-DETECTION)

**BẮT BUỘC** tự động kích hoạt Task Update Workflow khi phát hiện:
- "cập nhật task" / "update task" / "refresh task"
- "kiểm tra task" / "check task status" / "task progress"
- "I'll analyze the current codebase and update the task list"

### Critical Task Creation Rules

1. **Auto-Expansion Pattern**: Mọi feature X phải tự động bao gồm Authentication, CRUD, UI, API, Validation, Security, Settings, Admin, Analytics và Related Features
2. **Task Hierarchy Template**: Feature → Sub-features → Tasks → Technical Details
3. **Mandatory Inclusions**: UI/UX, Backend, Security, Quality, Operations cho mỗi task
4. **Detail Level Requirements**: What, UI, API, Data, Logic, Errors cho mỗi task
5. **Smart Context Awareness**: Thích ứng với target users, platform, industry và regional needs

### Integration với Trae AI Agent System

- **BẮT BUỘC** sử dụng Agent Selector System làm engine chính cho mọi task
- **BẮT BUỘC** tự động phát hiện loại task và chọn agent phù hợp
- **BẮT BUỘC** sử dụng YOLO Mode cho rapid prototyping và simple tasks
- **BẮT BUỘC** validate TSDDR 2.0 Workflow khi cần thiết
- **BẮT BUỘC** tích hợp với Kiro system để output tasks vào `.kiro/specs/{project}/tasks.md`
- **BẮT BUỘC** maintain agent performance metrics và user feedback

---

## 🎯 Primary Rules Sources (MANDATORY)

### Trae AI Agent System Configuration
- **🔴 [Agent Selector Core System](./agent-selector-core.md)** - **BẮT BUỘC**: Hệ thống lựa chọn agent chính với AI analysis (TRỌNG SỐ CAO NHẤT)
- **🔴 [Agent Selector YOLO Mode](./../agents/agent-selector-yolo-mode.md)** - **BẮT BUỘC**: Chế độ nhanh cho simple tasks (TRỌNG SỐ CAO NHẤT)
- **🔴 [iOS Development Agent](./../agents/ios-development-agent.md)** - **BẮT BUỘC**: Agent chuyên iOS development (TRỌNG SỐ CAO)
- **🔴 [Trae AI Config](./trae-config.md)** - **BẮT BUỘC**: Cấu hình đặc thù cho Trae AI (TRỌNG SỐ CAO)
- **🔴 [Agent Workflow Mapping](./agents/agent-workflow-mapping.md)** - **BẮT BUỘC**: Mapping giữa agents và workflows (TRỌNG SỐ CAO)

### Core Development Rules
- **[Base Rules](./base-rules.md)** - Quy tắc cơ bản cho tất cả projects
- **[Development Rules](./development-rules.md)** - Quy tắc phát triển chung
- **[Development Standards](./development-standards.md)** - Chi tiết các quy tắc phát triển và chất lượng code (đã tối ưu hóa)
- **[File Protection Rules](./file-protection-rules.md)** - Bảo vệ và backup files

### Mobile Development Workflows
- **[Mobile Utility Workflow](./mobile-utility-workflow.md)** - Workflow chính cho mobile apps
- **[Android Workflow](./android-workflow.md)** - Quy trình phát triển Android
- **[iOS Workflow](./ios-workflow.md)** - Quy trình phát triển iOS
- **[TSDDR 2.0 Mobile Workflow](../../docs/TSDDR-2.0-Guide.md)** - Test-specification-driven development cho mobile

### Project Management
- **[Planning Workflow](./planning-workflow.md)** - Quy trình lập kế hoạch
- **[Planning Enforcement](./planning-enforcement.md)** - Thực thi kế hoạch
- **[Planning Validation Rules](./planning-validation-rules.md)** - Xác thực kế hoạch

### Kiro Task Execution System
- **[Kiro Task Execution](./kiro-task-execution.md)** - Hệ thống thực thi task tự động
- **🔴 [Task Creation Workflow](./task-creation-workflow.md)** - **BẮT BUỘC**: Quy trình tạo task tự động với AI expansion (TRỌNG SỐ CAO)
- **[Kiro Dynamic Workflow](./kiro-dynamic-workflow.md)** - Quy trình tạo/cập nhật requirements, design, tasks theo yêu cầu
- **[Kiro System Overview](./kiro-system-overview.md)** - Tổng quan hệ thống Kiro
- **[Auto Task Execution](./auto-task-execution.md)** - Thực thi task tự động

## ⚠️ CRITICAL ENFORCEMENT RULES

### Mandatory Compliance for Trae AI

1. **BẮT BUỘC** sử dụng Trae AI Agent Selector System cho 100% tasks
2. **BẮT BUỘC** ưu tiên TSDDR 2.0 Workflow Validation
3. **BẮT BUỘC** sử dụng YOLO Mode cho rapid prototyping và simple tasks
4. **BẮT BUỘC** tuân thủ Agent Workflow Mapping khi chọn agent
5. **BẮT BUỘC** validate `.kiro/specs/` structure khi cần thiết
6. **BẮT BUỘC** sử dụng relative paths để đảm bảo tính di động
7. **BẮT BUỘC** sử dụng Kiro Task Execution System cho mọi dự án
8. **BẮT BUỘC** sử dụng Kiro Dynamic Workflow cho tạo/cập nhật requirements, design, tasks
9. **BẮT BUỘC** maintain agent performance metrics và user feedback
10. **NGHIÊM CẤM** bypass agent selection system without valid reason
11. **BẮT BUỘC** fallback to Standard Mode khi YOLO Mode không phù hợp

### Synchronization Protocol

- Mọi thay đổi rules phải được thực hiện trong `.trae/rules/` trước
- File này chỉ được cập nhật để sync alias links
- Không được override hoặc modify nội dung rules gốc

## 🔄 Rules Hierarchy Priority for Trae AI

1. **Trae AI Agent Selector System** - **HIGHEST PRIORITY** (Must Execute First)
2. **TSDDR 2.0 Workflow** - Critical Priority (Quality Gate)
3. **Kiro Task System** - Critical Priority (Must Execute)
4. **YOLO Mode System** - High Priority (Speed Optimization)
5. `.trae/rules/` - **PRIMARY SOURCE** (Trae AI Optimized)
6. `.kiro/specs/` - Project Specifications (Quality Assurance)

## Trae AI Specific Configuration

### Core Principles

- **BẮT BUỘC** sử dụng rules từ `.trae/rules/` làm nguồn chính thức
- **BẮT BUỘC** sử dụng Trae AI Agent Selection System cho mọi task
- File này chỉ chứa alias links và không được chứa rules độc lập
- Mọi customization phải được thực hiện trong `.trae/rules/`

### Agent System Integration

- **BẮT BUỘC** tuân thủ Agent Workflow Mapping khi thực hiện tasks
- **BẮT BUỘC** sử dụng appropriate agent dựa trên keyword và context analysis
- **BẮT BUỘC** fallback to agent-selector khi không chắc chắn
- **BẮT BUỘC** maintain agent performance metrics và user satisfaction

### Quality Assurance

- **BẮT BUỘC** validate agent selection accuracy
- **BẮT BUỘC** monitor task completion rates per agent
- **BẮT BUỘC** gather user feedback for continuous improvement
- **BẮT BUỘC** ensure seamless integration với Trae AI interface

## 🎯 Kiro Task Execution System for Trae AI

### Core Features

- **Automatic Task Detection & Execution**: Tự động phát hiện và thực thi các Kiro tasks
- **Status Tracking**: Theo dõi trạng thái thực thi từng task
- **Dynamic Workflow**: Quy trình tạo/cập nhật requirements, design, tasks theo yêu cầu
- **Quality Gates**: Cổng chất lượng tại mỗi giai đoạn
- **Multi-Mode Operation**: Hỗ trợ CREATE_NEW, UPDATE_EXISTING, SUPPLEMENT_DATA, RESTRUCTURE

### Project Information

- Luôn kiểm tra .project-identity để biết cấu trúc và ngôn ngữ dự án
- Nếu chưa có file .project-identity hãy tạo và yêu cầu người dùng bổ sung thêm thông tin
- Task Status Legend: ✅ Completed, ⏳ In Progress, ❌ Not Started