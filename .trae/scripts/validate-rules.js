#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

function validateRules() {
  console.log('🔍 Validating migrated rules...');
  
  const errors = [];
  const warnings = [];
  let totalFiles = 0;
  
  // Check all rule files
  function scanDirectory(dir) {
    if (!fs.existsSync(dir)) {
      errors.push(`Directory not found: ${dir}`);
      return;
    }
    
    const files = fs.readdirSync(dir, { withFileTypes: true });
    
    files.forEach(file => {
      if (file.isDirectory()) {
        scanDirectory(path.join(dir, file.name));
      } else if (file.name.endsWith('.md')) {
        validateRuleFile(path.join(dir, file.name));
        totalFiles++;
      }
    });
  }
  
  function validateRuleFile(filePath) {
    try {
      const content = fs.readFileSync(filePath, 'utf8');
      const filename = path.basename(filePath);
      const relativePath = path.relative('.trae/rules', filePath);
      
      // Check YAML frontmatter
      if (!content.startsWith('---')) {
        warnings.push(`${relativePath}: Missing YAML frontmatter`);
      }
      
      // Check #rulename format
      const lines = content.split('\n');
      let hasRuleName = false;
      
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        if (line.startsWith('#') && !line.startsWith('##') && !line.startsWith('---')) {
          hasRuleName = true;
          
          // Validate rulename format
          if (!line.match(/^#[a-z0-9-]+$/)) {
            warnings.push(`${relativePath}: Invalid rulename format: ${line}`);
          }
          break;
        }
      }
      
      if (!hasRuleName) {
        errors.push(`${relativePath}: Missing #rulename header`);
      }
      
      // Check for broken references
      const cursorRefs = content.match(/\.cursor\/rules\//g);
      if (cursorRefs) {
        warnings.push(`${relativePath}: Contains ${cursorRefs.length} old cursor references`);
      }
      
      // Check for .mdc references
      const mdcRefs = content.match(/\.mdc/g);
      if (mdcRefs) {
        warnings.push(`${relativePath}: Contains ${mdcRefs.length} .mdc references (should be .md)`);
      }
      
      // Check for required sections
      if (!content.includes('## ') && content.length > 200) {
        warnings.push(`${relativePath}: No section headers found`);
      }
      
      // Check file size
      if (content.length < 50) {
        warnings.push(`${relativePath}: File seems too small (${content.length} chars)`);
      }
      
      // Check for empty content after frontmatter
      const contentAfterFrontmatter = content.replace(/^---[\s\S]*?---\s*/, '');
      if (contentAfterFrontmatter.trim().length < 20) {
        errors.push(`${relativePath}: No meaningful content after frontmatter`);
      }
      
    } catch (error) {
      errors.push(`${path.relative('.trae/rules', filePath)}: Read error - ${error.message}`);
    }
  }
  
  // Validate directory structure
  const expectedDirs = [
    'core', 'agents', 'mobile/android', 'mobile/ios', 'mobile/shared',
    'web/frontend', 'web/backend', 'web/shared',
    'workflows/planning', 'workflows/brainstorm', 'workflows/tasks', 'workflows/development',
    'project/creation', 'project/management', 'project/integration',
    'intelligence/context', 'intelligence/validation', 'intelligence/advanced',
    'kiro'
  ];
  
  expectedDirs.forEach(dir => {
    const fullPath = path.join('.trae/rules', dir);
    if (!fs.existsSync(fullPath)) {
      warnings.push(`Expected directory missing: ${dir}`);
    }
  });
  
  // Scan all rules
  scanDirectory('.trae/rules');
  
  // Report results
  console.log(`\n📊 Validation Results:`);
  console.log(`📁 Total files scanned: ${totalFiles}`);
  console.log(`❌ Errors: ${errors.length}`);
  console.log(`⚠️  Warnings: ${warnings.length}`);
  
  if (errors.length > 0) {
    console.log('\n🚨 Errors:');
    errors.forEach(error => console.log(`  - ${error}`));
  }
  
  if (warnings.length > 0) {
    console.log('\n⚠️  Warnings:');
    warnings.forEach(warning => console.log(`  - ${warning}`));
  }
  
  // Overall status
  if (errors.length === 0 && warnings.length === 0) {
    console.log('\n🎉 All validations passed!');
  } else if (errors.length === 0) {
    console.log('\n✅ Validation completed with warnings only.');
  } else {
    console.log('\n❌ Validation failed. Please fix errors before proceeding.');
  }
  
  return { errors, warnings, totalFiles };
}

function generateValidationReport() {
  const results = validateRules();
  const timestamp = new Date().toISOString();
  
  const report = `# Validation Report\n\n**Generated**: ${timestamp}\n**Total Files**: ${results.totalFiles}\n**Errors**: ${results.errors.length}\n**Warnings**: ${results.warnings.length}\n\n## Summary\n\n${results.errors.length === 0 ? '✅ All critical validations passed' : '❌ Critical errors found'}\n\n## Details\n\n### Errors\n${results.errors.map(e => `- ${e}`).join('\n')}\n\n### Warnings\n${results.warnings.map(w => `- ${w}`).join('\n')}\n`;
  
  fs.writeFileSync('.trae/scripts/validation-report.md', report);
  console.log('\n📄 Validation report saved to .trae/scripts/validation-report.md');
}

if (require.main === module) {
  const results = validateRules();
  
  // Generate report if requested
  if (process.argv.includes('--report')) {
    generateValidationReport();
  }
  
  process.exit(results.errors.length > 0 ? 1 : 0);
}

module.exports = { validateRules, generateValidationReport };