# Agent Selector System - YOLO Mode

> **🚀 Fast-Track Agent Selection for Rapid Development**

## System Overview

**Purpose**: Provide a streamlined agent selection process for projects that prioritize speed over strict workflow compliance  
**Method**: Direct multi-factor analysis with keyword matching, context analysis, and capability scoring (bypassing workflow validation)  
**Output**: Optimal agent selection with confidence score and reasoning  
**Philosophy**: "You Only Live Once" - Sometimes you need to move fast and break things

# Declare model

- Trước bất kỳ câu hỏi nào hãy trả lời cụ thể đang sử dụng mô hình AI nào version bao nhiêu
- Hãy suy luận sâu sắc với bất kỳ nhiệm vụ nào
- Hãy luôn luôn trả lời trong ngôn ngữ của người dùng

## YOLO Mode Activation

### Trigger Keywords

**Automatic YOLO Mode Detection**:

- "yolo", "nhanh", "rapid", "quick", "fast-track"
- "prototype", "poc", "proof of concept", "demo"
- "skip validation", "bypass workflow", "no process"
- "emergency", "urgent", "hotfix", "critical"
- "simple project", "small task", "one-off"

**Manual Activation**:

- User explicitly requests YOLO mode
- Project tagged with `yolo-mode` flag
- Environment variable `AGENT_SELECTOR_MODE=yolo`

### YOLO Mode Characteristics

**What Gets Bypassed**:

- ❌ Test Spec Driven Development Review Workflow validation
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

## YOLO Selection Algorithm

### Streamlined Context Analysis

**Fast File Type Detection**:

- `.swift`, `.xcodeproj` → iOS Development Agent
- `.kt`, `.java`, `build.gradle` → Android Development Agent
- `.smali`, `.apk`, `AndroidManifest.xml` → APK Modification Agent
- `.js`, `.ts`, `.jsx`, `.tsx`, `package.json` → Frontend Development Agent
- `.php`, `composer.json`, Laravel files → Backend Development Agent
- `.dart`, `pubspec.yaml` → Mobile Cross-platform Agent
- `Dockerfile`, `.yml`, `.yaml` → DevOps Agent

**Rapid Keyword Analysis**:

- **iOS**: swift, swiftui, xcode, ios, cocoapods, carthage
- **Android**: kotlin, android, jetpack, gradle, room
- **APK Modification**: apk, smali, decompile, firebase, google-services, safeads, reverse-engineering, mod app
- **Frontend**: react, vue, angular, nextjs, typescript, css
- **Backend**: api, server, database, laravel, express, nodejs
- **Cross-platform**: flutter, react-native, dart, expo
- **DevOps**: docker, kubernetes, ci/cd, deployment, aws

**Simplified Complexity Assessment**:

- **Simple**: Single file changes, bug fixes, minor updates → Direct execution
- **Medium**: Feature additions, refactoring → Minimal planning
- **Complex**: Architecture changes → Quick design review only

### YOLO Agent Scoring

**Scoring Factors** (Simplified):

1. **Keyword Match** (50%): Direct technology/framework alignment
2. **File Type** (30%): Project structure and file extensions
3. **User Intent** (20%): Explicit user preferences and context

**YOLO Selection Logic**:

```
if (score >= 0.6) → Immediate Selection & Execution
if (score >= 0.4) → Quick Confirmation (5 seconds timeout)
if (score < 0.4) → Best Guess Selection with Warning
```

## YOLO Selection Process

### Step 1: Rapid Analysis (< 1 second)

1. **Skip Workflow Validation** - No Test Spec Driven Development Review checks
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

## YOLO Mode Features

### Speed Optimizations

**Performance Targets**:

- Total selection time: < 2 seconds
- Agent activation: < 1 second
- No blocking validations
- Parallel processing where possible

**Caching Strategy**:

- Aggressive caching of recent selections
- Pre-computed agent scores for common patterns
- Instant recall for similar project structures

### Risk Management

**YOLO Warnings**:

- "⚠️ YOLO Mode Active - Workflow validations bypassed"
- "🚀 Fast-track mode - Quality gates disabled"
- "⚡ Rapid development mode - Proceed with caution"

**Safety Nets**:

- Basic syntax validation still active
- Critical error prevention remains
- Rollback capabilities maintained
- Emergency stop functionality

### User Experience

**YOLO Indicators**:

- 🚀 YOLO mode badge in UI
- Speed metrics display
- Simplified progress indicators
- Minimal confirmation dialogs

**Quick Actions**:

- One-click agent switching
- Instant task execution
- Skip confirmation for low-risk operations
- Batch operation support

## YOLO vs Standard Mode Comparison

