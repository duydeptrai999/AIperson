# How to Use Your New Trae AI Native Rules System 🚀

> **Migration Completed Successfully!** ✅
> Your 58 Cursor rules have been converted to Trae AI native format

## 🎯 What Changed?

### Before (Cursor Format)
```
.cursor/rules/
├── android-workflow.mdc
├── base-rules.mdc
└── ... (58 files in flat structure)
```

### After (Trae AI Native)
```
.trae/rules/
├── core/           # 8 essential rules
├── agents/         # 6 workflow management rules
├── mobile/         # 9 mobile development rules
│   ├── android/
│   ├── ios/
│   └── shared/
├── web/            # 8 web development rules
├── workflows/      # 12 process workflows
├── project/        # 6 project management rules
├── intelligence/   # 4 AI context rules
└── kiro/          # 5 Kiro system rules
```

## 🚀 New Features You Can Use

### 1. Context-Aware Rule Activation
Trae AI now automatically detects your project type and activates relevant rules:

- **Android Project** → Auto-activates `#android-workflow`, `#mobile-utility-workflow`
- **iOS Project** → Auto-activates `#ios-workflow`, `#mobile-utility-workflow`
- **React Project** → Auto-activates `#frontend-rules`, `#nodejs-project-creation`
- **Backend API** → Auto-activates `#backend-rules-optimized`, `#api-integration-rules`

### 2. Smart Rule Loading
- Only loads rules relevant to your current context
- Faster performance and reduced memory usage
- Automatic priority-based activation

### 3. Native #rulename Syntax
All rules now use Trae AI's native format:
```markdown
#android-workflow
# Android Development Workflow

## Your rule content here...
```

## 📋 How to Use

### Automatic Usage (Recommended)
Just work normally! Trae AI will:
1. **Detect** your project type automatically
2. **Activate** relevant rules based on file types
3. **Load** only necessary rules for better performance
4. **Suggest** appropriate workflows for your tasks

### Manual Rule Reference
You can still reference specific rules:
```
# Reference a specific rule
@apply #android-workflow

# Reference multiple rules
@apply #frontend-rules #shadcn-ui-rules

# Reference by category
@apply mobile/* web/frontend/*
```

## 🎯 Rule Categories Explained

### 🔴 Core Rules (Always Active)
- `#base-rules` - Global project rules
- `#development-rules` - General development guidelines
- `#file-protection-rules` - File safety rules
- `#file-organization-rules` - Project structure rules

### 📱 Mobile Rules (Context-Activated)
- **Android**: `#android-workflow`, `#android-project-template`
- **iOS**: `#ios-workflow`, `#ios-project-template`
- **Shared**: `#mobile-utility-workflow`, `#tdd-mobile-workflow`

### 🌐 Web Rules (Context-Activated)
- **Frontend**: `#frontend-rules`, `#shadcn-ui-rules`
- **Backend**: `#backend-rules-optimized`, `#api-integration-rules`
- **Shared**: `#nodejs-project-creation`, `#tech-stack-selection`

### 🔄 Workflow Rules (On-Demand)
- **Planning**: `#planning-workflow`, `#brainstorm-workflow`
- **Tasks**: `#task-creation-workflow`, `#validate-workflow`
- **Development**: `#git-workflow`, `#four-role-development`

### 🧠 Intelligence Rules (Smart-Activated)
- **Context**: `#memory-bank-workflow`, `#context-generator`
- **Validation**: `#async-validator`, `#weather-basic-workflow`

### 🔧 Kiro System (Specialized)
- `#kiro-config`, `#kiro-system-overview`, `#kiro-task-execution`

## 🛠️ Configuration Files

### Main Configuration
- **Location**: `.trae/config/trae-main.md`
- **Purpose**: Global Trae AI settings
- **Customizable**: Yes, edit to adjust behavior

### Agent Mappings
- **Location**: `.trae/config/agent-mappings.json`
- **Purpose**: Define which rules activate for which project types
- **Customizable**: Yes, add your own mappings

### Context Priorities
- **Location**: `.trae/config/context-priorities.json`
- **Purpose**: Control rule activation patterns
- **Customizable**: Yes, adjust detection patterns

## 🔧 Customization Examples

### Add New Project Type Detection
Edit `.trae/config/context-priorities.json`:
```json
{
  "detection_patterns": {
    "flutter": ["flutter", "dart", "pubspec.yaml"],
    "your_framework": ["your", "keywords", "here"]
  }
}
```

### Create Custom Agent Mapping
Edit `.trae/config/agent-mappings.json`:
```json
{
  "flutter_mobile": {
    "primary_rules": [
      "#mobile-utility-workflow",
      "#tdd-mobile-workflow"
    ],
    "secondary_rules": [
      "#design-to-prompt"
    ]
  }
}
```

## 🚨 Troubleshooting

### Rules Not Activating?
1. Check file extensions match detection patterns
2. Verify project structure contains expected files
3. Review `.trae/config/context-priorities.json`

### Performance Issues?
1. Check if too many rules are loading simultaneously
2. Adjust priority levels in configuration
3. Use more specific context patterns

### Need Old Cursor Behavior?
```bash
# Emergency rollback (if needed)
./.trae/scripts/rollback.sh --confirm
```

## 📊 Monitoring & Analytics

### Check What Rules Are Active
```bash
# View current rule status
node .trae/scripts/validate-rules.js

# Generate detailed report
node .trae/scripts/validate-rules.js --report
```

### Performance Monitoring
- Rule loading times are logged
- Context detection accuracy tracked
- Memory usage optimized automatically

## 🎉 Benefits You'll Notice

### ✅ Immediate Benefits
- **Faster Loading**: Only relevant rules load
- **Better Organization**: Find rules by category
- **Smart Suggestions**: Context-aware recommendations
- **Cleaner Interface**: Organized rule structure

### ✅ Long-term Benefits
- **Easier Maintenance**: Logical rule grouping
- **Better Scalability**: Add rules without clutter
- **Improved Performance**: Optimized rule activation
- **Enhanced Workflow**: Context-driven development

## 🔄 Migration Status

- ✅ **58/58 Rules Migrated** (100% success)
- ✅ **Native Format Applied** (#rulename syntax)
- ✅ **Context System Active** (Smart activation)
- ✅ **Backup Available** (Rollback ready)
- ✅ **Validation Passed** (No critical errors)

---

## 🎯 Quick Start Checklist

- [ ] **Test Context Detection**: Create a `.kt` file → Android rules should activate
- [ ] **Test iOS Detection**: Create a `.swift` file → iOS rules should activate
- [ ] **Test Web Detection**: Create a `package.json` → Web rules should activate
- [ ] **Verify Performance**: Notice faster rule loading
- [ ] **Explore Structure**: Browse `.trae/rules/` folders
- [ ] **Customize Settings**: Edit `.trae/config/` files if needed

**Your Trae AI system is now optimized and ready! 🚀**

> **Need Help?** Check `.trae/migration-summary.md` for detailed migration info
> **Having Issues?** Run `.trae/scripts/rollback.sh --confirm` to restore Cursor rules