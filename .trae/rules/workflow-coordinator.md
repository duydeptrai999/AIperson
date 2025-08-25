---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.127Z"
---

#workflow-coordinator
# Workflow Coordinator - Spec-Driven Development Enforcement

## Overview

This coordinator ensures proper workflow selection and enforcement for all development activities. It mandates the correct sequence: Requirements → Design → Tasks → Execution, and prevents workflow violations.

## 🚨 CRITICAL WORKFLOW ROUTING

### Primary Workflow #selection-rules

**FOR NEW FEATURES** (no existing spec):
- **MANDATORY**: Use `spec-driven-development-workflow.md`
- **SEQUENCE**: Requirements → Design → Tasks → Execution
- **NO EXCEPTIONS**: Cannot skip any phase

**FOR EXISTING SPECS** (with approved requirements and design):
- **ALLOWED**: Use `task-creation-workflow.md` for task optimization
- **PREREQUISITE**: Both requirements.md and design.md must exist and be approved
- **VALIDATION**: Must verify prerequisites before proceeding

**FOR TASK EXECUTION** (with complete spec):
- **MANDATORY**: Follow single-task execution rules
- **PREREQUISITE**: Requirements, design, and tasks must all be approved
- **ENFORCEMENT**: One task at a time, complete before proceeding

## 🔍 WORKFLOW DECISION MATRIX

### Decision Tree for Workflow Selection

```
START: Feature Development Request
│
├─ Does .kiro/specs/{project}/ exist?
│  ├─ NO → Use spec-driven-development-workflow.md
│  │      Start with Requirements Phase
│  │
│  └─ YES → Check existing files
│     │
│     ├─ requirements.md exists and approved?
│     │  ├─ NO → Use spec-driven-development-workflow.md
│     │  │      Start with Requirements Phase
│     │  │
│     │  └─ YES → design.md exists and approved?
│     │     ├─ NO → Use spec-driven-development-workflow.md
│     │     │      Start with Design Phase
│     │     │
│     │     └─ YES → tasks.md exists and approved?
│     │        ├─ NO → Use task-creation-workflow.md
│     │        │      Create tasks from approved design
│     │        │
│     │        └─ YES → Execute tasks one by one
│     │               Follow single-task execution rules
```

## 🚨 MANDATORY ENFORCEMENT RULES

### Phase Gate Enforcement
**AUTOMATIC BLOCKING** for:
- Attempting to create design without approved requirements
- Attempting to create tasks without approved design  
- Attempting to execute tasks without approved task list
- Skipping any phase in the mandatory sequence
- Using wrong workflow for current state

### Document Validation Requirements
**MANDATORY CHECKS** before proceeding:
- **Requirements Phase**: EARS format, user stories, acceptance criteria, user approval
- **Design Phase**: Architecture, components, data models, testing strategy, user approval
- **Tasks Phase**: Coding activities, incremental progression, requirements traceability, user approval
- **Execution Phase**: Single task rule, status management, requirements validation

### User Approval Enforcement
**MANDATORY** explicit user approval using userInput tool:
- **Requirements Review**: reason='spec-requirements-review'
- **Design Review**: reason='spec-design-review'
- **Tasks Review**: reason='spec-tasks-review'
- **Clear Approval Required**: "yes", "approved", "looks good" responses

## 🔧 WORKFLOW COORDINATION LOGIC

### New Feature Development
```
1. Receive feature request
2. Check if .kiro/specs/{project}/ exists
3. If NO → Redirect to spec-driven-development-workflow.md
4. Start Requirements Phase
5. Get user approval for requirements
6. Proceed to Design Phase
7. Get user approval for design
8. Proceed to Tasks Phase
9. Get user approval for tasks
10. Begin single-task execution
```

### Existing Spec Enhancement
```
1. Receive task creation request
2. Validate prerequisites:
   - requirements.md exists and approved
   - design.md exists and approved
3. If prerequisites missing → Redirect to spec-driven-development-workflow.md
4. If prerequisites met → Use task-creation-workflow.md
5. Create optimized tasks from approved design
6. Get user approval for tasks
7. Begin single-task execution
```

