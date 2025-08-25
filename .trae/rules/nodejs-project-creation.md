---
trae_context:
  format: "native"
  version: "1.0"
  migrated_from: "cursor"
  last_updated: "2025-08-18T07:00:32.156Z"
---

#nodejs-project-creation
---
description: Comprehensive Node.js Project Creation Workflow with Full-Stack Templates
globs:
  - "**/*.js"
  - "**/*.ts"
  - "**/*.jsx"
  - "**/*.tsx"
  - "package.json"
  - "*.config.*"
alwaysApply: true
---

# Node.js Project Creation Workflow

## 🎯 Template Matrix & Auto-Selection

### Available Templates

#### 1. **Full-Stack Web Application** (Default)
- **Frontend**: React + TypeScript + Vite
- **Backend**: Express + TypeScript
- **Database**: PostgreSQL (primary) + SQLite (local/dev)
- **ORM**: Drizzle ORM với dual database support
- **Auth**: OAuth 2.0 (Google, GitHub, Discord)
- **UI**: Radix UI + TailwindCSS + Shadcn/ui
- **State**: React Query + Zustand
- **Deployment**: Netlify (frontend) + Railway/Render (backend)

#### 2. **API-Only Backend**
- **Framework**: Express + TypeScript
- **Database**: PostgreSQL + Redis caching
- **Auth**: JWT + OAuth providers
- **Documentation**: Swagger/OpenAPI
- **Testing**: Jest + Supertest
- **Deployment**: Railway/Render/Vercel

#### 3. **Desktop Application**
- **Framework**: Electron + React + TypeScript
- **Database**: SQLite + Local Storage
- **Auth**: OAuth với local token storage
- **UI**: Electron-optimized components
- **Packaging**: electron-builder
- **Distribution**: Auto-updater setup

#### 4. **Mobile API Backend**
- **Framework**: Express + TypeScript
- **Database**: PostgreSQL + Redis
- **Auth**: JWT + Social OAuth
- **Real-time**: Socket.io
- **Push Notifications**: Firebase/OneSignal
- **Deployment**: Optimized for mobile clients

#### 5. **AI-Powered Application**
- **Base**: Full-Stack template
- **AI Integration**: OpenAI/Anthropic/Google AI
- **Vector DB**: Pinecone/Weaviate
- **Streaming**: Server-Sent Events
- **Context Management**: LangChain/custom
- **Deployment**: AI-optimized hosting

## 🔧 Smart Template Selection Algorithm

### Auto-Detection Triggers

```typescript
interface ProjectDetection {
  keywords: string[];
  template: TemplateType;
  confidence: number;
}

const detectionRules: ProjectDetection[] = [
  {
    keywords: ["web app", "website", "dashboard", "admin panel", "full stack"],
    template: "fullstack-webapp",
    confidence: 0.9
  },
  {
    keywords: ["api", "backend", "microservice", "rest", "graphql"],
    template: "api-only",
    confidence: 0.85
  },
  {
    keywords: ["desktop", "electron", "native app"],
    template: "desktop-electron",
    confidence: 0.9
  },
  {
    keywords: ["mobile api", "app backend", "mobile backend"],
    template: "mobile-api",
    confidence: 0.8
  },
  {
    keywords: ["ai", "chatbot", "llm", "machine learning", "gpt"],
    template: "ai-powered",
    confidence: 0.85
  }
];
```

## 🗄️ Database Configuration Strategy

### Dual Database Setup (PostgreSQL + SQLite)

#### Primary Database: PostgreSQL
- **Production**: Neon/Supabase/Railway PostgreSQL
- **Staging**: Shared PostgreSQL instance
- **Features**: Full ACID, complex queries, scalability

#### Local Database: SQLite
- **Development**: Local SQLite file
- **Offline Mode**: Client-side caching
- **Testing**: In-memory SQLite
- **Features**: Fast local development, offline support

#### Drizzle ORM Configuration

```typescript
// drizzle.config.ts
import { defineConfig } from 'drizzle-kit';

export default defineConfig({
  schema: './shared/schema.ts',
  out: './drizzle',
  dialect: process.env.NODE_ENV === 'production' ? 'postgresql' : 'sqlite',
  dbCredentials: {
    // Auto-switch based on environment
    url: process.env.NODE_ENV === 'production' 
      ? process.env.DATABASE_URL 
      : './dev.db'
  }
});
```

#### Local Storage Integration

