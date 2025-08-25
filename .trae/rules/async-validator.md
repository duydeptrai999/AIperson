---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.176Z"
---

#async-validator
---
description: Rule để validate và ensure async/await patterns được sử dụng đúng cách
globs: *.js,*.jsx,*.ts,*.tsx,*.py,*.dart,*.swift,*.kt,*.java,*.cs,*.go,*.rs
alwaysApply: false
---
# Async #validator-rule

## Purpose
Tự động phát hiện và sửa các vấn đề liên quan đến async/await patterns, giúp tránh lỗi runtime khi làm việc với asynchronous code. Rule này đặc biệt quan trọng cho Trae và Kiro vốn thường gặp vấn đề với async functions.

## Nguyên Tắc Cơ Bản
- ***BẮT BUỘC*** validate async function declarations
- ***BẮT BUỘC*** check for missing await statements
- ***BẮT BUỘC*** ensure proper error handling cho promises
- ***BẮT BUỘC*** prevent sync blocking trong async context
- ***KHUYẾN NGHỊ*** use async/await over callbacks
- ***NGHIÊM CẤM*** mix sync và async code không đúng cách

## Async Pattern Detection

### Step 1: Identify Async Contexts
```
1. Functions marked with async keyword
2. Functions returning Promises
3. Callback-based async patterns
4. Event handlers và listeners
5. Timer-based operations
```

### Step 2: Validate Async Usage
```
1. Check async function declarations
2. Verify await usage for Promises
3. Detect missing error handling
4. Find sync blocking operations
5. Identify callback hell patterns
```

### Step 3: Generate Fixes
```
1. Add missing async keywords
2. Insert await statements
3. Wrap in try-catch blocks
4. Convert callbacks to async/await
5. Refactor promise chains
```

## Language-Specific Patterns

### JavaScript/TypeScript
```javascript
// ❌ BAD: Missing async keyword
function fetchData() {
    const result = await fetch('/api/data');
    return result.json();
}

// ✅ GOOD: Proper async declaration
async function fetchData() {
    const result = await fetch('/api/data');
    return result.json();
}

// ❌ BAD: Missing await
async function processData() {
    const data = fetchData(); // Promise not awaited
    console.log(data); // Will log Promise object
}

// ✅ GOOD: Proper await usage
async function processData() {
    const data = await fetchData();
    console.log(data);
}

// ❌ BAD: No error handling
async function riskyOperation() {
    const result = await dangerousCall();
    return result;
}

// ✅ GOOD: Proper error handling
async function riskyOperation() {
    try {
        const result = await dangerousCall();
        return result;
    } catch (error) {
        console.error('Operation failed:', error);
        throw error;
    }
}
```

### Python
```python
# ❌ BAD: Sync call in async function
async def fetch_data():
    response = requests.get('/api/data')  # Blocking call
    return response.json()

# ✅ GOOD: Proper async call
async def fetch_data():
    async with aiohttp.ClientSession() as session:
        async with session.get('/api/data') as response:
            return await response.json()

# ❌ BAD: Missing await
async def process_data():
    data = fetch_data()  # Coroutine not awaited
    print(data)  # Will print coroutine object

# ✅ GOOD: Proper await usage
async def process_data():
    data = await fetch_data()
    print(data)
```

### Common Async Patterns

#### Promise Chaining vs Async/Await
```javascript
// ❌ Promise chaining (harder to read)
function fetchUserData(userId) {
    return fetch(`/api/users/${userId}`)
        .then(response => response.json())
        .then(user => fetch(`/api/posts/${user.id}`))
        .then(response => response.json())
        .catch(error => console.error(error));
}

// ✅ Async/await (cleaner)
async function fetchUserData(userId) {
    try {
        const userResponse = await fetch(`/api/users/${userId}`);
        const user = await userResponse.json();
        const postsResponse = await fetch(`/api/posts/${user.id}`);
        return await postsResponse.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
}
```

## #validation-rules

### Missing Async Declaration
```
Rule: Function uses await but not declared async
Fix: Add async keyword to function declaration
```

### Missing Await Statement
```
Rule: Promise-returning function called without await
Fix: Add await keyword or handle Promise explicitly
```

### Unhandled Promise Rejection
```
Rule: Async operation without error handling
Fix: Wrap in try-catch or add .catch() handler
```

### Sync Blocking in Async Context
```
Rule: Synchronous blocking operation in async function
Fix: Use async alternative or move to sync function
```

## Error Handling Patterns

### Try-Catch Blocks
```javascript
// Basic error handling
async function safeOperation() {
    try {
        const result = await riskyOperation();
        return { success: true, data: result };
    } catch (error) {
        return { success: false, error: error.message };
    }
}

// Multiple operations
async function multipleOperations() {
    try {
        const user = await fetchUser();
        const posts = await fetchPosts(user.id);
        const comments = await fetchComments(posts);
        return { user, posts, comments };
    } catch (error) {
        // Handle any error in the chain
        logger.error('Operation failed:', error);
        throw new Error('Failed to load user data');
    }
}
```

