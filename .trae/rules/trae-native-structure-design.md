# Trae AI Native Rules Structure Design

> **Phương Án 1 Modified**: Native Trae Migration với Full Rules Preservation
> Giữ nguyên tất cả 72 rules hiện tại, chỉ tối ưu hóa cấu trúc và format cho Trae AI

## 🎯 Design Philosophy

### Core Principles
- **Preserve All Workflows**: Giữ nguyên 100% rules hiện tại (72 files)
- **Native Trae Format**: Chuyển đổi sang #rulename syntax và context-aware processing
- **Intelligent Organization**: Tổ chức theo nhóm chức năng và project types
- **Zero Loss Migration**: Không mất bất kỳ workflow nào trong quá trình chuyển đổi

### Why Keep All Rules?
- Mỗi rule phục vụ một workflow cụ thể cho từng nhóm dự án
- Các rules là specialized workflows cho different project contexts
- Giảm số lượng có thể gây thiếu sót trong complex project scenarios
- Trae AI có thể handle large rule sets efficiently với context-aware processing

## 📁 Proposed Trae Native Structure

```
.trae/
├── rules/
│   ├── 🔴 core/                    # Core System Rules (8 files)
│   │   ├── #trae-config.mdc
│   │   ├── #base-rules.mdc
│   │   ├── #development-rules.mdc
│   │   ├── #development-control-rules.mdc
│   │   ├── #file-protection-rules.mdc
│   │   ├── #file-organization-rules.mdc
│   │   ├── #cross-ide-compatibility.mdc
│   │   └── #cursor-optimization-rules.mdc
│   │
│   ├── 🎯 agents/                   # Agent & Workflow Management (6 files)
│   │   ├── #agent-workflow-mapping.mdc
│   │   ├── #workflow-coordinator.mdc
│   │   ├── #auto-task-execution.mdc
│   │   ├── #ai-execution-templates.mdc
│   │   ├── #feature-suggestion-engine.mdc
│   │   └── #context-generator.mdc
│   │
│   ├── 📱 mobile/                   # Mobile Development (12 files)
│   │   ├── android/
│   │   │   ├── #android-workflow.mdc
│   │   │   ├── #android-project-template.mdc
│   │   │   ├── #android-error-prevention.mdc
│   │   │   └── #android-code-deduplication.mdc
│   │   ├── ios/
│   │   │   ├── #ios-workflow.mdc
│   │   │   ├── #ios-project-template.mdc
│   │   │   └── #test-spec-driven-development-review.mdc
│   │   └── shared/
│   │       ├── #mobile-utility-workflow.mdc
│   │       ├── #tdd-mobile-workflow.mdc
│   │       ├── #design-to-prompt.mdc
│   │       ├── #universal-code-deduplication.mdc
│   │       └── #tdd-guidelines.mdc
│   │
│   ├── 🌐 web/                      # Web Development (8 files)
│   │   ├── frontend/
│   │   │   ├── #frontend-rules.mdc
│   │   │   ├── #shadcn-ui-rules.mdc
│   │   │   └── #i18n-rules.mdc
│   │   ├── backend/
│   │   │   ├── #backend-rules-optimized.mdc
│   │   │   ├── #api-integration-rules.mdc
│   │   │   └── #database-management.mdc
│   │   └── shared/
│   │       ├── #nodejs-project-creation.mdc
│   │       └── #tech-stack-selection.mdc
│   │
│   ├── 🔄 workflows/                # Process Workflows (15 files)
│   │   ├── planning/
│   │   │   ├── #planning-workflow.mdc
│   │   │   ├── #planning-enforcement.mdc
│   │   │   ├── #planning-validation-rules.mdc
│   │   │   └── #logic-reasoning-workflow.mdc
│   │   ├── brainstorm/
│   │   │   ├── #brainstorm-overview.mdc
│   │   │   ├── #brainstorm-workflow.mdc
│   │   │   └── #expert-brainstorm-guide.mdc
│   │   ├── tasks/
│   │   │   ├── #task-creation-workflow.mdc
│   │   │   ├── #task-update-workflow.mdc
│   │   │   └── #validate-workflow.mdc
│   │   └── development/
│   │       ├── #four-role-development.mdc
│   │       ├── #git-workflow.mdc
│   │       ├── #terminal-rules.mdc
│   │       └── #debug-with-browser-mcp.mdc
│   │
│   ├── 🎯 project/                  # Project Management (10 files)
│   │   ├── creation/
│   │   │   ├── #project-creation-workflow.mdc
│   │   │   ├── #project-identity-template.mdc
│   │   │   └── #project-rules-importance.mdc
│   │   ├── management/
│   │   │   ├── #project-stage-manager.mdc
│   │   │   ├── #project-upgrade-workflow.mdc
│   │   │   ├── #resource-management.mdc
│   │   │   └── #infrastructure-rules.mdc
│   │   └── integration/
│   │       ├── #integration-management.mdc
│   │       ├── #supabase-mcp-workflow.mdc
│   │       └── #context7-auto-workflow.mdc
│   │
│   ├── 🧠 intelligence/             # AI & Context Systems (8 files)
│   │   ├── context/
│   │   │   ├── #memory-bank-workflow.mdc
│   │   │   ├── #context7-auto-workflow.mdc
│   │   │   └── #import-analyzer.mdc
│   │   ├── validation/
│   │   │   ├── #async-validator.mdc
│   │   │   └── #weather-basic-workflow.mdc
│   │   └── advanced/
│   │       ├── #weather-detailed-workflow.mdc
│   │       └── #system-review-summary.mdc
│   │
│   └── 🔧 kiro/                     # Kiro Task System (5 files)
│       ├── #kiro-config.mdc
│       ├── #kiro-system-overview.mdc
│       ├── #kiro-task-execution.mdc
│       ├── #kiro-fallback-workflow.mdc
│       └── #gemini-config.mdc
│
├── config/
│   ├── trae-main.mdc               # Main Trae configuration
│   ├── agent-mappings.json         # Agent to rule mappings
│   └── context-priorities.json     # Context-aware rule priorities
│
└── scripts/
    ├── migrate-from-cursor.js      # Migration script
    ├── validate-rules.js           # Rules validation
    └── sync-check.js               # Sync verification
```

