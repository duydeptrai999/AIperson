---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.132Z"
---

#context-generator
---
description: Rule để generate portable context files cho các AI tools khác nhau
globs: *.*
alwaysApply: false
---
# Context #generator-rule

## Purpose
Generate portable context files cho các AI tools khác để đảm bảo tool-agnostic workflow. Giúp maintain single source of truth và seamless switching giữa Cursor, Claude, Gemini, Trae, và các AI tools khác.

## Nguyên Tắc Cơ Bản
- ***BẮT BUỘC*** collect tất cả relevant context từ project
- ***BẮT BUỘC*** generate theo format phù hợp cho từng tool
- ***BẮT BUỘC*** maintain consistency giữa các context files
- ***BẮT BUỘC*** include core rules và workflows
- ***KHUYẾN NGHỊ*** compress và optimize cho size limits
- ***NGHIÊM CẤM*** expose sensitive information

## Context Collection Workflow

### Step 1: Collect Core Context
```
1. .trae/rules/ - Tất cả workflow rules
2. .kiro/specs/{project}/ - Requirements, design, tasks
3. .project-identity - Project metadata
4. CLAUDE.md - Global instructions (nếu có)
5. Instruction.md - Project-specific instructions
6. Brainstorm files - Current planning context
```

### Step 2: Analyze Project Structure
```
1. Identify tech stack từ .project-identity
2. Scan package managers (package.json, requirements.txt, etc.)
3. Detect framework patterns
4. Map directory structure
5. Identify key components
```

### Step 3: Generate Unified Context
```
1. Merge all rules into single context
2. Remove duplicates và conflicts
3. Organize by priority
4. Add project-specific overrides
5. Include active brainstorm/planning context
```

## Output Formats

### CLAUDE.md - For Claude
```markdown
# Claude Context for [Project Name]

## Project Overview
[Generated from .project-identity and Instruction.md]

## Tech Stack
[Generated from package managers and .project-identity]

## #core-rules & Workflows
[Merged from .trae/rules/]

## Current Context
[Active brainstorm, planning, or tasks]

## Conversation Guidelines
[Personality and communication style]
```

### GEMINI.md - For Gemini
```markdown
# Gemini Context for [Project Name]

## System Instructions
You are an AI assistant working on [Project Name] with the following context...

## Project Context
[Similar structure to CLAUDE.md but optimized for Gemini's format]

## Task Management
[Current Kiro tasks and status]
```

### TRAE.md - For Trae
```markdown
# Trae Context for [Project Name]

## MCP Integration Points
[Context7 and other MCP tools configuration]

## #project-rules
[Adapted from .trae/rules/ for Trae format]

## Active Workflows
[Current execution context]
```

### GENERIC.md - Universal Format
```markdown
# AI Assistant Context for [Project Name]

## Essential Information
- Project: [Name]
- Type: [Type from .project-identity]
- Stack: [Technologies]
- Stage: [Current development stage]

## Core Principles
[Universal rules applicable to any AI]

## Current Focus
[Active tasks and priorities]

## Communication Style
[How to interact with user]
```

## Symlink Strategy

### Auto-create Symlinks (không thay thế .trae/rules/)
```bash
# For Kiro compatibility
ln -s .cursor/rules .kiro/steering

# For other tools - point to generated context
ln -s .cursor/generated/GENERIC.md .ai-context

# For specific tools
ln -s .cursor/generated/CLAUDE.md .claude-context
ln -s .cursor/generated/GEMINI.md .gemini-context
```

## Generation Commands

### Manual Generation
```
User: "Generate context for Claude"
AI: [Execute context generation workflow → Output CLAUDE.md]

User: "Update all AI contexts"
AI: [Regenerate all format files with latest context]
```

### Auto-Generation Triggers
- When .project-identity changes
- When new rules added to .trae/rules/
- When Kiro tasks updated
- Before switching AI tools
- Daily for active projects

## Smart Filtering

### Size Optimization
```
1. Remove commented code blocks
2. Summarize verbose rules
3. Prioritize active workflows
4. Exclude completed tasks
5. Compress repetitive patterns
```

### Context Prioritization
```
Priority 1: Active tasks and current focus
Priority 2: Core rules and principles  
Priority 3: Project structure and setup
Priority 4: Historical context and decisions
Priority 5: Nice-to-have references
```

## Integration với Existing Workflows

### With Kiro Task System
- Include current task status
- Reference active task IDs
- Maintain task dependencies
- Update on task completion

### With Planning Workflow
- Include active brainstorm content
- Reference planning documents
- Maintain EARS requirements
- Update with planning progress

### With Memory Bank
- Include recent workflow states
- Reference important decisions
- Maintain context continuity
- Clean old irrelevant context

## Quality Assurance

### Validation Checklist
- [ ] All core rules included
- [ ] No sensitive data exposed
- [ ] Format appropriate for target AI
- [ ] Size within tool limits
- [ ] Current context accurate
- [ ] No conflicting instructions

### Testing Process
1. Generate context file
2. Test with target AI tool
3. Verify understanding
4. Check for issues
5. Iterate if needed

## Error Handling

### Common Issues
- **File too large**: Apply smart filtering
- **Missing context**: Check collection paths
- **Format errors**: Validate markdown syntax
- **Conflicts**: Resolve rule priorities
- **Stale data**: Trigger regeneration

### Recovery Process
1. Identify error type
2. Apply appropriate fix
3. Regenerate affected files
4. Test with minimal example
5. Full validation

## Advanced Features

### Context Versioning
```
context-generator/
├── current/
│   ├── CLAUDE.md
│   ├── GEMINI.md
│   └── GENERIC.md
└── archive/
    └── 2024-01-15/
        ├── CLAUDE.md
        └── ...
```

### Incremental Updates
- Track changes since last generation
- Update only modified sections
- Maintain context history
- Enable rollback if needed

### Multi-Project Support
- Generate contexts for multiple projects
- Maintain project isolation
- Enable context switching
- Support project templates

## Usage Examples

### Example 1: Starting with new AI tool
```
User: "I need to switch to Claude for this task"
AI: "Generating Claude context... Created CLAUDE.md with current project state. 
     Attach this file to your Claude conversation to maintain continuity."
```

### Example 2: Daily context update
```
AI: "Daily context update completed:
     - Updated 3 active tasks
     - Included new planning decisions
     - Compressed size by 20%
     - All contexts regenerated"
```

### Example 3: Tool-specific optimization
```
User: "Optimize context for Gemini's limits"
AI: "Optimizing for Gemini (1M token limit):
     - Prioritized active workflows
     - Summarized completed tasks
     - Compressed rules to essentials
     - Final size: 750K tokens"
```

## Maintenance

### Regular Updates
- Daily: Update active tasks
- Weekly: Full regeneration
- Monthly: Archive old contexts
- Quarterly: Review and optimize

### Performance Monitoring
- Track generation time
- Monitor file sizes
- Log usage frequency
- Identify optimization opportunities

---

*Context Generator ensures seamless AI tool switching while maintaining project continuity and reducing friction in development workflow.*