### Error Recovery
```javascript
// Retry mechanism
async function retryOperation(fn, retries = 3) {
    for (let i = 0; i < retries; i++) {
        try {
            return await fn();
        } catch (error) {
            if (i === retries - 1) throw error;
            await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)));
        }
    }
}

// Fallback values
async function fetchWithFallback(url, fallback) {
    try {
        const response = await fetch(url);
        return await response.json();
    } catch (error) {
        console.warn('Fetch failed, using fallback:', error);
        return fallback;
    }
}
```

## Performance Considerations

### Parallel vs Sequential
```javascript
// ❌ Sequential (slower)
async function fetchAllData() {
    const users = await fetchUsers();
    const posts = await fetchPosts();
    const comments = await fetchComments();
    return { users, posts, comments };
}

// ✅ Parallel (faster)
async function fetchAllData() {
    const [users, posts, comments] = await Promise.all([
        fetchUsers(),
        fetchPosts(),
        fetchComments()
    ]);
    return { users, posts, comments };
}
```

### Conditional Async Operations
```javascript
// Smart loading
async function loadUserData(userId, options = {}) {
    const promises = [fetchUser(userId)];
    
    if (options.includePosts) {
        promises.push(fetchUserPosts(userId));
    }
    
    if (options.includeProfile) {
        promises.push(fetchUserProfile(userId));
    }
    
    const results = await Promise.all(promises);
    return {
        user: results[0],
        posts: results[1] || [],
        profile: results[2] || null
    };
}
```

## Integration với Existing Workflows

### With Kiro Task System
- Validate async patterns trong task implementation
- Ensure task execution là async-safe
- Check acceptance criteria cho async requirements

### With Import Analyzer
- Validate async utility imports
- Check for required async libraries
- Ensure error handling imports

### With Context Generator
- Include async patterns trong context
- Document async requirements
- Track async best practices

## Async Validation Commands

### Manual Commands
```
User: "Check async patterns in this file"
AI: [Scan file, detect issues, suggest fixes]

User: "Convert callbacks to async/await"
AI: [Refactor callback patterns to modern async]

User: "Add error handling to async functions"
AI: [Wrap async calls in try-catch blocks]
```

### Automatic Triggers
- Before code execution
- During task validation
- On file save
- Pre-commit checks

## Best Practices

### Async Function Design
- Keep async functions focused
- Always handle errors
- Return meaningful values
- Document async behavior

### Error Propagation
- Let errors bubble up appropriately
- Provide context in error messages
- Log errors at appropriate levels
- Consider error recovery strategies

### Testing Async Code
- Use async test frameworks
- Test both success and failure paths
- Mock async dependencies
- Verify error handling

## Common Pitfalls

### Forgetting Await
```javascript
// ❌ Common mistake
async function processItems(items) {
    items.forEach(async (item) => {
        await processItem(item); // Won't wait!
    });
    console.log('Done'); // Logs before processing
}

// ✅ Correct approach
async function processItems(items) {
    for (const item of items) {
        await processItem(item);
    }
    console.log('Done');
}

// ✅ Or parallel processing
async function processItems(items) {
    await Promise.all(items.map(item => processItem(item)));
    console.log('Done');
}
```

### Async in Constructors
```javascript
// ❌ Can't use async in constructor
class DataService {
    constructor() {
        this.data = await fetchData(); // Syntax error!
    }
}

// ✅ Use factory pattern
class DataService {
    static async create() {
        const service = new DataService();
        service.data = await fetchData();
        return service;
    }
}

// ✅ Or lazy initialization
class DataService {
    async getData() {
        if (!this.data) {
            this.data = await fetchData();
        }
        return this.data;
    }
}
```

## Validation Checklist

### Pre-Execution
- [ ] All async functions properly declared
- [ ] All Promises have await or handlers
- [ ] Error handling in place
- [ ] No sync blocking operations
- [ ] Proper import statements

### Post-Execution
- [ ] No unhandled promise rejections
- [ ] Async operations complete properly
- [ ] Error states handled gracefully
- [ ] Performance optimized

## Tool-Specific Configurations

### For Trae
```
# Trae-specific async handling
- Always explicit async declarations
- Comprehensive error boundaries
- Detailed async operation logging
- Fallback strategies for all async calls
```

### For Kiro
```
# Kiro task async patterns
- Task handlers must be async
- Await all subtask completions
- Error propagation through task chain
- Async status updates
```

### For Cursor
```
# Cursor optimization
- Leverage async/await suggestions
- Use inline error handling hints
- Enable async linting rules
- Configure async snippets
```

## Monitoring and Metrics

### Async Health Metrics
- Number of missing await statements
- Unhandled promise rejections
- Async function error rates
- Average async operation time

### Performance Tracking
- Sequential vs parallel execution
- Async operation bottlenecks
- Memory usage in async contexts
- Event loop blocking detection

---

*Async Validator ensures reliable asynchronous code execution by catching common async/await mistakes before they cause runtime errors.*