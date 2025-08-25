---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.129Z"
---

#ai-execution-templates
# AI Execution Templates - Cursor Optimized

## 🎯 Purpose

Cung cấp templates và scripts chuẩn cho AI assistants để thực thi manual code quality workflow một cách nhất quán trên tất cả platforms (TRAE, KIRO, Claude, Gemini).

## 🚀 Core Execution Templates

### Template 1: Pre-Code Analysis Workflow

```markdown
# PRE-CODE ANALYSIS EXECUTION

## MANDATORY Steps (Execute in Order):

### Step 1: File Structure Scan (10 seconds)
```

AI_EXECUTE: {
action: "scan_file_structure",
target: "current_file",
analysis: {
package_declaration: "extract_package_name",
existing_imports: "list_all_imports",
class_declarations: "identify_classes_interfaces",
method_signatures: "extract_method_info",
variable_declarations: "map_variable_types"
}
}

```

### Step 2: Context Understanding (15 seconds)
```

AI_EXECUTE: {
action: "analyze_context",
checks: [
"determine_project_type", // Android, Web, Backend, etc.
"identify_framework_patterns", // Fragment, Activity, Compose, etc.
"check_existing_coding_style",
"note_naming_conventions",
"detect_architecture_pattern" // MVVM, MVC, etc.
]
}

```

### Step 3: Preparation (5 seconds)
```

AI_EXECUTE: {
action: "prepare_templates",
setup: [
"load_common_import_map",
"configure_auto_fix_rules",
"set_confidence_thresholds",
"prepare_error_patterns",
"ready_fix_templates"
]
}

```

```

### Template 2: Real-Time Code Validation

```markdown
# REAL-TIME VALIDATION EXECUTION

## CONTINUOUS Monitoring (Execute on Every Code Change):

### Import Validation
```

AI_EXECUTE: {
trigger: "on_class_usage",
action: "validate_import",
rules: {
"Fragment": "auto_add_androidx_fragment_import",
"ViewModel": "auto_add_lifecycle_import",
"Context": "auto_add_android_context_import",
"Log": "auto_add_android_util_log_import",
// Compose
"Composable": "auto_add_compose_runtime_import",
"Text": "auto_add_material3_text_import",
"Button": "auto_add_material3_button_import"
},
confidence_threshold: 0.95
}

```

### Type Safety Validation
```

AI_EXECUTE: {
trigger: "on_variable_assignment",
action: "validate_type_safety",
checks: [
{
pattern: "nullable_variable.property",
fix: "add_safe_call_operator", // variable?.property
confidence: 0.95
},
{
pattern: "string_to_int_assignment",
fix: "add_safe_conversion", // .toIntOrNull() ?: 0
confidence: 0.90
},
{
pattern: "unsafe_casting",
fix: "add_safe_cast", // as? Type ?: return
confidence: 0.85
}
]
}

```

### Syntax Validation
```

AI_EXECUTE: {
trigger: "on_statement_completion",
action: "validate_syntax",
fixes: [
{
pattern: "missing_closing_bracket",
fix: "auto_complete_bracket",
confidence: 0.98
},
{
pattern: "incomplete_function_declaration",
fix: "add_function_body_template",
confidence: 0.95
},
{
pattern: "missing_semicolon",
fix: "add_semicolon",
confidence: 0.99
}
]
}

```

```

### Template 3: Post-Code Quality Check

```markdown
# POST-CODE QUALITY EXECUTION

## FINAL Validation (Execute After Code Completion):

### Step 1: Import Optimization (20 seconds)
```

AI_EXECUTE: {
action: "optimize_imports",
tasks: [
"scan_entire_file_for_unused_imports",
"remove_unused_imports_with_95_confidence",
"sort_imports_by_category",
"check_for_import_conflicts",
"validate_import_organization"
]
}

```

### Step 2: Complete Type Safety Check (30 seconds)
```

AI_EXECUTE: {
action: "final_type_safety_validation",
comprehensive_checks: [
"scan_all_nullable_operations",
"validate_all_type_assignments",
"check_generic_type_consistency",
"verify_function_return_types",
"ensure_parameter_type_matching"
]
}

```

### Step 3: Syntax & Style Validation (10 seconds)
```

AI_EXECUTE: {
action: "final_syntax_style_check",
validations: [
"complete_bracket_matching_scan",
"verify_statement_completion",
"check_naming_convention_compliance",
"validate_indentation_consistency",
"ensure_proper_formatting"
]
}

```

```

## 🤖 AI-Specific Execution Scripts

### TRAE AI Execution Script

```markdown
# TRAE AI - ANDROID SPECIFIC EXECUTION

## Pre-Code Setup
```

