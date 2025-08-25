# Project Identity Integration for Claude AI

## Mandatory Project Identity Check Protocol for Claude

### Pre-Work Requirements
- **BẮT BUỘC**: Kiểm tra `/.project-identity` trước khi bắt đầu BẤT KỲ công việc nào
- **BẮT BUỘC**: Đọc và hiểu current project context từ .project-identity
- **BẮT BUỘC**: Kiểm tra conflicts với other AI tools qua currentWorkingStatus
- **BẮT BUỘC**: Validate access permissions cho target files

### Claude Work Declaration Protocol

#### Khi Bắt Đầu Session
1. Cập nhật currentWorkingStatus trong .project-identity:
   ```json
   {
     "currentWorkingStatus": {
       "aiTool": "Claude",
       "workIntent": "[Detailed work intent and specific files to modify]",
       "status": "in_progress",
       "lastUpdate": "2024-12-20T11:00:00.000Z"
     }
   }
   ```

2. Claude-specific Intent Format:
   - **Work Type**: Analysis, Implementation, Debugging, Documentation, Refactoring
   - **Scope**: Component-level, Module-level, System-level
   - **Complexity**: Simple, Medium, Complex
   - **Estimated Duration**: Short (<30min), Medium (30min-2h), Long (>2h)

3. Target Files Declaration:
   - List all files that will be modified
   - Include new files that will be created
   - Specify directories that will be affected
   - Update list as work progresses

#### Claude-Specific Work Categories

##### Code Analysis & Review
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Analyzing codebase architecture for performance optimization - targeting src/components/*, config/performance.js",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:00:00.000Z"
  }
}
```

##### Feature Implementation
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Implementing user dashboard with real-time notifications - targeting src/dashboard/*, src/notifications/*",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:15:00.000Z"
  }
}
```

##### Bug Investigation & Fix
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Debugging memory leak in data processing module - targeting src/data/processor.js, tests/data-tests.js",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:30:00.000Z"
  }
}
```

##### Documentation & Specs
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Creating API documentation for v3.0 endpoints - targeting docs/api-v3.md, README.md",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:45:00.000Z"
  }
}
```

##### System Architecture
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Designing microservices architecture for scalability - targeting docs/architecture.md, config/services.yaml",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T12:00:00.000Z"
  }
}
```

### Claude Collaboration Protocol

#### With Cursor IDE
- **BẮT BUỘC**: Coordinate với Cursor cho real-time editing
- **BẮT BUỘC**: Respect Cursor's active editing sessions
- **BẮT BUỘC**: Sync changes để avoid conflicts

#### With Trae AI
- **BẮT BUỘC**: Coordinate cho mobile development tasks
- **BẮT BUỘC**: Share context về mobile-specific requirements
- **BẮT BUỘC**: Avoid duplicate mobile implementations

#### With Kiro System
- **BẮT BUỘC**: Respect Kiro's spec generation processes
- **BẮT BUỘC**: Coordinate task execution với Kiro workflows
- **BẮT BUỘC**: Maintain consistency với Kiro-generated specs

#### With Gemini
- **BẮT BUỘC**: Share analysis results và insights
- **BẮT BUỘC**: Coordinate research và documentation tasks
- **BẮT BUỘC**: Avoid duplicate analytical work

### Claude Quality Assurance

#### Pre-Implementation Checks
- Verify no conflicts với ongoing work
- Validate technical approach với project standards
- Confirm resource availability
- Check dependencies và prerequisites

#### During Implementation
- Update progress trong .project-identity regularly
- Document significant decisions trong recentChanges
- Report blockers immediately (update status to "blocked")
- Maintain code quality standards

#### Post-Implementation
- Update recentChanges array trong .project-identity
- Document lessons learned
- Clean up work status từ .project-identity (set status to "completed")
- Prepare handoff documentation

### Claude Error Handling & Recovery

#### Conflict Detection
- **BẮT BUỘC**: Stop work nếu detect file conflicts
- **BẮT BUỘC**: Notify user về conflict situation
- **BẮT BUỘC**: Propose resolution strategies

#### System Errors
- **BẮT BUỘC**: Log errors trong recentChanges với detailed context
- **BẮT BUỘC**: Update currentWorkingStatus.status to "blocked"
- **BẮT BUỘC**: Suggest recovery actions trong workIntent

#### Communication Failures
- **BẮT BUỘC**: Implement retry mechanisms
- **BẮT BUỘC**: Fallback to manual coordination
- **BẮT BUỘC**: Document communication issues

### Claude Performance Optimization

#### Efficient Project Identity Usage
- Cache .project-identity content for session duration
- Minimize JSON read/write operations
- Batch updates when possible
- Use incremental JSON updates với jq

#### Resource Management
- Monitor memory usage during large tasks
- Implement progress checkpoints
- Optimize file I/O operations
- Manage concurrent operations

## Example Claude Work Declarations

### Complex Feature Development
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Implementing advanced search with AI-powered recommendations - targeting src/search/*, src/ai/recommendations.js, src/api/search-endpoints.js",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:00:00.000Z"
  }
}
```

### System Integration
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Integrating third-party payment gateway with error handling - targeting src/payments/gateway.js, src/payments/error-handler.js, tests/payment-tests.js",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:15:00.000Z"
  }
}
```

### Performance Optimization
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Optimizing database queries and implementing caching layer - targeting src/database/queries.js, src/cache/redis-client.js, config/database.js",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:30:00.000Z"
  }
}
```

### Architecture Refactoring
```json
{
  "currentWorkingStatus": {
    "aiTool": "Claude",
    "workIntent": "Refactoring monolith to microservices architecture - targeting src/services/*, config/microservices.yaml, docs/migration-plan.md",
    "status": "in_progress",
    "lastUpdate": "2024-12-20T11:45:00.000Z"
  }
}
```

## Integration với Claude Workflows
- Enhance existing Claude capabilities với .project-identity coordination
- Maintain Claude's analytical strengths
- Preserve Claude's code quality focus
- Enable seamless multi-AI collaboration qua JSON-based status

## Monitoring và Success Metrics
- Track collaboration effectiveness qua .project-identity metrics
- Measure conflict reduction với currentWorkingStatus
- Monitor code quality maintenance
- Assess user satisfaction với multi-AI coordination