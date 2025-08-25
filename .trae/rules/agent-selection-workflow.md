# Agent Selection Workflow

> **🎯 Intelligent Agent Selection & Task Routing**  
> Quy trình lựa chọn agent tự động với multi-factor analysis

## Overview

**Purpose**: Automatically select the most appropriate specialized agent based on task analysis  
**Method**: Multi-factor analysis with keyword matching, context analysis, and capability scoring  
**Output**: Optimal agent selection with confidence score and reasoning  
**Integration**: Seamless với Trae AI IDE và Kiro system

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

## Selection Process

### Step 1: Initial Analysis

**Workflow Validation** (Standard Mode Only):
- **MANDATORY FIRST CHECK**: Verify existence of TSDDR 2.0 Workflow documentation
- **Kiro Specs Validation**: Check for workflow implementation in `.kiro/specs/` directory
- **Critical Dependencies Check**: Verify Test Phase, Review Phase, Quality Gates
- **Action if Missing**: STOP agent selection, prompt user to create workflow

**Context Gathering**:
1. Parse user request for keywords and context
2. Analyze project structure and file types
3. Assess task complexity and scope
4. Identify primary technology stack

### Step 2: Agent Scoring

**Scoring Process**:
1. Calculate keyword match scores for each agent
2. Apply file type and context bonuses
3. Factor in complexity handling capabilities
4. Include performance and preference weights
5. Generate final confidence scores

**Agent Profiles Integration**:
- Load agent capabilities from `agent-profiles-detailed.md`
- Apply dynamic scoring based on current project context
- Consider agent availability and workload
- Factor in recent performance metrics

### Step 3: Selection Decision

**Decision Matrix**:
1. Select highest scoring agent if confidence > 0.6
2. Request clarification if all scores < 0.4
3. Provide alternative suggestions for medium confidence
4. Log selection reasoning and confidence level

**Output Format**:
```json
{
  "selected_agent": "ios-development-agent",
  "confidence_score": 0.85,
  "reasoning": "High Swift keyword match, .xcodeproj detected, iOS complexity suitable",
  "alternatives": ["mobile-cross-platform-agent"],
  "workflow_validated": true
}
```

## Fallback Mechanisms

**Multi-Agent Tasks**: Route to primary agent with collaboration notes  
**Unclear Context**: Request additional information before selection  
**Low Confidence**: Present top 2-3 options for user choice  
**New Technologies**: Default to most relevant existing agent with notes

**Emergency Fallback**:
- If all agents score < 0.2, activate General Development Agent
- Provide detailed reasoning for fallback selection
- Request user confirmation before proceeding
- Log incident for algorithm improvement

## Performance Optimization

### Caching Strategy

**Selection Cache**:
- Store recent selections and patterns
- Cache project-specific agent preferences
- Maintain technology stack mappings
- Optimize for repeated similar requests

**Performance Metrics**:
- Selection accuracy tracking
- Response time monitoring
- User satisfaction scoring
- Agent performance correlation

### Learning Capabilities

**Adaptive Scoring**:
- Update weights based on success/failure feedback
- Learn from user override patterns
- Adjust algorithm based on usage patterns
- Improve keyword detection accuracy

**Continuous Improvement**:
- Weekly performance reviews
- Monthly algorithm adjustments
- Quarterly capability assessments
- Annual system optimization

## Integration Points

### Input Sources

**Primary Inputs**:
- User natural language requests
- Project file structure analysis
- Previous conversation context
- User preference history

**Context Enhancement**:
- Git commit history analysis
- Recent file modification patterns
- Active development branches
- Team collaboration patterns

### Output Targets

**Agent Activation**:
- Selected agent activation with context
- Task routing to appropriate workflow
- Performance tracking initialization
- User notification and confirmation

**System Integration**:
- Kiro system task assignment
- Trae AI workflow coordination
- Development environment setup
- Progress monitoring activation

## Quality Assurance

### Validation Rules

**Pre-Selection Validation**:
- Minimum confidence threshold (0.4)
- Maximum response time (< 2 seconds)
- Fallback agent availability
- Selection reasoning clarity

**Post-Selection Validation**:
- Agent capability verification
- Task-agent compatibility check
- Resource availability confirmation
- User satisfaction prediction

### Error Handling

**Common Error Scenarios**:
- **Workflow Missing**: Immediate stop with guided setup instructions
- **Kiro Specs Missing**: Auto-prompt for project structure creation
- **Agent Unavailable**: Automatic fallback to secondary choice
- **Low Confidence**: User confirmation required
- **System Timeout**: Default to most recent successful selection

**Recovery Procedures**:
- Graceful degradation for edge cases
- Default agent selection for timeouts
- User override capabilities
- Selection history logging
- Incident reporting and analysis

## Monitoring & Analytics

### Performance Metrics

**Selection Accuracy**:
- Correct agent selection rate
- User override frequency
- Task completion success rate
- Time to completion correlation

**System Performance**:
- Average selection time
- Cache hit rate
- Algorithm efficiency
- Resource utilization

### Reporting Dashboard

**Real-time Metrics**:
- Current selection accuracy
- Active agent workload
- System response times
- User satisfaction scores

**Historical Analysis**:
- Selection pattern trends
- Agent performance evolution
- Technology stack preferences
- Optimization opportunities

## Advanced Features

### Machine Learning Integration

**Pattern Recognition**:
- Automatic keyword discovery
- Context similarity detection
- User behavior prediction
- Technology trend adaptation

**Predictive Analytics**:
- Task complexity prediction
- Agent workload forecasting
- Success probability estimation
- Resource requirement planning

### Context Awareness Enhancement

**Environmental Factors**:
- Time of day preferences
- Team availability patterns
- Project deadline pressure
- Resource constraints

**Collaborative Intelligence**:
- Multi-agent coordination
- Task dependency analysis
- Workload balancing
- Knowledge sharing optimization

## Configuration

### Customization Options

**Scoring Weights**:
- Adjustable factor weights
- Project-specific tuning
- Team preference settings
- Performance-based optimization

**Threshold Settings**:
- Confidence level adjustments
- Response time limits
- Cache duration settings
- Fallback trigger points

### Integration Settings

**Trae AI Configuration**:
- Agent endpoint mappings
- Workflow integration points
- Performance monitoring setup
- User interface customization

**Kiro System Integration**:
- Spec file locations
- Task format preferences
- Progress tracking setup
- Reporting configurations

This workflow ensures optimal agent selection while maintaining high performance and user satisfaction.