### Task Execution
```
1. Receive task execution request
2. Validate complete spec exists:
   - requirements.md (approved)
   - design.md (approved)
   - tasks.md (approved)
3. If any missing → Complete missing phases first
4. If all exist → Execute single task
5. Update task status (in_progress → completed)
6. Stop and wait for user direction
7. Repeat for next task
```

## 🚨 VIOLATION DETECTION AND CORRECTION

### Common Violations
1. **Phase Skipping**: Attempting to create tasks without requirements/design
2. **Missing Approval**: Proceeding without explicit user approval
3. **Wrong Workflow**: Using task-creation-workflow without prerequisites
4. **Parallel Execution**: Attempting to execute multiple tasks simultaneously
5. **Missing Documents**: Starting execution without complete spec

### Automatic Corrections
- **Phase Skipping** → Force redirect to spec-driven-development-workflow.md
- **Missing Approval** → Block progression until approval received
- **Wrong Workflow** → Redirect to correct workflow based on current state
- **Parallel Execution** → Enforce single-task rule
- **Missing Documents** → Complete missing phases before proceeding

### Error Messages
```
VIOLATION: Phase Skipping Detected
ACTION: Redirecting to spec-driven-development-workflow.md
REASON: Cannot create tasks without approved requirements and design
NEXT STEP: Complete Requirements → Design → Tasks sequence

VIOLATION: Missing Prerequisites
ACTION: Blocking task creation workflow
REASON: requirements.md or design.md missing or not approved
NEXT STEP: Use spec-driven-development-workflow.md to complete missing phases

VIOLATION: Parallel Task Execution
ACTION: Enforcing single-task rule
REASON: Multiple tasks cannot be executed simultaneously
NEXT STEP: Complete current task before starting next
```

## 🎯 QUALITY ASSURANCE INTEGRATION

### Continuous Validation
**MANDATORY** checks at each step:
- Document existence and format validation
- User approval status verification
- Workflow compliance monitoring
- Phase completion validation
- Requirements traceability maintenance

### Quality Gates
**MANDATORY** gates that must pass:
- **Gate 1**: Requirements completeness and approval
- **Gate 2**: Design completeness and approval
- **Gate 3**: Tasks completeness and approval
- **Gate 4**: Execution readiness and single-task compliance

### Monitoring and Reporting
**AUTOMATIC** tracking of:
- Workflow violations and corrections
- Phase completion status
- User approval timestamps
- Task execution progress
- Quality gate passage

## 🔄 INTEGRATION WITH EXISTING RULES

### Rule Hierarchy
1. **workflow-coordinator.md** (THIS FILE) - Master coordinator
2. **spec-driven-development-workflow.md** - Complete development process
3. **task-creation-workflow.md** - Task optimization from approved design
4. **Other development rules** - Subordinate to spec-driven process

### Coordination Points
- **Entry Point**: All development requests route through coordinator
- **Workflow Selection**: Coordinator determines appropriate workflow
- **Validation**: Coordinator enforces prerequisites and approvals
- **Execution**: Coordinator manages single-task execution
- **Quality**: Coordinator ensures all quality gates pass

## 🚨 CRITICAL SUCCESS FACTORS

### Non-Negotiable Requirements
1. **SEQUENTIAL PHASES**: Requirements → Design → Tasks → Execution (NO EXCEPTIONS)
2. **USER APPROVAL**: Each phase requires explicit user approval via userInput tool
3. **DOCUMENT COMPLETENESS**: All documents must meet quality standards
4. **SINGLE TASK EXECUTION**: One task at a time, complete before proceeding
5. **REQUIREMENTS TRACEABILITY**: All work must trace back to approved requirements

### Success Metrics
- **100% Phase Compliance**: No phase skipping violations
- **100% Approval Compliance**: All phases explicitly approved
- **100% Document Quality**: All documents meet standards
- **100% Execution Compliance**: Single-task rule enforced
- **100% Traceability**: All tasks trace to requirements

This coordinator ensures systematic, quality-driven development with complete traceability and user control at every phase.