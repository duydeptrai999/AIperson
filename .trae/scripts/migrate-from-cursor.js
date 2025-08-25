#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const CURSOR_RULES_DIR = '.cursor/rules';
const TRAE_RULES_DIR = '.trae/rules';

// Rule mapping configuration - all 72 rules
const RULE_MAPPINGS = {
  // Core rules (8 files)
  'base-rules.mdc': 'core/base-rules.md',
  'development-rules.mdc': 'core/development-rules.md',
  'trae-config.mdc': 'core/trae-config.md',
  'file-protection-rules.mdc': 'core/file-protection-rules.md',
  'file-organization-rules.mdc': 'core/file-organization-rules.md',
  'cross-ide-compatibility.mdc': 'core/cross-ide-compatibility.md',
  'terminal-rules.mdc': 'core/terminal-rules.md',
  'resource-management.mdc': 'core/resource-management.md',
  
  // Agent & Workflow Management (6 files)
  'agent-workflow-mapping.mdc': 'agents/agent-workflow-mapping.md',
  'workflow-coordinator.mdc': 'agents/workflow-coordinator.md',
  'auto-task-execution.mdc': 'agents/auto-task-execution.md',
  'ai-execution-templates.mdc': 'agents/ai-execution-templates.md',
  'context-generator.mdc': 'agents/context-generator.md',
  'import-analyzer.mdc': 'agents/import-analyzer.md',
  
  // Mobile Development (12 files)
  'android-workflow.mdc': 'mobile/android/android-workflow.md',
  'android-project-template.mdc': 'mobile/android/android-project-template.md',
  'android-error-prevention.mdc': 'mobile/android/android-error-prevention.md',
  'ios-workflow.mdc': 'mobile/ios/ios-workflow.md',
  'ios-project-template.mdc': 'mobile/ios/ios-project-template.md',
  'mobile-utility-workflow.mdc': 'mobile/shared/mobile-utility-workflow.md',
  'tdd-mobile-workflow.mdc': 'mobile/shared/tdd-mobile-workflow.md',
  'design-to-prompt.mdc': 'mobile/shared/design-to-prompt.md',
  'tdd-guidelines.mdc': 'mobile/shared/tdd-guidelines.md',
  
  // Web Development (8 files)
  'frontend-rules.mdc': 'web/frontend/frontend-rules.md',
  'shadcn-ui-rules.mdc': 'web/frontend/shadcn-ui-rules.md',
  'i18n-rules.mdc': 'web/frontend/i18n-rules.md',
  'backend-rules-optimized.mdc': 'web/backend/backend-rules-optimized.md',
  'api-integration-rules.mdc': 'web/backend/api-integration-rules.md',
  'database-management.mdc': 'web/backend/database-management.md',
  'nodejs-project-creation.mdc': 'web/shared/nodejs-project-creation.md',
  'tech-stack-selection.mdc': 'web/shared/tech-stack-selection.md',
  
  // Process Workflows (15 files)
  'planning-workflow.mdc': 'workflows/planning/planning-workflow.md',
  'planning-enforcement.mdc': 'workflows/planning/planning-enforcement.md',
  'logic-reasoning-workflow.mdc': 'workflows/planning/logic-reasoning-workflow.md',
  'brainstorm-overview.mdc': 'workflows/brainstorm/brainstorm-overview.md',
  'brainstorm-workflow.mdc': 'workflows/brainstorm/brainstorm-workflow.md',
  'expert-brainstorm-guide.mdc': 'workflows/brainstorm/expert-brainstorm-guide.md',
  'task-creation-workflow.mdc': 'workflows/tasks/task-creation-workflow.md',
  'task-update-workflow.mdc': 'workflows/tasks/task-update-workflow.md',
  'validate-workflow.mdc': 'workflows/tasks/validate-workflow.md',
  'four-role-development.mdc': 'workflows/development/four-role-development.md',
  'git-workflow.mdc': 'workflows/development/git-workflow.md',
  'debug-with-browser-mcp.mdc': 'workflows/development/debug-with-browser-mcp.md',
  
  // Project Management (10 files)
  'project-rules-importance.mdc': 'project/creation/project-rules-importance.md',
  'project-stage-manager.mdc': 'project/management/project-stage-manager.md',
  'infrastructure-rules.mdc': 'project/management/infrastructure-rules.md',
  'integration-management.mdc': 'project/integration/integration-management.md',
  'supabase-mcp-workflow.mdc': 'project/integration/supabase-mcp-workflow.md',
  'context7-auto-workflow.mdc': 'project/integration/context7-auto-workflow.md',
  
  // AI & Context Systems (8 files)
  'memory-bank-workflow.mdc': 'intelligence/context/memory-bank-workflow.md',
  'async-validator.mdc': 'intelligence/validation/async-validator.md',
  'weather-basic-workflow.mdc': 'intelligence/validation/weather-basic-workflow.md',
  'system-review-summary.mdc': 'intelligence/advanced/system-review-summary.md',
  
  // Kiro Task System (5 files)
  'kiro-config.mdc': 'kiro/kiro-config.md',
  'kiro-system-overview.mdc': 'kiro/kiro-system-overview.md',
  'kiro-task-execution.mdc': 'kiro/kiro-task-execution.md',
  'kiro-fallback-workflow.mdc': 'kiro/kiro-fallback-workflow.md',
  'gemini-config.mdc': 'kiro/gemini-config.md'
};

