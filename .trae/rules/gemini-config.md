---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.183Z"
---

#gemini-config
---
description: Gemini-specific configuration for info-hub integration and structured output optimization
globs: "*.md,*.md"
alwaysApply: false
---

# Gemini Configuration for Project Identity Integration

## Project Identity #integration-rules

### Automatic Checking
- **BẮT BUỘC** check `.project-identity` before ANY response generation
- **BẮT BUỘC** read "currentWorkingStatus" to check for conflicts
- **BẮT BUỘC** verify no conflicts with other AI tools
- **BẮT BUỘC** update status after significant completions
- **NGHIÊM CẤM** duplicate work already assigned to other tools

### Update Protocol for Gemini
```markdown
## Before starting work
1. Check .project-identity for currentWorkingStatus
2. Look for active tasks and potential conflicts
3. Update currentWorkingStatus with JSON:
   {
     "aiTool": "Gemini",
     "workIntent": "[Clear description]",
     "targetFiles": ["file1.ext", "file2.ext"],
     "status": "in_progress",
     "lastUpdate": "[ISO timestamp]"
   }

## After completing work
1. Update currentWorkingStatus with status: "completed"
2. Document any new issues in projectStage notes
3. Clean up completed entries after validation
```

## Gemini-Specific Optimizations

### Structured Output Enhancement
- **BẮT BUỘC** use structured thinking patterns
- **BẮT BUỘC** provide clear section headers
- **BẮT BUỘC** use bullet points for clarity
- **BẮT BUỘC** include code examples when relevant

### Response Format Optimization
```markdown
## Gemini Response Structure
1. **Understanding**: Restate the problem
2. **Analysis**: Break down requirements
3. **Solution**: Provide clear implementation
4. **Validation**: Check against criteria
5. **Next Steps**: Clear action items
```

## Context Management

### Token Optimization
```markdown
## Efficient Context Usage
- Prioritize recent context over historical
- Summarize long conversations periodically
- Focus on actionable information
- Remove redundant explanations
```

### Multi-Turn Conversation Handling
```markdown
## Conversation State Management
1. Track conversation context across turns
2. Reference previous decisions explicitly
3. Maintain consistency in approach
4. Update info-hub after major decisions
```

## Integration with Spec-Driven Development

### Gemini Spec-Driven Patterns
```markdown
## Spec-Driven Response Flow
1. **Clarify Requirements**: Ask targeted questions
2. **Plan Approach**: Document strategy
3. **Execute Systematically**: Step-by-step implementation
4. **Validate Results**: Check against specs
```

### Code Generation Patterns
```markdown
## Gemini Code Generation
- Generate complete, runnable code blocks
- Include inline documentation
- Provide usage examples
- Explain design decisions
```

## Error Handling Patterns

### Common Gemini Issues
```markdown
## Known Issues & Mitigations
1. **Context Loss**: 
   - Mitigation: Frequent .project-identity updates
   - Recovery: Re-read project context

2. **Response Truncation**:
   - Mitigation: Break into smaller chunks
   - Recovery: Continue from last point

3. **Inconsistent Formatting**:
   - Mitigation: Use templates
   - Recovery: Reformat on detection
```

### Error Recovery Protocol
```markdown
## Recovery Steps
1. Detect error or inconsistency
2. Log issue in .project-identity currentWorkingStatus
3. Attempt self-correction
4. If failed, mark status as "blocked"
5. Suggest alternative approach
```

## Performance Optimization

### Response Time Optimization
```markdown
## Speed Improvements
- Use cached context when available
- Minimize redundant processing
- Parallelize independent tasks
- Batch related operations
```

### Quality Metrics
```markdown
## Gemini Quality Tracking
- Response completeness rate
- Code correctness rate
- Task completion time
- User satisfaction indicators
```

## Conflict Prevention

### Multi-Tool Coordination
```markdown
## Coordination Protocol
1. Check currentWorkingStatus for active tools before starting
2. Respect task boundaries and targetFiles
3. Communicate through .project-identity updates
4. Avoid overlapping work areas
```

### Resource Locking
```markdown
## Gemini Resource Usage
- Acquire lock before major changes
- Release lock promptly after completion
- Handle lock conflicts gracefully
- Use timeout mechanisms
```

## Integration Points

### With Planning Workflow
- Follow EARS requirement format
- Generate Kiro-compatible tasks
- Maintain planning consistency

### With Auto-Task-Execution
- Support atomic task execution
- Provide clear reasoning
- Enable rollback capabilities

### With Context7
- Leverage best practices
- Validate against patterns
- Cache validated results

## Testing Configuration

### Response Validation
```markdown
## Gemini Output Testing
- Syntax validation for code
- Logic verification for solutions
- Completeness check for responses
- Consistency across turns
```

### Integration Testing
```markdown
## Multi-Tool Testing
- Project identity synchronization
- JSON validation and parsing
- Conflict resolution testing
- Performance benchmarking
```

## Monitoring and Analytics

### Usage Patterns
```markdown
## Track Gemini Metrics
- Query types and frequency
- Response success rates
- Average completion times
- Error patterns
```

### Improvement Areas
```markdown
## Continuous Improvement
- Identify common failures
- Optimize response templates
- Enhance context management
- Improve coordination protocols
```

## Debugging Support

### Gemini Debug Mode
```markdown
## Enable detailed logging
GEMINI_DEBUG=true

## Debug information includes:
- Context processing steps
- Decision tree traversal
- Project identity sync operations
- Performance metrics
```

### Troubleshooting Guide
```markdown
## Common Issues
1. Context confusion
   - Solution: Reset with .project-identity
   
2. Duplicate work
   - Solution: Strict currentWorkingStatus checking
   
3. Format inconsistency
   - Solution: Template enforcement
```

---

**Note**: This configuration ensures Gemini works seamlessly within the multi-AI ecosystem, maintaining high quality outputs while preventing conflicts through .project-identity synchronization and structured response patterns.