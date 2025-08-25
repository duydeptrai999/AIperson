# TRAE AI - Mandatory Code Quality Enforcement

## 🎯 MANDATORY COMPLIANCE NOTICE

**_NGHIÊM CẤM_** bỏ qua quy trình code quality này trong bất kỳ trường hợp nào. Tất cả AI responses phải tuân thủ 100% quy trình này.

## 🔗 Core Rule References

**_BẮT BUỘC_** tham khảo và thực thi các rules sau:

### Primary Rules (MANDATORY)

- **[AI Code Quality Automation](../../.trae/rules/ai-code-quality-automation.md)** - Manual workflow automation
- **[AI Manual Code Review Process](../../.trae/rules/ai-manual-code-review-process.md)** - Review protocols
- **[AI Execution Templates](../../.trae/rules/ai-execution-templates.md)** - Execution scripts

### Supporting Rules (REQUIRED)

- **[Android Workflow](../../.trae/rules/android-workflow.md)** - Android-specific patterns
- **[Universal Code Deduplication](../../.trae/rules/universal-code-deduplication.md)** - Code quality standards

## 🤖 TRAE AI Specific Enforcement

### MANDATORY Pre-Execution Checklist

```markdown
**_BẮT BUỘC_** thực hiện trước EVERY code generation:

☐ 1. EXECUTE: Pre-Code Analysis Workflow (30 seconds max)

- File structure scan
- Context understanding
- Template preparation

☐ 2. VALIDATE: Project type identification

- Android: Load androidx import maps
- Compose: Load Jetpack Compose patterns
- Fragment: Load lifecycle patterns

☐ 3. CONFIGURE: Auto-fix confidence thresholds

- Import additions: 95% confidence
- Safe call operators: 90% confidence
- Basic syntax fixes: 98% confidence
```

### MANDATORY Real-Time Execution

```markdown
**_BẮT BUỘC_** thực hiện DURING code writing:

☐ 1. IMPORT MANAGEMENT (Real-time)

- Auto-add androidx imports: androidx.fragment.app.Fragment
- Auto-add lifecycle imports: androidx.lifecycle.ViewModel
- Auto-add compose imports: androidx.compose.runtime.Composable
- Confidence threshold: 95%+

☐ 2. TYPE SAFETY ENFORCEMENT (Real-time)

- Detect nullable.property → Auto-fix to nullable?.property
- Detect String to Int → Auto-fix with .toIntOrNull() ?: 0
- Detect unsafe casting → Auto-fix with safe cast as?
- Confidence threshold: 90%+

☐ 3. SYNTAX VALIDATION (Real-time)

- Auto-complete missing brackets
- Complete incomplete functions
- Fix statement syntax errors
- Confidence threshold: 98%+
```

### MANDATORY Post-Code Validation

```markdown
**_BẮT BUỘC_** thực hiện AFTER code completion:

☐ 1. FINAL IMPORT OPTIMIZATION (20 seconds max)

- Remove unused imports (95% confidence)
- Sort imports by category
- Validate import organization

☐ 2. COMPLETE TYPE SAFETY CHECK (30 seconds max)

- Scan all nullable operations
- Validate all type assignments
- Check generic type consistency

☐ 3. ANDROID-SPECIFIC VALIDATION (10 seconds max)

- Fragment lifecycle compliance
- Context usage safety
- Memory leak prevention
- Resource management validation
```

## 🚨 CRITICAL ENFORCEMENT RULES

### Zero Tolerance Issues (BLOCK EXECUTION)

```markdown
**_NGHIÊM CẤM_** proceed without fixing:

❌ Missing import statements
❌ Syntax errors (brackets, parentheses)
❌ Type mismatches
❌ Null pointer risks
❌ Unresolved references

ACTION: Must auto-fix with 95%+ confidence or REQUEST manual intervention
```

### High Priority Issues (FIX BEFORE PROCEEDING)

```markdown
**_BẮT BUỘC_** address before continuing:

⚠️ Unused imports
⚠️ Naming convention violations
⚠️ Missing error handling
⚠️ Performance anti-patterns
⚠️ Security vulnerabilities

ACTION: Auto-fix with 85%+ confidence or FLAG for review
```

## 📊 TRAE AI Compliance Monitoring

### Success Criteria (MANDATORY)

```markdown
✅ CRITICAL: Zero import errors
✅ CRITICAL: Zero syntax errors
✅ CRITICAL: Zero type safety violations
✅ HIGH: Consistent naming conventions
✅ HIGH: Basic error handling present
✅ MEDIUM: Code style compliance > 90%
```

### Performance Requirements

```markdown
📈 Import accuracy: > 95%
📈 Type safety coverage: > 98%
📈 Syntax error detection: > 99%
📈 Auto-fix success rate: > 90%
📈 False positive rate: < 5%
📈 Total execution time: < 2 minutes
```

### Android-Specific Metrics

```markdown
🤖 Fragment lifecycle compliance: > 95%
🤖 Compose annotation accuracy: > 98%
🤖 Context usage safety: > 90%
🤖 Memory leak prevention: > 85%
🤖 androidx import accuracy: > 99%
```

## 🔄 Continuous Compliance Monitoring

### Real-Time Monitoring

```markdown
- Track auto-fix application success rates
- Monitor confidence threshold effectiveness
- Detect patterns in failed fixes
- Log performance against benchmarks
```

### Daily Compliance Reports

```markdown
- Import management effectiveness
- Type safety enforcement success
- Android-specific rule compliance
- Overall code quality improvements
```

### Escalation Procedures

```markdown
IF compliance < 90%:
→ IMMEDIATE: Review and adjust confidence thresholds
→ ESCALATE: Manual intervention required
→ BLOCK: Further code generation until resolved

IF critical issues detected:
→ STOP: Immediate execution halt
→ FIX: Auto-apply high-confidence fixes
→ VERIFY: Manual review for complex issues
```

## 🎯 TRAE AI Integration Points

### With Android Workflow

```markdown
- Blueprint-First Development compliance
- Material Design 3 component usage
- Jetpack Compose best practices
- Fragment lifecycle management
- ViewModel implementation patterns
```

### With TDD Workflow

```markdown
- Test-first development approach
- Unit test coverage validation
- Integration test requirements
- Performance test considerations
- Quality gate enforcement
```

### With Git Workflow

```markdown
- Pre-commit quality checks
- Commit message validation
- Branch protection compliance
- Code review preparation
- CI/CD integration readiness
```

## 🚀 Implementation Notes

### For TRAE AI Developers

```markdown
1. This workflow is MANDATORY - no exceptions
2. All confidence thresholds are minimums - higher is better
3. When in doubt, err on the side of caution
4. Manual intervention is preferred over low-confidence auto-fixes
5. Document all decisions and reasoning
```

### For Project Managers

```markdown
1. Monitor compliance reports daily
2. Escalate repeated violations immediately
3. Ensure training on updated procedures
4. Validate quality improvements over time
5. Maintain documentation of exceptions
```

---

**ENFORCEMENT STATUS**: 🔒 MANDATORY - NO EXCEPTIONS
**COMPLIANCE LEVEL**: 100% Required
**LAST UPDATED**: Current Session
**REVIEW FREQUENCY**: Weekly effectiveness, Monthly optimization
