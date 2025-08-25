# Project Identity Enforcement Workflow

## Overview

Workflow này đảm bảo rằng AI assistant luôn kiểm tra và tuân thủ thông tin trong `.project-identity` trước khi thực hiện bất kỳ nhiệm vụ nào.

## 🔴 MANDATORY RULES

### Pre-Task Identity Check

**BẮT BUỘC thực hiện trước mọi nhiệm vụ:**

1. **Read .project-identity file**
2. **Analyze project context**
3. **Load appropriate workflow rules**
4. **Apply project-specific constraints**

### Identity Check Protocol

#### Step 1: File Validation
```
IF .project-identity exists:
  → Proceed to Step 2
ELSE:
  → Create .project-identity template
  → Request user to fill required information
  → STOP until completed
```

#### Step 2: Context Analysis
**Extract and understand:**
- `projectType`: Determines platform-specific rules
- `projectStage`: Defines current workflow phase
- `mainLanguages`: Programming languages in use
- `mainFrameworks`: Technology stack
- `keyFeatures`: Core functionality
- `projectLifecycle`: Current stage requirements

#### Step 3: Workflow Rules Loading
**Load rules in this order:**
1. `coreRules.always_applied` (mandatory for all tasks)
2. `platformSpecificRules` based on `projectType`
3. Stage-specific rules from `projectLifecycle`
4. Integration rules if features are enabled

#### Step 4: Enforcement Validation
**Check constraints:**
- Verify stage progression rules
- Validate new project detection triggers
- Ensure no stage skipping
- Apply workflow enforcement rules

## Workflow Rules Mapping

### By Project Type

**Android Projects:**
```
Required Rules:
- .trae/rules/android-workflow.md
- .trae/rules/android-code-deduplication.md
- .trae/rules/tdd-mobile-workflow.md
- .trae/rules/mobile-utility-workflow.md
```

**iOS Projects:**
```
Required Rules:
- .trae/rules/ios-workflow.md
- .trae/rules/tdd-mobile-workflow.md
- .trae/rules/mobile-utility-workflow.md
```

**Web Projects:**
```
Required Rules:
- .trae/rules/frontend-rules.md
- .trae/rules/backend-rules-optimized.md
- .trae/rules/api-integration-rules.md
```

**Flutter Projects:**
```
Required Rules:
- .trae/rules/mobile-utility-workflow.md
- .trae/rules/tdd-mobile-workflow.md
```

### By Project Stage

**Stage 1 - Brainstorm:**
```
Required Rules:
- .trae/rules/brainstorm-workflow.md
- .trae/rules/brainstorm-detailed-workflow.md
Blocking: Cannot proceed to development without brainstorm completion
```

**Stage 2 - Setup:**
```
Required Rules:
- .trae/rules/project-creation-workflow.md
- .trae/rules/planning-workflow.md
- .trae/rules/tech-stack-selection.md
```

**Stage 3 - Development:**
```
Required Rules:
- Platform-specific development rules
- .trae/rules/development-rules.md
- .trae/rules/tdd-guidelines.md
```

## Context7 Integration (Enhanced)

### Memory Check Requirements
**Before starting any task:**
1. **BẮT BUỘC**: Kích hoạt Context7 Auto-Check Workflow
2. **BẮT BUỘC**: Search Context7 for project-related memories
3. **BẮT BUỘC**: Resolve library IDs cho các tech stack components
4. **BẮT BUỘC**: Thu thập documentation và best practices
5. **BẮT BUỘC**: Apply lessons learned from similar projects
6. **BẮT BUỘC**: Check for existing solutions or patterns
7. **BẮT BUỘC**: Validate architecture choices với industry standards
8. **BẮT BUỘC**: Update memory with new insights
9. **MỚI**: Implement error handling cho Context7 failures
10. **MỚI**: Cache Context7 results để improve performance

### Memory Categories to Check
- Project architecture decisions
- Technology stack experiences
- Common issues and solutions
- Best practices for project type
- Integration patterns
- Library compatibility matrices
- Performance optimization patterns

## Enforcement Actions

### Blocking Conditions
**STOP execution if:**
- `.project-identity` is missing or incomplete
- Project stage requirements not met
- Required workflow rules not loaded
- Stage progression rules violated

### Warning Conditions
**Warn user if:**
- Project type doesn't match detected technology
- Stage seems inconsistent with project state
- Missing recommended integrations
- Outdated project information

### Auto-Update Triggers
**Automatically update `.project-identity` when:**
- New technologies are detected
- Project stage naturally progresses
- New features are implemented
- Integration status changes

## Error Handling

### Missing .project-identity
```
Action: Create template file
Message: "🚫 Project identity file missing. Creating template..."
Next: Request user to complete required fields
```

### Incomplete Information
```
Action: Identify missing fields
Message: "⚠️ Project identity incomplete. Missing: [fields]"
Next: Request specific information
```

### Stage Violation
```
Action: Block execution
Message: "🚫 Cannot skip project stages. Current: [stage], Required: [required]"
Next: Guide user to complete current stage
```

## Integration Points

### With Other Workflows
- **Planning Workflow**: Validates stage progression
- **Development Rules**: Applies technology-specific constraints
- **Resource Management**: Allocates resources based on project needs

### With MCP Tools
- **Context7**: Memory and experience lookup
- **Memory Bank**: Project knowledge storage
- **Telegram Notifications**: Status updates if enabled

## Success Criteria

**Task can proceed when:**
- ✅ `.project-identity` exists and is complete
- ✅ All required workflow rules are loaded
- ✅ Project context is understood
- ✅ Stage requirements are validated
- ✅ No blocking conditions exist

## Monitoring and Logging

### Log Events
- Identity check completion
- Workflow rules loaded
- Stage transitions
- Enforcement actions taken
- Auto-updates performed

### Metrics to Track
- Identity check compliance rate
- Stage progression accuracy
- Rule loading success rate
- User satisfaction with guidance

## Best Practices

### For AI Assistant
1. Always check identity before any substantial work
2. Provide clear guidance when blocking
3. Suggest next steps for resolution
4. Keep project information updated
5. Use appropriate workflow rules consistently

### For Users
1. Keep `.project-identity` updated
2. Follow stage progression naturally
3. Provide complete project information
4. Review and confirm auto-updates
5. Use project-appropriate workflows

## Troubleshooting

### Common Issues

**Issue**: AI not following project-specific rules
**Solution**: Verify `.project-identity` completeness and rule loading

**Issue**: Blocked from proceeding with task
**Solution**: Check stage requirements and complete prerequisites

**Issue**: Inconsistent behavior across sessions
**Solution**: Ensure `.project-identity` is committed to repository

**Issue**: Wrong workflow rules applied
**Solution**: Verify `projectType` and `projectStage` accuracy

---

**Workflow Version**: 1.0
**Last Updated**: 2024-12-19
**Compatibility**: All project types
**Dependencies**: .project-identity file, Context7 MCP