---
trae_context:
  format: "native"
  version: "1.0"
  document_type: "guidelines"
  purpose: "Minimal Instruction Writing Standards"
  last_updated: "2025-01-18T08:00:00.000Z"
---

# Minimal Instruction Guidelines

> **🎯 Standards for writing clean, concise, and technical instructions for AI agents**

## Core Principles

### Clarity Over Verbosity
- Use direct, actionable language
- Avoid redundant explanations and examples
- Focus on "what to do" rather than "why to do it"
- Eliminate marketing language and fluff

### Technical Precision
- Use specific technical terms and concepts
- Provide concrete implementation guidance
- Include essential configuration details only
- Avoid abstract or philosophical discussions

### Context Efficiency
- Minimize token consumption while maintaining completeness
- Structure information hierarchically (most important first)
- Use bullet points and lists for scannable content
- Group related concepts together

## Instruction Structure

### Required Sections
1. **Agent Profile** - Core focus, platform, architecture (3-4 lines max)
2. **Core Competencies** - Key technical skills (bullet points)
3. **Development Rules** - Implementation standards (actionable items)
4. **Best Practices** - Quality guidelines (concrete actions)
5. **Technical Implementation** - Patterns and workflows (brief descriptions)
6. **Quality Assurance** - Testing and validation (essential checks)

### Optional Sections (Use Sparingly)
- **Integration Points** - Only if critical for agent coordination
- **Specialized Knowledge** - Only for unique technical requirements

## Content Guidelines

### What to Include
- Essential technical competencies
- Actionable development rules
- Concrete implementation patterns
- Critical quality standards
- Integration requirements

### What to Exclude
- Code examples and snippets
- Detailed configuration files
- Step-by-step tutorials
- Verbose explanations
- Marketing descriptions
- Historical context
- Philosophical discussions

## Writing Standards

### Language Style
- Use imperative mood ("Implement X", "Configure Y")
- Prefer active voice over passive voice
- Use present tense for standards and rules
- Keep sentences under 20 words when possible

### Technical Terminology
- Use industry-standard terms consistently
- Avoid jargon that doesn't add technical value
- Define acronyms only if essential
- Use specific version numbers when relevant

### Formatting Rules
- Use bullet points for lists (not numbered unless sequence matters)
- Bold important concepts sparingly
- Use code formatting only for specific commands/configs
- Maintain consistent heading hierarchy

## Length Targets

### Agent Instructions
- **Target**: 120-200 lines total
- **Maximum**: 250 lines (exceptional cases only)
- **Core Competencies**: 15-25 items max
- **Development Rules**: 10-15 actionable items
- **Best Practices**: 8-12 concrete guidelines

### System Documents
- **Target**: 100-150 lines for system configs
- **Maximum**: 200 lines for complex systems
- Focus on decision logic and core algorithms

## Quality Checklist

### Content Review
- [ ] Every line serves a specific technical purpose
- [ ] No redundant or overlapping information
- [ ] All competencies are actionable and measurable
- [ ] Rules provide clear implementation guidance
- [ ] Best practices are concrete and verifiable

### Structure Review
- [ ] Information flows from general to specific
- [ ] Related concepts are grouped together
- [ ] Headings accurately reflect content
- [ ] Bullet points are parallel in structure
- [ ] No unnecessary nesting or sub-sections

### Language Review
- [ ] Technical terms are used precisely
- [ ] Sentences are concise and direct
- [ ] Active voice is used consistently
- [ ] No marketing language or fluff
- [ ] Instructions are immediately actionable

## Common Anti-Patterns

### Avoid These Mistakes
- **Code Bloat**: Including full code examples in instructions
- **Tutorial Syndrome**: Writing step-by-step guides instead of standards
- **Redundancy**: Repeating the same concept in different sections
- **Vague Guidelines**: Using abstract terms without concrete actions
- **Feature Creep**: Adding "nice-to-have" information

### Red Flags
- Instructions over 300 lines
- More than 3 levels of heading hierarchy
- Code blocks longer than 5 lines
- Sections with overlapping content
- Abstract concepts without implementation details

## Optimization Techniques

### Content Consolidation
- Merge similar competencies into broader categories
- Combine related rules under unified principles
- Eliminate duplicate information across sections
- Use cross-references instead of repetition

### Language Compression
- Replace phrases with single technical terms
- Use bullet points instead of paragraph explanations
- Eliminate transition words and filler phrases
- Combine related sentences into compound structures

### Structure Simplification
- Flatten deep hierarchies into broader categories
- Remove sections that don't add unique value
- Group implementation details under broader patterns
- Use consistent formatting to reduce cognitive load

## Maintenance Standards

### Regular Review
- Audit instructions quarterly for relevance
- Remove outdated technical references
- Consolidate newly identified redundancies
- Update competencies based on technology evolution

### Version Control
- Track significant changes in document metadata
- Maintain change logs for major revisions
- Document rationale for structural changes
- Preserve optimization metrics (line count reductions)

---

**Purpose**: Ensure AI agent instructions are technically precise, contextually efficient, and immediately actionable  
**Target Audience**: AI system designers and instruction writers  
**Success Metric**: Instructions under 200 lines while maintaining technical completeness