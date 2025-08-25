---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.133Z"
---

#import-analyzer
---
description: Rule để phân tích và giải quyết vấn đề thiếu import trong code
globs: *.js,*.jsx,*.ts,*.tsx,*.py,*.java,*.kt,*.swift,*.dart,*.go,*.rs,*.rb,*.php
alwaysApply: false
---
# Import #analyzer-rule

## Purpose
Tự động phát hiện và giải quyết các vấn đề liên quan đến import statements, giúp giảm lỗi "missing import" khi làm việc với các AI tools khác nhau. Rule này đặc biệt hữu ích cho Trae và Kiro vốn thường gặp vấn đề với import dependencies.

## Nguyên Tắc Cơ Bản
- ***BẮT BUỘC*** scan toàn bộ code để phát hiện missing imports
- ***BẮT BUỘC*** suggest correct import statements
- ***BẮT BUỘC*** validate import paths và module resolution
- ***BẮT BUỘC*** check circular dependencies
- ***KHUYẾN NGHỊ*** organize imports theo convention
- ***NGHIÊM CẤM*** auto-add imports mà không verify

## Import Detection Workflow

### Step 1: Analyze Code Context
```
1. Identify undefined symbols/classes/functions
2. Check current file imports
3. Scan project for matching exports
4. Analyze package.json/requirements.txt for external deps
5. Map symbol to potential import sources
```

### Step 2: Import Resolution
```
1. For each undefined symbol:
   - Search in project files
   - Search in node_modules/site-packages
   - Search in standard library
   - Check common aliases
2. Rank potential sources by:
   - Exact name match
   - Export type (default/named)
   - Path proximity
   - Usage frequency
```

### Step 3: Generate Import Suggestions
```
1. Format import based on language/framework
2. Consider existing import style
3. Group imports by type:
   - Standard library
   - External packages
   - Internal modules
   - Type imports (TypeScript)
4. Apply project conventions
```

## Language-Specific Patterns

### JavaScript/TypeScript
```javascript
// Named imports
import { Component, useState } from 'react';

// Default imports
import React from 'react';

// Type imports (TypeScript)
import type { User } from './types';

// Dynamic imports
const module = await import('./module');

// Common missing imports
- React hooks (useState, useEffect)
- Utility functions (lodash, moment)
- Type definitions
- CSS/SCSS modules
```

### Python
```python
# Standard library
import os
import sys
from typing import List, Dict

# External packages
import numpy as np
import pandas as pd

# Relative imports
from .models import User
from ..utils import helper

# Common missing imports
- Type hints (typing module)
- Async imports (asyncio)
- Testing utilities (pytest)
```

### Java/Kotlin
```java
// Java imports
import java.util.List;
import java.util.ArrayList;
import com.company.project.User;

// Kotlin imports
import kotlinx.coroutines.*
import androidx.compose.runtime.*

// Common missing imports
- Collection classes
- Android components
- Coroutine scopes
```

## Import #organization-rules

### Standard Order
```
1. Standard library imports
2. External dependency imports
3. Internal/local imports
4. Type-only imports (if applicable)

// Blank line between groups
```

### Grouping Strategy
```javascript
// React example
// 1. React core
import React from 'react';
import { useState, useEffect } from 'react';

// 2. External libraries
import axios from 'axios';
import { format } from 'date-fns';

// 3. Internal components
import { Button } from '@/components/ui';
import { UserCard } from './UserCard';

// 4. Types
import type { User } from '@/types';
```

## Circular Dependency Detection

### Detection Algorithm
```
1. Build dependency graph
2. Perform DFS to find cycles
3. Identify problematic imports
4. Suggest refactoring strategies
```

### Resolution Strategies
```
1. Extract shared types to separate file
2. Use dependency injection
3. Lazy imports for circular refs
4. Restructure module boundaries
```

## Integration với Existing Workflows

### With Kiro Task System
- Validate imports before task execution
- Include import requirements in task specs
- Auto-check imports in acceptance criteria

### With Development Workflow
- Pre-commit import validation
- Import organization on save
- CI/CD import checking

### With Context Generator
- Include import maps in context
- Track commonly used imports
- Generate import templates

## Smart Import Features

### Auto-Import Suggestions
```
When typing undefined symbol:
1. Show popup with import options
2. Preview import statement
3. Apply on selection
4. Update import organization
```

### Import Optimization
```
1. Remove unused imports
2. Convert to named imports when possible
3. Combine imports from same module
4. Update deprecated import paths
```

### Project-Specific Aliases
```javascript
// Support for path aliases
'@/components' → './src/components'
'@/utils' → './src/utils'
'~/models' → './app/models'
```

## Error Handling

### Common Import Errors
```
1. Module not found
   - Check spelling
   - Verify installation
   - Check path resolution

2. Circular dependency
   - Show dependency chain
   - Suggest refactoring

3. Version mismatch
   - Check package versions
   - Suggest compatible version

4. Platform-specific imports
   - Conditional imports
   - Platform checks
```

### Recovery Strategies
```
1. Fallback imports
2. Polyfills for missing features
3. Dynamic import with error handling
4. Graceful degradation
```

## Import Analysis Commands

### Manual Commands
```
User: "Analyze imports in this file"
AI: [Scan file, detect issues, suggest fixes]

User: "Fix missing imports"
AI: [Add required imports, organize existing]

User: "Check circular dependencies"
AI: [Analyze module graph, report cycles]
```

### Automatic Triggers
- Before code execution
- On file save
- During task validation
- Pre-commit checks

## Best Practices

### Import Hygiene
- Keep imports minimal and specific
- Avoid wildcard imports
- Use consistent import style
- Document non-obvious imports

### Performance Considerations
- Lazy load heavy modules
- Use dynamic imports for code splitting
- Avoid importing entire libraries
- Tree-shake unused exports

### Maintainability
- Use absolute imports for stability
- Maintain import order
- Group related imports
- Comment complex imports

## Import Validation Checklist

### Pre-Execution
- [ ] All symbols have imports
- [ ] Import paths are valid
- [ ] No circular dependencies
- [ ] External deps installed
- [ ] Types properly imported

### Post-Execution
- [ ] No runtime import errors
- [ ] Imports optimized
- [ ] Unused imports removed
- [ ] Import order maintained

## Tool-Specific Configurations

### For Trae
```
# Special handling for Trae import issues
- Always use explicit imports
- Avoid complex re-exports
- Include file extensions when needed
- Use fully qualified paths
```

### For Kiro
```
# Kiro-specific import patterns
- Reference imports in task specs
- Validate before execution
- Include in acceptance criteria
- Document import requirements
```

### For Cursor
```
# Cursor optimization
- Use import suggestions
- Enable auto-import
- Configure import aliases
- Set import preferences
```

## Monitoring and Metrics

### Import Health Metrics
- Number of missing imports detected
- Circular dependencies found
- Import resolution time
- Auto-fix success rate

### Continuous Improvement
- Track common import patterns
- Build import knowledge base
- Optimize resolution algorithm
- Update language patterns

---

*Import Analyzer ensures smooth development by preventing import-related errors before they occur, making the development process more efficient across all AI tools.*