```typescript
// Hybrid storage strategy
class HybridStorage {
  // Critical data: Database
  async saveUserData(data: UserData) {
    return await db.insert(users).values(data);
  }
  
  // UI state: Local Storage
  saveUIPreferences(prefs: UIPreferences) {
    localStorage.setItem('ui-prefs', JSON.stringify(prefs));
  }
  
  // Temporary data: Session Storage
  saveTempData(data: any) {
    sessionStorage.setItem('temp', JSON.stringify(data));
  }
}
```

## 🔐 Authentication System

### OAuth 2.0 Implementation

#### Supported Providers
- **Google OAuth**: Web + Mobile
- **GitHub OAuth**: Developer-focused apps
- **Discord OAuth**: Gaming/Community apps
- **Microsoft OAuth**: Enterprise apps
- **Custom OAuth**: Extensible provider system

#### Passport.js Configuration

```typescript
// auth/strategies.ts
import passport from 'passport';
import { GoogleStrategy } from 'passport-google-oauth20';
import { GitHubStrategy } from 'passport-github2';

// Auto-configure based on environment variables
const strategies = {
  google: process.env.GOOGLE_CLIENT_ID && new GoogleStrategy({
    clientID: process.env.GOOGLE_CLIENT_ID,
    clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    callbackURL: '/auth/google/callback'
  }, handleOAuthCallback),
  
  github: process.env.GITHUB_CLIENT_ID && new GitHubStrategy({
    clientID: process.env.GITHUB_CLIENT_ID,
    clientSecret: process.env.GITHUB_CLIENT_SECRET,
    callbackURL: '/auth/github/callback'
  }, handleOAuthCallback)
};
```

#### JWT + Session Hybrid

```typescript
// Secure token management
class AuthManager {
  // Short-lived JWT for API calls
  generateAccessToken(user: User): string {
    return jwt.sign({ userId: user.id }, process.env.JWT_SECRET, {
      expiresIn: '15m'
    });
  }
  
  // Long-lived refresh token in httpOnly cookie
  generateRefreshToken(user: User): string {
    return jwt.sign({ userId: user.id }, process.env.REFRESH_SECRET, {
      expiresIn: '7d'
    });
  }
}
```

## 🏗️ Component Scaffolding System

### Auto-Generation Templates

#### CRUD Component Generator

```bash
# Generate complete CRUD for entity
npm run scaffold:crud User
# Generates:
# - components/User/UserList.tsx
# - components/User/UserForm.tsx
# - components/User/UserDetail.tsx
# - hooks/useUser.ts
# - api/userApi.ts
# - types/user.ts
```

#### Page Generator

```bash
# Generate page with routing
npm run scaffold:page Dashboard
# Generates:
# - pages/Dashboard/index.tsx
# - pages/Dashboard/Dashboard.tsx
# - pages/Dashboard/components/
# - Auto-updates routing
```

#### API Route Generator

```bash
# Generate API endpoints
npm run scaffold:api posts
# Generates:
# - routes/posts.ts (GET, POST, PUT, DELETE)
# - controllers/postsController.ts
# - validators/postsValidator.ts
# - Auto-updates main router
```

### Scaffolding Configuration

```typescript
// scaffold.config.ts
export const scaffoldConfig = {
  templates: {
    component: './templates/component.hbs',
    page: './templates/page.hbs',
    api: './templates/api.hbs'
  },
  
  outputPaths: {
    components: './client/src/components',
    pages: './client/src/pages',
    api: './server/routes',
    types: './shared/types'
  },
  
  autoImports: true,
  updateRouting: true,
  generateTests: true
};
```

## 🚀 One-Click Deployment Setup

### Netlify Configuration

#### Auto-Generated netlify.toml

```toml
[build]
  base = "."
  command = "npm run build"
  publish = "dist/public"

[build.environment]
  NODE_VERSION = "18"
  NPM_FLAGS = "--prefix=client"

[[redirects]]
  from = "/api/*"
  to = "https://your-backend.railway.app/api/:splat"
  status = 200
  force = true

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

#### Environment Variables Setup

```typescript
// deployment/netlify-env.ts
const requiredEnvVars = [
  'VITE_API_URL',
  'VITE_GOOGLE_CLIENT_ID',
  'VITE_APP_NAME'
];

// Auto-validation script
export function validateNetlifyEnv() {
  const missing = requiredEnvVars.filter(key => !process.env[key]);
  if (missing.length > 0) {
    throw new Error(`Missing environment variables: ${missing.join(', ')}`);
  }
}
```

### Backend Deployment (Railway/Render)

#### railway.json

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "npm run build:server"
  },
  "deploy": {
    "startCommand": "npm run start:prod",
    "healthcheckPath": "/health",
    "healthcheckTimeout": 100
  }
}
```