## 🚀 Trae AI Native Features Integration

### 1. #rulename Syntax
```markdown
# Original Cursor format
# Mobile Utility Workflow

# New Trae format
#mobile-utility-workflow
# Mobile Utility Workflow
```

### 2. Context-Aware Processing
```javascript
// Trae AI sẽ tự động detect context và apply relevant rules
context: {
  projectType: "mobile",
  platform: "android",
  stage: "development"
}
// → Auto-apply: #android-workflow, #mobile-utility-workflow, #tdd-mobile-workflow
```

### 3. Smart Rule Activation
```yaml
# .trae/config/context-priorities.json
{
  "mobile_android": [
    "#android-workflow",
    "#mobile-utility-workflow", 
    "#android-project-template",
    "#tdd-mobile-workflow"
  ],
  "web_frontend": [
    "#frontend-rules",
    "#shadcn-ui-rules",
    "#nodejs-project-creation"
  ]
}
```

### 4. Dynamic Rule Loading
- Trae AI chỉ load rules relevant cho current context
- Giảm memory footprint và tăng performance
- Auto-suggest rules based on project analysis

## 🔄 Migration Strategy

### Phase 1: Structure Preparation (30 minutes)
1. Tạo folder structure mới trong `.trae/rules/`
2. Setup config files và mappings
3. Prepare migration scripts

### Phase 2: Rules Conversion (2 hours)
1. Convert từng rule sang #rulename format
2. Organize vào appropriate folders
3. Update internal references và links

### Phase 3: Integration & Testing (1 hour)
1. Test context-aware rule activation
2. Verify agent workflow mappings
3. Performance optimization

### Phase 4: Cleanup (30 minutes)
1. Archive old `.cursor/rules/` (backup)
2. Update project references
3. Documentation update

## 🎯 Benefits of This Approach

### ✅ Advantages
- **Zero Loss**: Giữ nguyên 100% functionality
- **Native Performance**: Tận dụng Trae AI optimizations
- **Better Organization**: Logical grouping theo project types
- **Context Intelligence**: Smart rule activation
- **Scalability**: Dễ dàng thêm rules mới

### 🔧 Technical Improvements
- **Faster Loading**: Context-aware rule loading
- **Better Memory**: Chỉ load rules cần thiết
- **Smart Suggestions**: AI-powered rule recommendations
- **Auto-Sync**: Real-time rule synchronization

## 📊 Implementation Timeline

| Phase | Duration | Tasks |
|-------|----------|-------|
| Prep | 30 min | Structure setup, config files |
| Convert | 2 hours | Rules conversion, organization |
| Test | 1 hour | Integration testing, validation |
| Deploy | 30 min | Cleanup, documentation |
| **Total** | **4 hours** | **Complete migration** |

## 🤝 Next Steps

1. **Confirm Approach**: User approval cho design này
2. **Start Migration**: Begin với Phase 1 preparation
3. **Iterative Testing**: Test từng nhóm rules sau khi convert
4. **Performance Monitoring**: Track improvements sau migration

---

> **Ready to proceed?** 🚀
> Chúng ta có thể bắt đầu migration ngay với approach này không?