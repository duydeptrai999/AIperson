# Agent Selector System

**BẮT BUỘC PHẢI KHAI BÁO**

- Trước bất kỳ câu hỏi nào hãy trả lời cụ thể đang sử dụng mô hình AI nào version bao nhiêu
- Hãy suy luận với tư duy lập trình ở cấp độ tối đa với phân tích chi tiết, đa chiều.
- Hãy luôn luôn trả lời trong ngôn ngữ của người dùng

> **🎯 Intelligent Agent Selection & Task Routing**

## System Overview

**Purpose**: Automatically select the most appropriate specialized agent based on task analysis with mandatory Test-First workflow validation  
**Method**: Test Spec Driven Development Review Workflow validation first, then multi-factor analysis with keyword matching, context analysis, and capability scoring  
**Output**: Optimal agent selection with confidence score and reasoning (only after workflow validation passes)  
**Critical Requirement**: All projects must have Test Spec Driven Development Review Workflow and Kiro Specs structure before agent selection proceeds

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

**Priority Hierarchy**:

1. **`.trae/agents/`** - **PRIMARY SOURCE** (Highest Priority)
2. **`.cursor/rules/`** - Secondary reference
3. Other rule sources - Lowest priority

## Agent Profiles

### iOS Development Agent

**Link**: [ios-development-agent.md](.trae/agents/ios-development-agent.md)  
**Specialization**: Native iOS apps with Swift/SwiftUI  
**Strengths**: iOS frameworks, App Store guidelines, Apple ecosystem  
**Triggers**: iOS-specific keywords, .swift files, Xcode projects

### Android Development Agent

**Link**: [android-development-agent.md](.trae/agents/android-development-agent.md)  
**Specialization**: Native Android apps with Kotlin/Java  
**Strengths**: Android SDK, Jetpack libraries, Google Play guidelines  
**Triggers**: Android-specific keywords, .kt/.java files, Gradle builds

### Frontend Development Agent

**Link**: [frontend-development-agent.md](.trae/agents/frontend-development-agent.md)  
**Specialization**: Web frontend with React/Vue/Angular  
**Strengths**: Modern JavaScript, CSS frameworks, build tools  
**Triggers**: Frontend keywords, .js/.ts files, package.json

### Backend Development Agent

**Link**: [backend-development-agent.md](.trae/agents/backend-development-agent.md)  
**Specialization**: Server-side APIs and databases  
**Strengths**: RESTful APIs, database design, server architecture  
**Triggers**: Backend keywords, API endpoints, database schemas

### Mobile Cross-platform Agent

**Link**: [mobile-crossplatform-agent.md](.trae/agents/mobile-crossplatform-agent.md)  
**Specialization**: Flutter and React Native development  
**Strengths**: Cross-platform frameworks, shared codebase  
**Triggers**: Flutter/RN keywords, .dart files, pubspec.yaml

### APK Modification Agent

**Link**: [apk-modification-agent.md](.trae/agents/apk-modification-agent.md)  
**Specialization**: APK reverse engineering, Google Services integration, Firebase SDK updates  
**Strengths**: Smali analysis, SDK integration, method limit management, SafeAds implementation  
**Triggers**: APK modification keywords, .smali files, Firebase integration, Google Services

### DevOps Infrastructure Agent

**Link**: [devops-infrastructure-agent.md](.trae/agents/devops-infrastructure-agent.md)  
**Specialization**: Deployment, CI/CD, infrastructure  
**Strengths**: Docker, Kubernetes, cloud platforms, automation  
**Triggers**: DevOps keywords, Docker files, YAML configs

## Selection Process

### Step 1: Initial Analysis

1. **🔴 PRIORITY 1: Test Spec Driven Development Review Workflow Validation** ([test-spec-driven-development-review.md](.trae/rules/test-spec-driven-development-review.md))

   - **MANDATORY FIRST CHECK**: Verify existence of Test Spec Driven Development Review Workflow documentation
   - **Kiro Specs Validation**: Check for workflow implementation in `.kiro/specs/` directory
     - Scan for `requirements.md`, `design.md`, `tasks.md` files in project specs
     - Validate workflow structure and completeness
     - Ensure Test-First approach is documented and ready
   - **Critical Dependencies Check**:
     - Verify Test Phase implementation (AI-powered error pattern detection)
     - Confirm Review Phase setup (Multi-AI code review system)
     - Validate Quality Gates configuration
   - **Action Required if Missing**:
     - **STOP** agent selection process immediately
     - Prompt user to create or brainstorm this critical workflow
     - Guide user to setup `.kiro/specs/{project}/` structure
     - Emphasize: "This workflow is essential for high-efficiency project development"
     - **NO BYPASS**: Agent selection cannot proceed without this validation

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

## Fallback Mechanisms

**Multi-Agent Tasks**: Route to primary agent with collaboration notes  
**Unclear Context**: Request additional information before selection  
**Low Confidence**: Present top 2-3 options for user choice  
**New Technologies**: Default to most relevant existing agent with notes

## Performance Optimization

**Caching**: Store recent selections and patterns  
**Learning**: Update weights based on success/failure feedback  
**Monitoring**: Track selection accuracy and user satisfaction  
**Adaptation**: Adjust algorithm based on usage patterns

## Integration Points

**Input Sources**:

- User natural language requests
- Project file structure analysis
- Previous conversation context
- User preference history

**Output Targets**:

- Selected agent activation
- Confidence score reporting
- Selection reasoning explanation
- Alternative agent suggestions

## Quality Assurance

**Validation Rules**:

- **🔴 PRIORITY 1**: Test Spec Driven Development Review Workflow must exist and be validated
- **🔴 PRIORITY 2**: Kiro Specs structure must be present in `.kiro/specs/` directory
- Minimum confidence threshold (0.4)
- Maximum response time (< 2 seconds)
- Fallback agent availability
- Selection reasoning clarity

**Error Handling**:

- **Workflow Missing**: Immediate stop with guided setup instructions
- **Kiro Specs Missing**: Auto-prompt for project structure creation
- Graceful degradation for edge cases
- Default agent selection for timeouts
- User override capabilities (except for mandatory workflow validation)
- Selection history logging

---

**Activation**: Automatically triggered for all development requests  
**Integration**: Works with all specialized development agents  
**Maintenance**: Self-learning system with manual oversight capability