#### Auto-Deploy Scripts

```bash
#!/bin/bash
# deploy.sh - One-click deployment

echo "🚀 Starting deployment..."

# Build frontend
npm run build

# Deploy to Netlify
netlify deploy --prod --dir=dist/public

# Deploy backend to Railway
railway up

echo "✅ Deployment complete!"
echo "Frontend: https://your-app.netlify.app"
echo "Backend: https://your-api.railway.app"
```

## 📁 Project Structure Template

### Full-Stack Structure

```
project-name/
├── client/                 # Frontend (React + Vite)
│   ├── src/
│   │   ├── components/     # Reusable components
│   │   ├── pages/         # Page components
│   │   ├── hooks/         # Custom hooks
│   │   ├── lib/           # Utilities
│   │   ├── types/         # TypeScript types
│   │   └── assets/        # Static assets
│   ├── public/
│   └── index.html
├── server/                 # Backend (Express)
│   ├── routes/            # API routes
│   ├── controllers/       # Route handlers
│   ├── middleware/        # Custom middleware
│   ├── services/          # Business logic
│   ├── utils/             # Server utilities
│   └── index.ts           # Server entry
├── shared/                 # Shared code
│   ├── schema.ts          # Database schema
│   ├── types.ts           # Shared types
│   └── validators.ts      # Validation schemas
├── drizzle/               # Database migrations
├── tests/                 # Test files
├── docs/                  # Documentation
├── deployment/            # Deployment configs
│   ├── netlify.toml
│   ├── railway.json
│   └── docker/
└── scripts/               # Build/deploy scripts
```

## 🔄 Project Creation Workflow

### Phase 1: Project Detection & Setup

1. **Analyze User Requirements**
   - Parse project description
   - Detect template type
   - Identify special requirements

2. **Template Selection**
   - Auto-select based on keywords
   - Confirm with user if ambiguous
   - Load template configuration

3. **Environment Setup**
   - Create project structure
   - Initialize Git repository
   - Setup package.json with dependencies

### Phase 2: Database Configuration

1. **Database Setup**
   - Configure PostgreSQL connection
   - Setup SQLite for development
   - Generate Drizzle schema
   - Create initial migrations

2. **Local Storage Strategy**
   - Define storage layers
   - Implement hybrid storage class
   - Setup caching mechanisms

### Phase 3: Authentication Implementation

1. **OAuth Configuration**
   - Setup Passport.js strategies
   - Configure OAuth providers
   - Implement JWT handling
   - Create auth middleware

2. **Security Setup**
   - CORS configuration
   - Rate limiting
   - Input validation
   - Security headers

### Phase 4: Component Scaffolding

1. **Generator Setup**
   - Install scaffolding tools
   - Configure templates
   - Setup auto-import system

2. **Initial Components**
   - Generate auth components
   - Create layout components
   - Setup routing structure

### Phase 5: Deployment Configuration

1. **Frontend Deployment**
   - Configure Netlify
   - Setup environment variables
   - Configure redirects

2. **Backend Deployment**
   - Setup Railway/Render
   - Configure database connection
   - Setup health checks

3. **CI/CD Pipeline**
   - GitHub Actions setup
   - Auto-deployment triggers
   - Environment promotion

## 🛠️ Development Tools & Scripts

### Package.json Scripts

```json
{
  "scripts": {
    "dev": "concurrently \"npm run dev:client\" \"npm run dev:server\"",
    "dev:client": "cd client && vite",
    "dev:server": "cd server && tsx watch index.ts",
    "build": "npm run build:client && npm run build:server",
    "build:client": "cd client && vite build",
    "build:server": "cd server && tsc",
    "db:generate": "drizzle-kit generate",
    "db:migrate": "drizzle-kit migrate",
    "db:studio": "drizzle-kit studio",
    "scaffold:component": "node scripts/scaffold-component.js",
    "scaffold:page": "node scripts/scaffold-page.js",
    "scaffold:api": "node scripts/scaffold-api.js",
    "scaffold:crud": "node scripts/scaffold-crud.js",
    "deploy": "./scripts/deploy.sh",
    "deploy:frontend": "netlify deploy --prod",
    "deploy:backend": "railway up",
    "test": "jest",
    "test:watch": "jest --watch",
    "lint": "eslint . --ext .ts,.tsx",
    "type-check": "tsc --noEmit"
  }
}
```

### Development Dependencies