function convertToTraeFormat(content, filename) {
  // Extract rule name from filename
  const ruleName = '#' + path.basename(filename, '.mdc').replace(/-/g, '-');
  
  // Add YAML frontmatter for Trae AI
  const frontmatter = `---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "${new Date().toISOString()}"
---

`;
  
  // Convert content to Trae format
  let converted = `${frontmatter}${ruleName}\n${content}`;
  
  // Update internal references
  converted = converted.replace(/\.cursor\/rules\//g, '.trae/rules/');
  converted = converted.replace(/\.mdc/g, '.md');
  
  // Update rule references to use # syntax
  converted = converted.replace(/([A-Z][a-z-]+\s+Rules?)/g, (match) => {
    return `#${match.toLowerCase().replace(/\s+/g, '-')}`;
  });
  
  return converted;
}

function migrateRules() {
  console.log('🚀 Starting Cursor → Trae AI migration...');
  console.log(`📁 Migrating ${Object.keys(RULE_MAPPINGS).length} rules`);
  
  let successCount = 0;
  let errorCount = 0;
  
  Object.entries(RULE_MAPPINGS).forEach(([source, target]) => {
    const sourcePath = path.join(CURSOR_RULES_DIR, source);
    const targetPath = path.join(TRAE_RULES_DIR, target);
    
    try {
      if (fs.existsSync(sourcePath)) {
        const content = fs.readFileSync(sourcePath, 'utf8');
        const converted = convertToTraeFormat(content, source);
        
        // Ensure target directory exists
        fs.mkdirSync(path.dirname(targetPath), { recursive: true });
        
        // Write converted file
        fs.writeFileSync(targetPath, converted);
        console.log(`✅ Migrated: ${source} → ${target}`);
        successCount++;
      } else {
        console.log(`⚠️  Source not found: ${source}`);
        errorCount++;
      }
    } catch (error) {
      console.log(`❌ Error migrating ${source}: ${error.message}`);
      errorCount++;
    }
  });
  
  console.log('\n📊 Migration Summary:');
  console.log(`✅ Success: ${successCount}`);
  console.log(`❌ Errors: ${errorCount}`);
  console.log(`📈 Success Rate: ${((successCount / (successCount + errorCount)) * 100).toFixed(1)}%`);
  
  if (errorCount === 0) {
    console.log('🎉 Migration completed successfully!');
  } else {
    console.log('⚠️  Migration completed with some errors. Please review.');
  }
}

if (require.main === module) {
  migrateRules();
}

module.exports = { migrateRules, convertToTraeFormat };