TRAE_EXECUTE: {
android_specific_setup: {
import_knowledge_base: "load_androidx_import_map",
lifecycle_patterns: "load_fragment_activity_patterns",
compose_patterns: "load_jetpack_compose_patterns",
context_usage_rules: "load_context_safety_rules"
}
}

```

## Real-Time Android Validation
```

TRAE_EXECUTE: {
on_fragment_detected: {
validate: [
"lifecycle_method_signatures",
"proper_context_usage",
"view_binding_patterns",
"fragment_transaction_safety"
],
auto_fix_confidence: 0.90
},
on_compose_detected: {
validate: [
"composable_annotations",
"remember_state_usage",
"compose_navigation_patterns",
"material3_component_usage"
],
auto_fix_confidence: 0.95
}
}

```

## Post-Code Android Check
```

TRAE_EXECUTE: {
final_android_validation: {
lifecycle_compliance: "validate_complete_lifecycle_implementation",
memory_leak_check: "scan_for_context_leaks_and_static_references",
threading_validation: "ensure_main_thread_ui_operations",
resource_management: "validate_proper_resource_cleanup"
}
}

```

```

### KIRO System Execution Script

```markdown
# KIRO - CONFIGURATION SPECIFIC EXECUTION

## Pre-Task Setup
```

KIRO_EXECUTE: {
configuration_setup: {
yaml_syntax_rules: "load_yaml_validation_patterns",
dependency_patterns: "load_dependency_resolution_rules",
file_path_validation: "load_path_reference_patterns",
task_syntax_rules: "load_kiro_task_format_rules"
}
}

```

## Real-Time Configuration Validation
```

KIRO_EXECUTE: {
on_yaml_edit: {
validate: [
"indentation_consistency",
"key_value_pair_syntax",
"array_object_formatting",
"string_quoting_consistency"
],
auto_fix_confidence: 0.95
},
on_dependency_declaration: {
validate: [
"dependency_exists_in_registry",
"version_compatibility",
"circular_dependency_check",
"required_dependencies_present"
],
auto_fix_confidence: 0.85
}
}

```

## Post-Task Configuration Check
```

KIRO_EXECUTE: {
final_config_validation: {
complete_syntax_check: "validate_entire_config_file_syntax",
dependency_resolution: "verify_all_dependencies_can_resolve",
task_execution_readiness: "confirm_all_tasks_have_required_config",
error_handling_completeness: "ensure_error_scenarios_covered"
}
}

```

```

### Claude Integration Execution Script

```markdown
# CLAUDE - API INTEGRATION SPECIFIC EXECUTION

## Pre-Integration Setup
```

CLAUDE_EXECUTE: {
api_setup: {
http_client_patterns: "load_http_client_best_practices",
async_patterns: "load_async_await_templates",
json_parsing_rules: "load_safe_json_parsing_patterns",
error_handling_templates: "load_api_error_handling_templates"
}
}

```

## Real-Time API Validation
```

CLAUDE_EXECUTE: {
on_api_call_detected: {
validate: [
"try_catch_block_present",
"timeout_configuration_exists",
"error_response_handling",
"network_permission_declared"
],
auto_fix_confidence: 0.90
},
on_json_parsing: {
validate: [
"null_safety_in_json_access",
"type_safety_in_data_extraction",
"graceful_parsing_error_handling",
"schema_validation_present"
],
auto_fix_confidence: 0.95
}
}

```

## Post-Integration API Check
```

CLAUDE_EXECUTE: {
final_api_validation: {
complete_error_coverage: "ensure_all_api_calls_have_error_handling",
timeout_consistency: "verify_consistent_timeout_configuration",
response_validation: "confirm_all_responses_properly_validated",
security_check: "scan_for_api_key_exposure_and_ssl_usage"
}
}

```

```

### Gemini Integration Execution Script

```markdown
# GEMINI - ML MODEL SPECIFIC EXECUTION

## Pre-Model Setup
```

GEMINI_EXECUTE: {
ml_setup: {
model_initialization_patterns: "load_safe_model_loading_templates",
input_validation_rules: "load_ml_input_validation_patterns",
tensor_compatibility_checks: "load_tensor_dimension_validation",
resource_management_patterns: "load_ml_resource_cleanup_templates"
}
}

```

## Real-Time ML Validation
```

GEMINI_EXECUTE: {
on_model_usage: {
validate: [
"model_initialization_safety",
"input_data_validation_present",
"output_handling_robustness",
"resource_cleanup_implementation"
],
auto_fix_confidence: 0.85
},
on_tensor_operations: {
validate: [
"tensor_dimension_compatibility",
"data_type_consistency",
"memory_efficient_operations",
"error_handling_for_model_failures"
],
auto_fix_confidence: 0.80
}
}