| Feature             | Standard Mode | YOLO Mode      |
| ------------------- | ------------- | -------------- |
| Workflow Validation | ✅ Required   | ❌ Bypassed    |
| Quality Gates       | ✅ Enforced   | ❌ Optional    |
| Selection Time      | 5-10 seconds  | < 2 seconds    |
| Documentation Check | ✅ Mandatory  | ❌ Skipped     |
| Error Prevention    | 🔒 Strict     | ⚡ Minimal     |
| User Confirmation   | 🐌 Multiple   | ⚡ Single/None |
| Rollback Safety     | 🛡️ Full       | 🔧 Basic       |

## Integration with Existing System

### Mode Detection

```javascript
// Pseudo-code for mode detection
function detectSelectionMode(userInput, projectContext) {
  const yoloKeywords = [
    "yolo",
    "rapid",
    "quick",
    "fast",
    "urgent",
    "prototype",
  ];
  const hasYoloKeyword = yoloKeywords.some((keyword) =>
    userInput.toLowerCase().includes(keyword)
  );

  const isSimpleProject = !projectContext.hasComplexStructure;
  const userPreference = getUserPreference("agent_selection_mode");

  if (hasYoloKeyword || userPreference === "yolo" || isSimpleProject) {
    return "YOLO_MODE";
  }

  return "STANDARD_MODE";
}
```

### Fallback Strategy

**YOLO → Standard Fallback**:

- If YOLO selection fails critically
- User explicitly requests standard mode
- Project complexity exceeds YOLO thresholds
- Critical errors detected during execution

**Standard → YOLO Upgrade**:

- User requests speed optimization
- Simple project detected during standard flow
- Time-sensitive development needs
- Prototype/demo development mode

## YOLO Mode Use Cases

### Perfect for YOLO Mode

**Rapid Prototyping**:

- Quick proof of concepts
- Demo applications
- Hackathon projects
- Learning exercises

**Simple Tasks**:

- Bug fixes
- Minor feature additions
- Configuration changes
- Documentation updates

**Time-Sensitive Work**:

- Emergency hotfixes
- Critical production issues
- Deadline-driven development
- Client demos

### Not Suitable for YOLO Mode

**Production Systems**:

- Enterprise applications
- Financial systems
- Healthcare applications
- Security-critical software

**Complex Projects**:

- Multi-team development
- Long-term maintenance projects
- Regulatory compliance required
- High-stakes deployments

## Configuration Options

### YOLO Mode Settings

```yaml
# .trae/config/yolo-mode.yml
yolo_mode:
  enabled: true
  auto_detect: true
  confirmation_timeout: 5 # seconds
  max_selection_time: 2 # seconds
  warning_level: "minimal" # minimal, standard, verbose

  triggers:
    keywords: ["yolo", "rapid", "quick", "fast", "urgent"]
    file_patterns: ["prototype/*", "demo/*", "poc/*"]
    project_size_threshold: "small" # small, medium, large

  safety:
    enable_basic_validation: true
    allow_critical_operations: false
    require_confirmation_for: ["delete", "deploy"]
```

### Environment Variables

```bash
# Enable YOLO mode globally
export AGENT_SELECTOR_MODE=yolo

# Set YOLO timeout
export YOLO_SELECTION_TIMEOUT=2

# YOLO warning level
export YOLO_WARNING_LEVEL=minimal
```

## Monitoring and Analytics

### YOLO Mode Metrics

**Performance Metrics**:

- Average selection time
- Agent accuracy rate
- User satisfaction scores
- Error rates by project type

**Usage Analytics**:

- YOLO mode adoption rate
- Most common YOLO triggers
- Success rate by agent type
- Time saved vs standard mode

### Quality Tracking

**Success Indicators**:

- Task completion rate
- Code quality metrics (when available)
- User feedback scores
- Rollback frequency

**Risk Indicators**:

- Critical error frequency
- Security issue detection
- Performance regression alerts
- User-reported problems

## Future Enhancements

### AI-Powered YOLO

**Smart Pattern Recognition**:

- Learn from successful YOLO selections
- Predict optimal agent for new projects
- Auto-adjust confidence thresholds
- Personalized YOLO recommendations

**Adaptive Risk Assessment**:

- Dynamic safety level adjustment
- Context-aware validation skipping
- Intelligent warning systems
- Predictive error prevention

### Integration Improvements

**IDE Integration**:

- YOLO mode toggle in IDE
- Visual speed indicators
- One-click agent switching
- Integrated performance metrics

**Team Collaboration**:

- Team YOLO preferences
- Shared YOLO configurations
- Collaborative agent selection
- Team performance analytics

---

**Activation**: Triggered by YOLO keywords or manual selection  
**Integration**: Seamless fallback to/from standard Agent Selector System  
**Philosophy**: Speed and pragmatism over process perfection  
**Motto**: "Move fast, select smart, code faster! 🚀"
