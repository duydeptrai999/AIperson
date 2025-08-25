#!/usr/bin/env node

/**
 * Trae AI Enhancement Application Script
 * Áp dụng các cải thiện từ trae-enhanced-config.mdc
 */

const fs = require('fs').promises;
const path = require('path');

// Enhanced Agent Selection Implementation
class EnhancedAgentSelector {
  constructor() {
    this.devKeywords = [
      'implement', 'create', 'build', 'develop', 'code', 'feature', 
      'api', 'function', 'class', 'method', 'component', 'service',
      'endpoint', 'route', 'controller', 'model', 'database', 'schema'
    ];
  }

  selectAgent(userInput, context = {}) {
    const keywords = this.extractKeywords(userInput.toLowerCase());
    const isDevelopment = this.devKeywords.some(keyword => 
      keywords.some(k => k.includes(keyword) || keyword.includes(k))
    );

    if (isDevelopment) {
      return {
        agent: 'comprehensive-dev-agent',
        confidence: 0.9,
        reason: 'Development task requires comprehensive coverage',
        strategy: 'comprehensive'
      };
    }

    return this.specificAgentSelection(keywords, context);
  }

  extractKeywords(text) {
    return text.split(/\s+/).filter(word => word.length > 2);
  }

  specificAgentSelection(keywords, context) {
    // Fallback logic for non-development tasks
    return {
      agent: 'general-agent',
      confidence: 0.7,
      reason: 'General task handling',
      strategy: 'specific'
    };
  }
}

// Comprehensive Codebase Analyzer
class CodebaseAnalyzer {
  constructor() {
    this.analysisCache = new Map();
  }

  async analyzeBeforeImplementation(taskDescription) {
    console.log('🔍 Starting comprehensive codebase analysis...');
    const startTime = Date.now();

    try {
      // 1. Find all related files
      const relatedFiles = await this.findAllRelatedFiles(taskDescription);
      console.log(`📁 Found ${relatedFiles.length} related files`);

      // 2. Extract existing patterns
      const patterns = await this.extractImplementationPatterns(relatedFiles);
      console.log(`🔍 Extracted ${patterns.length} implementation patterns`);

      // 3. Find similar features
      const similarFeatures = await this.findSimilarImplementations(taskDescription);
      console.log(`🔗 Found ${similarFeatures.length} similar implementations`);

      // 4. Generate comprehensive plan
      const plan = await this.generateComprehensivePlan({
        task: taskDescription,
        relatedFiles,
        patterns,
        similarFeatures
      });

      const duration = Date.now() - startTime;
      console.log(`✅ Analysis completed in ${duration}ms`);

      return {
        relatedFiles,
        patterns,
        similarFeatures,
        plan,
        analysisMetrics: {
          duration,
          filesAnalyzed: relatedFiles.length,
          patternsFound: patterns.length,
          similarFeatures: similarFeatures.length
        }
      };
    } catch (error) {
      console.error(`❌ Analysis failed: ${error.message}`);
      throw error;
    }
  }

  async findAllRelatedFiles(taskDescription) {
    // Simulate comprehensive file discovery
    const projectRoot = process.cwd();
    const allFiles = [];

    try {
      // Search in common directories
      const searchDirs = ['src', 'lib', 'components', 'services', 'api', 'controllers', 'models'];
      
      for (const dir of searchDirs) {
        const dirPath = path.join(projectRoot, dir);
        try {
          const files = await this.scanDirectory(dirPath);
          allFiles.push(...files);
        } catch (err) {
          // Directory might not exist, continue
        }
      }

      // Filter based on task description keywords
      const keywords = taskDescription.toLowerCase().split(/\s+/);
      const relevantFiles = allFiles.filter(file => {
        const fileName = path.basename(file).toLowerCase();
        return keywords.some(keyword => 
          fileName.includes(keyword) || 
          keyword.length > 3 && fileName.includes(keyword.substring(0, keyword.length - 1))
        );
      });

      return relevantFiles.slice(0, 20); // Limit to prevent overwhelming
    } catch (error) {
      console.warn(`⚠️ File discovery error: ${error.message}`);
      return [];
    }
  }