```

## Post-Model ML Check
```

GEMINI_EXECUTE: {
final_ml_validation: {
model_lifecycle_management: "ensure_proper_model_loading_and_disposal",
performance_optimization: "validate_efficient_inference_implementation",
error_resilience: "confirm_graceful_handling_of_model_errors",
resource_leak_prevention: "scan_for_memory_leaks_in_ml_operations"
}
}

```

```

## 📋 Quick Reference Execution Guide

### Critical Issue Auto-Fix Templates

```markdown
# CRITICAL FIXES (AUTO-APPLY if Confidence >= 95%)

## Missing Import Fix
```

TEMPLATE: missing_import_fix
PATTERN: unresolved_class_reference
ACTION: auto_add_import_statement
CONFIDENCE_THRESHOLD: 0.95

Example:
// Detected: RecyclerView (unresolved)
// Action: Add "import androidx.recyclerview.widget.RecyclerView"

```

## Null Safety Fix
```

TEMPLATE: null_safety_fix
PATTERN: nullable_variable_unsafe_access
ACTION: add_safe_call_operator
CONFIDENCE_THRESHOLD: 0.95

Example:
// Detected: user.name (where user is nullable)
// Action: Change to user?.name

```

## Type Conversion Fix
```

TEMPLATE: type_conversion_fix
PATTERN: type_mismatch_assignment
ACTION: add_safe_type_conversion
CONFIDENCE_THRESHOLD: 0.90

Example:
// Detected: val age: Int = "25"
// Action: Change to val age: Int = "25".toIntOrNull() ?: 0

```

## Syntax Error Fix
```

TEMPLATE: syntax_error_fix
PATTERN: missing_bracket_or_semicolon
ACTION: complete_syntax_structure
CONFIDENCE_THRESHOLD: 0.98

Example:
// Detected: if (condition) { [missing close bracket]
// Action: Add missing }

```

```

### High Priority Issue Templates

```markdown
# HIGH PRIORITY FIXES (MANUAL REVIEW if Confidence < 85%)

## Performance Anti-Pattern Detection
```

TEMPLATE: performance_issue_detection
PATTERN: inefficient_operation_detected
ACTION: flag_for_manual_review_with_suggestion
CONFIDENCE_THRESHOLD: 0.70

Example:
// Detected: Nested loops with O(n²) complexity
// Action: Flag with suggestion to use more efficient algorithm

```

## Security Vulnerability Detection
```

TEMPLATE: security_issue_detection  
PATTERN: potential_security_risk
ACTION: flag_for_immediate_attention
CONFIDENCE_THRESHOLD: 0.80

Example:
// Detected: Hardcoded API key in source code
// Action: Flag for immediate security review

```

```

## ⚡ Performance #optimization-rules

### Execution Time Limits

```markdown
📊 Pre-Code Analysis: Maximum 30 seconds
📊 Real-Time Validation: Maximum 5 seconds per change
📊 Post-Code Check: Maximum 60 seconds
📊 Total Review Time: Maximum 2 minutes per file
```

### Cursor Optimization (250-line chunks)

```markdown
🎯 File Size Check: If > 250 lines → Split analysis
🎯 Chunk Processing: Analyze in 250-line segments
🎯 Memory Efficiency: Process one chunk at a time
🎯 Context Preservation: Maintain imports and declarations across chunks
```

### Parallel Processing Opportunities

```markdown
⚡ Import validation + Type checking: Run simultaneously
⚡ Syntax checking + Style validation: Run simultaneously  
⚡ Security scan + Performance check: Run simultaneously
⚡ Platform-specific validation: Run in parallel across platforms
```

## 🎯 Success Metrics & Validation

### Template Effectiveness Tracking

```markdown
📈 Auto-fix Success Rate: > 90%
📈 False Positive Rate: < 5%
📈 Execution Time Compliance: > 95%
📈 Platform Consistency: > 95%
📈 User Satisfaction: > 85%
```

### Continuous Template Improvement

```markdown
🔄 Daily: Success rate monitoring
🔄 Weekly: Template effectiveness review
🔄 Monthly: New pattern detection and integration  
🔄 Quarterly: Major template optimization
🔄 Annually: Complete template framework review
```

---

**Implementation Note**: These templates are designed for consistent execution across all AI platforms (TRAE, KIRO, Claude, Gemini) without relying on IDE automation. Each template includes specific confidence thresholds and execution time limits to ensure reliable and efficient code quality improvement.
