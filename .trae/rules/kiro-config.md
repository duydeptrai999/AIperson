---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.179Z"
---

#kiro-config
---
description: Kiro-specific configuration for info-hub integration and task execution optimization
globs: "*.md,*.md"
alwaysApply: false
---

# Kiro Configuration for Project Identity Integration

## Project Identity #integration-rules

### Automatic Checking
- **BẮT BUỘC** check `.project-identity` before ANY task operation
- **BẮT BUỘC** read "currentWorkingStatus" to understand active tasks
- **BẮT BUỘC** update status when claiming tasks
- **BẮT BUỘC** mark tasks as completed in currentWorkingStatus after execution
- **NGHIÊM CẤM** execute tasks already assigned to other tools

### Update Protocol for Kiro
```markdown
## Before starting tasks
1. Check .project-identity for currentWorkingStatus
2. Verify no other tool is working on same task
3. Update currentWorkingStatus with JSON:
   {
     "aiTool": "Kiro",
     "workIntent": "[Task ID and description]",
     "targetFiles": ["file1.ext", "file2.ext"],
     "status": "in_progress",
     "lastUpdate": "[ISO timestamp]"
   }

## After completing tasks
1. Update task status in .kiro/specs/{project}/tasks.md
2. Update currentWorkingStatus with status: "completed"
3. Clean up completed entries after validation
```

## Kiro Task System Enhancement

### Task Detection & Validation
- **BẮT BUỘC** validate task format before execution
- **BẮT BUỘC** check dependencies are resolved
- **BẮT BUỘC** ensure acceptance criteria are testable
- **BẮT BUỘC** validate EARS requirements format

### Task Format Validation
```markdown
## Valid Kiro Task Structure
- Task ID: [TASK-XXX] format
- Status: Not Started|In Progress|Completed|Blocked
- Priority: High|Medium|Low
- Dependencies: List of task IDs or "None"
- EARS Requirement: Must follow EARS syntax
- Acceptance Criteria: Minimum 2 testable criteria
```

## Dynamic Workflow Integration

### Automatic Dynamic Detection
- **BẮT BUỘC** check for existence of required files:
  - `.kiro/specs/{project}/requirements.md`
  - `.kiro/specs/{project}/design.md`
  - `.kiro/specs/{project}/tasks.md`
- **BẮT BUỘC** trigger dynamic workflow if any missing
- **KHUYẾN NGHỊ** use Kiro tools first before dynamic workflow

### Dynamic Execution
```markdown
## Dynamic Workflow Steps
1. Detect missing files
2. Update .project-identity currentWorkingStatus with dynamic status
3. Execute Brainstorm → Requirements → Design → Tasks
4. Validate generated files
5. Update currentWorkingStatus with completion status
```

## Task Execution Optimization

### Batch Processing
```markdown
## Batch Task Execution
- Group related tasks by module/feature
- Execute in dependency order
- Validate after each batch
- Commit changes per batch
```

### Parallel Task Detection
```markdown
## Parallel #execution-rules
- Identify tasks with no dependencies
- Check if tasks modify different files
- Execute in parallel if no conflicts
- Update status for all parallel tasks
```

## Integration with Spec-Driven Development

### Planning Phase Integration
```markdown
## Task Planning with Spec-Driven
1. Extract EARS requirements from task
2. Break down into atomic actions
3. Plan test scenarios
4. Document reasoning before execution
```

### Execution Phase Integration
```markdown
## Spec-Driven Execution
1. Execute ONE atomic change
2. Validate the change
3. Commit with task reference
4. Update task progress
5. Check acceptance criteria
```

## Error Handling Patterns

### Task Execution Failures
```markdown
## Failure Recovery Protocol
1. Log error with context in .project-identity currentWorkingStatus
2. Mark task status as "blocked" with reason in workIntent
3. Create recovery task if needed
4. Update currentWorkingStatus with blockage details
```

### Dependency Resolution
```markdown
## Dependency Handling
1. Detect circular dependencies
2. Suggest dependency resolution
3. Update task order if needed
4. Document changes in .project-identity notes
```

## Performance Tracking

### Task Metrics
```markdown
## Kiro Performance Metrics
- Tasks completed per session
- Average task completion time
- Dependency resolution time
- Dynamic workflow frequency
- Task failure rate

## Update .project-identity with metrics weekly
```

## Conflict Detection with Other Tools

### File-Level Conflicts
```markdown
## Conflict Prevention
1. Check files being modified by task
2. Compare with .project-identity "currentWorkingStatus"
3. If conflict detected:
   - Wait for other tool to complete
   - Or negotiate task split
   - Update currentWorkingStatus with resolution
```

### Task-Level Conflicts
```markdown
## Task Conflict Resolution
1. Detect overlapping task scopes
2. Check task dependencies
3. Coordinate through .project-identity updates
4. Split or merge tasks if needed
```

## Integration Points

### With Auto-Task-Execution
- Use Kiro task format for all executions
- Apply surgical precision from spec-driven
- Update both Kiro tasks and .project-identity

### With Planning Workflow
- Generate Kiro-compatible tasks
- Include EARS requirements
- Define clear acceptance criteria

### With Context7
- Validate architecture decisions
- Check best practices for task implementation
- Cache validation results

## Monitoring and Alerts

### Task Queue Monitoring
```markdown
## Queue Health Checks
- Alert if tasks pending > 24 hours
- Warn if blocked tasks > 3
- Notify if dynamic workflow triggered > 2/day
```

### Performance Alerts
```markdown
## Performance Thresholds
- Task execution time > 2x estimate
- Failure rate > 20%
- Dependency conflicts > 5/day
```

## Testing Configuration

### Task Validation Tests
```markdown
## Automated Task Validation
- Format compliance check
- Dependency graph validation
- Acceptance criteria testability
- EARS requirement syntax check
```

### Integration Tests
```markdown
## Kiro Integration Testing
- Project identity update verification
- Concurrent task execution
- Dynamic workflow trigger
- Conflict detection accuracy
```

## Debugging Support

### Kiro Debug Mode
```markdown
## Enable debug logging
KIRO_DEBUG=true

## Debug output includes:
- Task parsing details
- Dependency resolution steps
- Project identity sync operations
- Conflict detection logs
```

### Task Execution Trace
```markdown
## Trace task execution
1. Log each atomic action
2. Record validation results
3. Track state changes
4. Document decision points
```

---

**Note**: This configuration ensures Kiro task system works seamlessly with .project-identity synchronization while maintaining high quality through spec-driven development principles and dynamic workflow mechanisms.