```json
{
  "devDependencies": {
    "@types/node": "^20.0.0",
    "@types/express": "^4.17.0",
    "@types/passport": "^1.0.0",
    "@typescript-eslint/eslint-plugin": "^6.0.0",
    "@typescript-eslint/parser": "^6.0.0",
    "@vitejs/plugin-react": "^4.0.0",
    "concurrently": "^8.0.0",
    "drizzle-kit": "^0.20.0",
    "eslint": "^8.0.0",
    "jest": "^29.0.0",
    "netlify-cli": "^17.0.0",
    "railway": "^3.0.0",
    "tsx": "^4.0.0",
    "typescript": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

## 🎯 Quality Gates & Best Practices

### Code Quality

1. **TypeScript Strict Mode**
   - Enable all strict checks
   - No implicit any
   - Strict null checks

2. **ESLint Configuration**
   - Airbnb style guide
   - React hooks rules
   - Import order rules

3. **Testing Requirements**
   - Unit tests for utilities
   - Integration tests for API
   - E2E tests for critical flows

### Security Standards

1. **Authentication**
   - Secure token storage
   - CSRF protection
   - Rate limiting

2. **Data Protection**
   - Input validation
   - SQL injection prevention
   - XSS protection

3. **Environment Security**
   - Secret management
   - HTTPS enforcement
   - Security headers

### Performance Optimization

1. **Frontend**
   - Code splitting
   - Lazy loading
   - Image optimization

2. **Backend**
   - Database indexing
   - Caching strategy
   - Connection pooling

3. **Database**
   - Query optimization
   - Proper indexing
   - Connection management

## 🔧 Customization Options

### Template Variants

- **Minimal**: Basic setup without extras
- **Standard**: Full-featured template
- **Enterprise**: Advanced features + monitoring
- **Starter**: Beginner-friendly with tutorials

### Optional Features

- **Real-time**: Socket.io integration
- **File Upload**: Cloudinary/S3 setup
- **Email**: SendGrid/Resend integration
- **Analytics**: Google Analytics/Mixpanel
- **Monitoring**: Sentry error tracking
- **Documentation**: Auto-generated API docs

### UI Framework Options

- **Radix UI + TailwindCSS** (Default)
- **Material-UI**
- **Chakra UI**
- **Ant Design**
- **Custom CSS**

## 📚 Documentation Generation

### Auto-Generated Files

1. **README.md**: Project overview and setup
2. **API.md**: API documentation
3. **DEPLOYMENT.md**: Deployment guide
4. **CONTRIBUTING.md**: Development guidelines
5. **CHANGELOG.md**: Version history

### Interactive Documentation

- **Storybook**: Component documentation
- **Swagger UI**: API documentation
- **TypeDoc**: Code documentation

## 🚀 Getting Started Command

```bash
# Create new Node.js project
npx create-nodejs-app my-project

# Interactive setup
? Select template type: Full-Stack Web Application
? Database preference: PostgreSQL + SQLite
? Authentication method: OAuth (Google + GitHub)
? UI framework: Radix UI + TailwindCSS
? Deployment target: Netlify + Railway
? Enable scaffolding: Yes
? Include testing setup: Yes

# Project created successfully!
# Next steps:
# 1. cd my-project
# 2. npm install
# 3. npm run dev
# 4. Open http://localhost:5173
```

---

## 🔗 Integration Points

This workflow integrates with:
- **Kiro Task System**: Auto-generates tasks for each phase
- **Appdexer**: Workspace configuration
- **Context7**: Documentation and examples
- **Memory Bank**: Project templates and patterns
- **Agent System**: Specialized agents for each technology

## 📋 Checklist Template

### Project Creation Checklist

- [ ] Template selected and configured
- [ ] Database setup (PostgreSQL + SQLite)
- [ ] Authentication implemented (OAuth)
- [ ] Component scaffolding configured
- [ ] Deployment setup (Netlify + Railway)
- [ ] Environment variables configured
- [ ] Testing framework setup
- [ ] Documentation generated
- [ ] CI/CD pipeline configured
- [ ] Security measures implemented
- [ ] Performance optimizations applied
- [ ] Quality gates passed
- [ ] Initial deployment successful

### Post-Creation Tasks

- [ ] Setup monitoring and analytics
- [ ] Configure error tracking
- [ ] Setup backup strategies
- [ ] Document API endpoints
- [ ] Create user guides
- [ ] Setup team access
- [ ] Configure staging environment
- [ ] Plan feature roadmap

---

*This workflow ensures consistent, high-quality Node.js project creation with modern best practices and deployment-ready configuration.*