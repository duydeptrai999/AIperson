#!/bin/bash

# Emergency Rollback Script for Trae AI Migration
# Usage: ./rollback.sh [--confirm]

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${RED}🚨 Emergency Rollback Initiated...${NC}"

# Check if confirmation flag is provided
if [[ "$1" != "--confirm" ]]; then
    echo -e "${YELLOW}⚠️  This will restore the original Cursor rules and disable Trae native processing.${NC}"
    echo -e "${YELLOW}⚠️  All migrated Trae rules will be moved to .trae/rules.disabled${NC}"
    echo ""
    echo "To proceed, run: ./rollback.sh --confirm"
    exit 1
fi

echo "Starting rollback process..."

# Step 1: Check if backup exists
BACKUP_DIR=".archive/cursor-rules-$(date +%Y%m%d)"
if [[ ! -d "$BACKUP_DIR" ]]; then
    # Look for any backup directory
    BACKUP_DIR=$(find .archive -name "cursor-rules-*" -type d | head -1)
    if [[ -z "$BACKUP_DIR" ]]; then
        echo -e "${RED}❌ No backup found in .archive/ directory${NC}"
        echo "Cannot proceed with rollback without backup."
        exit 1
    fi
fi

echo -e "${GREEN}✅ Found backup: $BACKUP_DIR${NC}"

# Step 2: Restore cursor rules
echo "Restoring Cursor rules..."
if [[ -d ".cursor/rules" ]]; then
    mv .cursor/rules .cursor/rules.rollback-backup-$(date +%Y%m%d_%H%M%S)
fi

cp -r "$BACKUP_DIR" .cursor/rules
echo -e "${GREEN}✅ Cursor rules restored${NC}"

# Step 3: Disable Trae native processing
echo "Disabling Trae native processing..."
if [[ -d ".trae/rules" ]]; then
    mv .trae/rules .trae/rules.disabled-$(date +%Y%m%d_%H%M%S)
    echo -e "${GREEN}✅ Trae rules disabled${NC}"
fi

# Step 4: Restore original configs (if git is available)
if command -v git &> /dev/null; then
    echo "Restoring original configurations..."
    
    # List of files to potentially restore
    CONFIG_FILES=(
        ".trae/project_rules.md"
        ".trae/user_rules.md"
        ".trae/trae-main-config.mdc"
    )
    
    for file in "${CONFIG_FILES[@]}"; do
        if git show HEAD:"$file" > /dev/null 2>&1; then
            git checkout HEAD -- "$file" 2>/dev/null || true
            echo -e "${GREEN}✅ Restored: $file${NC}"
        fi
    done
fi

# Step 5: Update migration status
echo "Updating migration status..."
cat > .trae/migration-status.md << EOF
# Migration Status: ROLLED BACK

**Rollback Date**: $(date)
**Status**: Cursor rules restored, Trae native disabled
**Backup Location**: $BACKUP_DIR
**Disabled Trae Rules**: .trae/rules.disabled-$(date +%Y%m%d_%H%M%S)

## What was rolled back:
- ✅ Cursor rules restored from backup
- ✅ Trae native rules disabled
- ✅ Original configurations restored

## To re-attempt migration:
1. Review the issues that caused rollback
2. Fix any problems in the migration scripts
3. Run the migration again: node .trae/scripts/migrate-from-cursor.js

## To permanently remove Trae migration:
1. Delete .trae/rules.disabled-* directories
2. Delete .trae/config/ directory
3. Delete .trae/scripts/ directory
EOF

echo -e "${GREEN}✅ Migration status updated${NC}"

# Step 6: Final verification
echo "Verifying rollback..."
if [[ -d ".cursor/rules" ]] && [[ ! -d ".trae/rules" ]]; then
    echo -e "${GREEN}🎉 Rollback completed successfully!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Restart Trae AI to use Cursor rules"
    echo "2. Review .trae/migration-status.md for details"
    echo "3. Check .trae/rules.disabled-* if you need any migrated content"
else
    echo -e "${RED}❌ Rollback verification failed${NC}"
    echo "Please check the directories manually:"
    echo "- .cursor/rules should exist"
    echo "- .trae/rules should not exist"
    exit 1
fi

echo -e "${YELLOW}⚠️  Remember to restart Trae AI for changes to take effect${NC}"