  async scanDirectory(dirPath) {
    const files = [];
    try {
      const entries = await fs.readdir(dirPath, { withFileTypes: true });
      
      for (const entry of entries) {
        const fullPath = path.join(dirPath, entry.name);
        
        if (entry.isDirectory() && !entry.name.startsWith('.')) {
          const subFiles = await this.scanDirectory(fullPath);
          files.push(...subFiles);
        } else if (entry.isFile() && this.isCodeFile(entry.name)) {
          files.push(fullPath);
        }
      }
    } catch (error) {
      // Directory access error, skip
    }
    
    return files;
  }

  isCodeFile(fileName) {
    const codeExtensions = ['.js', '.ts', '.jsx', '.tsx', '.py', '.java', '.php', '.rb', '.go'];
    return codeExtensions.some(ext => fileName.endsWith(ext));
  }

  async extractImplementationPatterns(files) {
    const patterns = [];
    
    for (const file of files.slice(0, 10)) { // Limit to prevent timeout
      try {
        const content = await fs.readFile(file, 'utf8');
        
        // Extract class patterns
        const classMatches = content.match(/class\s+(\w+)/g) || [];
        classMatches.forEach(match => {
          patterns.push({
            type: 'class',
            pattern: match,
            file: path.basename(file)
          });
        });

        // Extract function patterns
        const functionMatches = content.match(/function\s+(\w+)|const\s+(\w+)\s*=/g) || [];
        functionMatches.forEach(match => {
          patterns.push({
            type: 'function',
            pattern: match,
            file: path.basename(file)
          });
        });

        // Extract API patterns
        const apiMatches = content.match(/\.get\(|.post\(|\.put\(|\.delete\(/g) || [];
        if (apiMatches.length > 0) {
          patterns.push({
            type: 'api',
            pattern: `${apiMatches.length} API endpoints`,
            file: path.basename(file)
          });
        }
      } catch (error) {
        // File read error, skip
      }
    }

    return patterns;
  }

  async findSimilarImplementations(taskDescription) {
    // Simulate finding similar implementations
    return [
      {
        feature: 'Similar API endpoint',
        file: 'user-controller.js',
        similarity: 0.8
      },
      {
        feature: 'Similar data model',
        file: 'user-model.js',
        similarity: 0.7
      }
    ];
  }

  async generateComprehensivePlan(analysisData) {
    const { task, relatedFiles, patterns, similarFeatures } = analysisData;
    
    return {
      taskBreakdown: [
        'Analyze existing patterns',
        'Implement core functionality',
        'Add error handling',
        'Write tests',
        'Update documentation'
      ],
      implementationStrategy: 'comprehensive',
      estimatedComplexity: 'medium',
      requiredFiles: relatedFiles.slice(0, 5),
      referencePattterns: patterns.slice(0, 3),
      acceptanceCriteria: [
        'All functionality implemented',
        'Error handling included',
        'Tests passing',
        'Documentation updated'
      ]
    };
  }
}

// Reliable Task Executor
class ReliableTaskExecutor {
  constructor() {
    this.executionLog = [];
    this.checkpoints = new Map();
  }

  generateExecutionId() {
    return `exec_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  async executeTask(task) {
    const executionId = this.generateExecutionId();
    const startTime = Date.now();

    try {
      await this.logExecution(executionId, 'STARTED', { task, startTime });
      
      // Create checkpoint
      const checkpoint = await this.createCheckpoint(executionId);
      await this.logExecution(executionId, 'CHECKPOINT_CREATED', { checkpoint });

      // Execute with progress tracking
      const result = await this.executeWithProgress(task, executionId);

      // Validate result
      const validation = await this.validateResult(result, task.acceptanceCriteria || []);
      if (!validation.passed) {
        throw new Error(`Validation failed: ${validation.errors.join(', ')}`);
      }

      // Log success
      const duration = Date.now() - startTime;
      await this.logExecution(executionId, 'COMPLETED', { 
        result, 
        duration,
        validation 
      });

      console.log(`✅ Task ${task.id || executionId} completed successfully in ${duration}ms`);
      return { ...result, executionId, duration };

    } catch (error) {
      await this.handleExecutionError(executionId, task, error, startTime);
      throw error;
    }
  }

  async executeWithProgress(task, executionId) {
    const steps = this.breakdownIntoSteps(task);
    const results = [];

    for (let i = 0; i < steps.length; i++) {
      const step = steps[i];
      
      try {
        await this.logExecution(executionId, 'STEP_STARTED', { 
          step: i + 1, 
          total: steps.length, 
          description: step.description 
        });

        const stepResult = await this.executeStep(step);
        results.push(stepResult);

        await this.logExecution(executionId, 'STEP_COMPLETED', { 
          step: i + 1, 
          result: stepResult 
        });

      } catch (stepError) {
        await this.logExecution(executionId, 'STEP_FAILED', { 
          step: i + 1, 
          error: stepError.message 
        });
        throw stepError;
      }
    }

    return { steps: results, totalSteps: steps.length };
  }

  breakdownIntoSteps(task) {
    // Default step breakdown
    return [
      { description: 'Initialize task', action: 'init' },
      { description: 'Analyze requirements', action: 'analyze' },
      { description: 'Implement solution', action: 'implement' },
      { description: 'Validate result', action: 'validate' }
    ];
  }

  async executeStep(step) {
    // Simulate step execution
    await this.sleep(100); // Simulate work
    return {
      step: step.description,
      status: 'completed',
      timestamp: new Date().toISOString()
    };
  }

  async validateResult(result, acceptanceCriteria) {
    const errors = [];
    
    // Basic validation
    if (!result || typeof result !== 'object') {
      errors.push('Invalid result format');
    }

    // Check acceptance criteria
    for (const criteria of acceptanceCriteria) {
      if (!this.checkCriteria(result, criteria)) {
        errors.push(`Criteria not met: ${criteria}`);
      }
    }

    return {
      passed: errors.length === 0,
      errors
    };
  }

  checkCriteria(result, criteria) {
    // Simple criteria checking
    return true; // Placeholder
  }

  async createCheckpoint(executionId) {
    const checkpoint = {
      id: executionId,
      timestamp: new Date().toISOString(),
      state: 'created'
    };
    
    this.checkpoints.set(executionId, checkpoint);
    return checkpoint;
  }

  async logExecution(executionId, phase, data) {
    const logEntry = {
      executionId,
      phase,
      timestamp: new Date().toISOString(),
      data
    };

    this.executionLog.push(logEntry);
    
    // Write to log file
    try {
      const logPath = path.join(process.cwd(), '.trae', 'logs', 'execution.log');
      await fs.mkdir(path.dirname(logPath), { recursive: true });
      await fs.appendFile(logPath, JSON.stringify(logEntry) + '\n');
    } catch (error) {
      console.warn(`⚠️ Failed to write log: ${error.message}`);
    }
  }

  async handleExecutionError(executionId, task, error, startTime) {
    const duration = Date.now() - startTime;
    
    await this.logExecution(executionId, 'FAILED', {
      task,
      error: {
        message: error.message,
        stack: error.stack,
        type: error.constructor.name
      },
      duration
    });

    console.error(`❌ Task ${task.id || executionId} failed after ${duration}ms: ${error.message}`);
  }

  sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

// Enhanced Info-Hub Integration
class EnhancedInfoHub {
  constructor() {
    this.infoHubPath = path.join(process.cwd(), 'info-hub.md');
    this.failureLogPath = path.join(process.cwd(), '.trae', 'logs', 'info-hub-failures.log');
  }

  async updateWithRetry(tool, status, data, maxRetries = 3) {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        await this.updateInfoHub(tool, status, data);
        console.log(`✅ Info-hub updated successfully (attempt ${attempt})`);
        return;
      } catch (error) {
        console.warn(`⚠️ Info-hub update failed (attempt ${attempt}): ${error.message}`);
        
        if (attempt === maxRetries) {
          console.error(`❌ Info-hub update failed after ${maxRetries} attempts`);
          await this.logInfoHubFailure(tool, status, data, error);
          return; // Don't throw - continue execution
        }
        
        await this.sleep(1000 * attempt);
      }
    }
  }

  async updateInfoHub(tool, status, data) {
    try {
      let content = '';
      try {
        content = await fs.readFile(this.infoHubPath, 'utf8');
      } catch (error) {
        // File doesn't exist, create default content
        content = this.getDefaultInfoHubContent();
      }

      // Update the tool status in the content
      const updatedContent = this.updateToolStatus(content, tool, status, data);
      
      await fs.writeFile(this.infoHubPath, updatedContent);
    } catch (error) {
      throw new Error(`Failed to update info-hub: ${error.message}`);
    }
  }

  updateToolStatus(content, tool, status, data) {
    const timestamp = new Date().toISOString();
    const updateLine = `| ${tool} | ${status} | ${timestamp} | ${JSON.stringify(data)} |`;
    
    // Simple update - append to work distribution table
    const lines = content.split('\n');
    const tableIndex = lines.findIndex(line => line.includes('Work Distribution'));
    
    if (tableIndex !== -1) {
      // Find the end of the table and insert
      let insertIndex = tableIndex + 3; // Skip header and separator
      while (insertIndex < lines.length && lines[insertIndex].startsWith('|')) {
        if (lines[insertIndex].includes(tool)) {
          lines[insertIndex] = updateLine;
          return lines.join('\n');
        }
        insertIndex++;
      }
      // Tool not found, add new line
      lines.splice(insertIndex, 0, updateLine);
    }
    
    return lines.join('\n');
  }

  getDefaultInfoHubContent() {
    return `# Info Hub\n\n## Work Distribution\n\n| Tool | Status | Last Update | Data |\n|------|--------|-------------|------|\n`;
  }

  async safeRead() {
    try {
      return await fs.readFile(this.infoHubPath, 'utf8');
    } catch (error) {
      console.warn(`⚠️ Info-hub read failed: ${error.message}`);
      return this.getDefaultInfoHubContent();
    }
  }

  async logInfoHubFailure(tool, status, data, error) {
    const failureLog = {
      timestamp: new Date().toISOString(),
      tool,
      status,
      data,
      error: error.message,
      action: 'info_hub_update_failed'
    };
    
    try {
      await fs.mkdir(path.dirname(this.failureLogPath), { recursive: true });
      await fs.appendFile(this.failureLogPath, JSON.stringify(failureLog) + '\n');
    } catch (logError) {
      console.error(`Failed to log info-hub failure: ${logError.message}`);
    }
  }

  sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

// Main Enhanced Trae Execution
class EnhancedTraeExecution {
  constructor() {
    this.agentSelector = new EnhancedAgentSelector();
    this.codebaseAnalyzer = new CodebaseAnalyzer();
    this.taskExecutor = new ReliableTaskExecutor();
    this.infoHub = new EnhancedInfoHub();
  }

  async execute(userRequest) {
    const executionId = this.taskExecutor.generateExecutionId();
    
    try {
      console.log(`🚀 Starting enhanced Trae execution: ${executionId}`);
      
      // 1. Enhanced agent selection
      const agent = this.agentSelector.selectAgent(userRequest);
      console.log(`🤖 Selected agent: ${agent.agent} (confidence: ${agent.confidence})`);
      
      // 2. Comprehensive codebase analysis
      const analysis = await this.codebaseAnalyzer.analyzeBeforeImplementation(userRequest);
      
      // 3. Update info-hub with retry
      await this.infoHub.updateWithRetry('Trae', 'Working', {
        request: userRequest,
        agent: agent.agent,
        executionId
      });
      
      // 4. Reliable task execution
      const result = await this.taskExecutor.executeTask({
        id: executionId,
        description: userRequest,
        analysis,
        acceptanceCriteria: analysis.plan.acceptanceCriteria
      });
      
      // 5. Final status update
      await this.infoHub.updateWithRetry('Trae', 'Completed', {
        executionId,
        result,
        duration: result.duration
      });
      
      console.log(`🎉 Enhanced execution completed successfully`);
      return result;
      
    } catch (error) {
      console.error(`❌ Enhanced execution failed: ${error.message}`);
      
      await this.infoHub.updateWithRetry('Trae', 'Failed', {
        executionId,
        error: error.message
      });
      
      throw error;
    }
  }
}

// Export for use
if (require.main === module) {
  // CLI usage
  const userRequest = process.argv[2] || 'Test enhanced execution';
  const enhancedTrae = new EnhancedTraeExecution();
  
  enhancedTrae.execute(userRequest)
    .then(result => {
      console.log('\n✅ Execution completed:', result);
      process.exit(0);
    })
    .catch(error => {
      console.error('\n❌ Execution failed:', error.message);
      process.exit(1);
    });
}

module.exports = {
  EnhancedAgentSelector,
  CodebaseAnalyzer,
  ReliableTaskExecutor,
  EnhancedInfoHub,
  EnhancedTraeExecution
};