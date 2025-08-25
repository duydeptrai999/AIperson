# Project Rules - Base AI Project

*Simplified version tuân thủ chuẩn Trae AI - Guidelines cụ thể cho Base AI Project*

## Project Overview

### Technology Stack
- **Backend**: Node.js với TypeScript
- **Frontend**: React với modern hooks và TypeScript
- **Database**: PostgreSQL với Prisma ORM
- **Authentication**: JWT với refresh tokens
- **Testing**: Jest cho unit tests, Cypress cho E2E
- **Deployment**: Docker containers trên cloud platform

### Architecture Pattern
- **Backend**: Clean Architecture với dependency injection
- **Frontend**: Component-based architecture với custom hooks
- **API**: RESTful design với GraphQL cho complex queries
- **State Management**: Context API với useReducer cho complex state

## Code Style Guidelines

### Naming Conventions
```typescript
// ✅ GOOD
const userRepository = new UserRepository();
const getUserById = async (id: string) => { ... };
interface UserCreateRequest { ... }
class AuthenticationService { ... }

// ❌ BAD
const ur = new UserRepo();
const getuser = (id) => { ... };
interface userreq { ... }
class authsvc { ... }
```

### File Organization
```
src/
├── components/          # Reusable UI components
├── pages/              # Page-level components
├── hooks/              # Custom React hooks
├── services/           # API calls và business logic
├── utils/              # Helper functions
├── types/              # TypeScript type definitions
├── constants/          # Application constants
└── tests/              # Test files
```

### Import/Export Standards
```typescript
// ✅ GOOD - Named exports với barrel exports
export { UserService } from './UserService';
export { AuthService } from './AuthService';

// ✅ GOOD - Clean imports
import { UserService, AuthService } from '@/services';
import { Button, Input } from '@/components/ui';

// ❌ BAD - Default exports cho services
export default UserService;
```

## API Design Standards

### REST Endpoints
```typescript
// ✅ GOOD - Consistent naming
GET    /api/v1/users           # List users
GET    /api/v1/users/:id       # Get user by ID
POST   /api/v1/users           # Create user
PUT    /api/v1/users/:id       # Update user
DELETE /api/v1/users/:id       # Delete user

// ✅ GOOD - Nested resources
GET    /api/v1/users/:id/posts # Get user's posts
POST   /api/v1/users/:id/posts # Create post for user
```

### Response Format
```typescript
// ✅ GOOD - Consistent response structure
interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
  meta?: {
    pagination?: {
      page: number;
      limit: number;
      total: number;
    };
  };
}
```

### Error Handling
```typescript
// ✅ GOOD - Structured error responses
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "email": "Email format is invalid",
      "password": "Password must be at least 8 characters"
    }
  }
}
```

## Database Guidelines

### Schema Design
```prisma
// ✅ GOOD - Clear relationships và constraints
model User {
  id        String   @id @default(cuid())
  email     String   @unique
  name      String
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt
  
  posts     Post[]
  profile   Profile?
  
  @@map("users")
}

model Post {
  id       String @id @default(cuid())
  title    String
  content  String
  authorId String
  
  author   User   @relation(fields: [authorId], references: [id])
  
  @@map("posts")
}
```

### Query Patterns
```typescript
// ✅ GOOD - Efficient queries với proper includes
const getUserWithPosts = async (userId: string) => {
  return prisma.user.findUnique({
    where: { id: userId },
    include: {
      posts: {
        orderBy: { createdAt: 'desc' },
        take: 10
      },
      profile: true
    }
  });
};
```

## Frontend Component Standards

### Component Structure
```typescript
// ✅ GOOD - Consistent component structure
interface UserCardProps {
  user: User;
  onEdit?: (user: User) => void;
  className?: string;
}

export const UserCard: React.FC<UserCardProps> = ({ 
  user, 
  onEdit, 
  className 
}) => {
  const [isLoading, setIsLoading] = useState(false);
  
  const handleEdit = useCallback(() => {
    onEdit?.(user);
  }, [user, onEdit]);
  
  return (
    <div className={cn('user-card', className)}>
      {/* Component content */}
    </div>
  );
};
```

### Custom Hooks
```typescript
// ✅ GOOD - Reusable logic trong custom hooks
export const useUser = (userId: string) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  useEffect(() => {
    const fetchUser = async () => {
      try {
        setLoading(true);
        const userData = await userService.getById(userId);
        setUser(userData);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unknown error');
      } finally {
        setLoading(false);
      }
    };
    
    fetchUser();
  }, [userId]);
  
  return { user, loading, error };
};
```

## Testing Standards

### Unit Tests
```typescript
// ✅ GOOD - Comprehensive test coverage
describe('UserService', () => {
  describe('createUser', () => {
    it('should create user with valid data', async () => {
      const userData = {
        email: 'test@example.com',
        name: 'Test User'
      };
      
      const result = await userService.create(userData);
      
      expect(result.success).toBe(true);
      expect(result.data).toMatchObject(userData);
    });
    
    it('should return error for invalid email', async () => {
      const userData = {
        email: 'invalid-email',
        name: 'Test User'
      };
      
      const result = await userService.create(userData);
      
      expect(result.success).toBe(false);
      expect(result.error?.code).toBe('VALIDATION_ERROR');
    });
  });
});
```

### Integration Tests
```typescript
// ✅ GOOD - Test API endpoints
describe('POST /api/v1/users', () => {
  it('should create user successfully', async () => {
    const response = await request(app)
      .post('/api/v1/users')
      .send({
        email: 'test@example.com',
        name: 'Test User'
      })
      .expect(201);
      
    expect(response.body.success).toBe(true);
    expect(response.body.data.email).toBe('test@example.com');
  });
});
```

## Security Requirements

### Authentication
```typescript
// ✅ GOOD - Proper JWT handling
const authenticateToken = (req: Request, res: Response, next: NextFunction) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({
      success: false,
      error: { code: 'UNAUTHORIZED', message: 'Access token required' }
    });
  }
  
  jwt.verify(token, process.env.JWT_SECRET!, (err, user) => {
    if (err) {
      return res.status(403).json({
        success: false,
        error: { code: 'FORBIDDEN', message: 'Invalid token' }
      });
    }
    req.user = user;
    next();
  });
};
```

### Input Validation
```typescript
// ✅ GOOD - Comprehensive validation
const createUserSchema = z.object({
  email: z.string().email('Invalid email format'),
  name: z.string().min(2, 'Name must be at least 2 characters'),
  password: z.string().min(8, 'Password must be at least 8 characters')
});

const validateCreateUser = (req: Request, res: Response, next: NextFunction) => {
  try {
    createUserSchema.parse(req.body);
    next();
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({
        success: false,
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid input data',
          details: error.errors
        }
      });
    }
    next(error);
  }
};
```

## Performance Guidelines

### Database Optimization
- **Indexes**: Tạo indexes cho frequently queried fields
- **Pagination**: Implement cursor-based pagination cho large datasets
- **N+1 Queries**: Sử dụng `include` hoặc `select` để avoid N+1 problems

### Frontend Optimization
- **Code Splitting**: Sử dụng React.lazy() cho route-based splitting
- **Memoization**: Sử dụng useMemo và useCallback appropriately
- **Bundle Size**: Monitor và optimize bundle size

### Caching Strategy
- **API Responses**: Cache frequently accessed data
- **Static Assets**: Implement proper cache headers
- **Database Queries**: Use Redis cho expensive queries

## Deployment Standards

### Environment Configuration
```typescript
// ✅ GOOD - Environment-specific configs
const config = {
  development: {
    database: {
      url: process.env.DATABASE_URL!,
      logging: true
    },
    cors: {
      origin: 'http://localhost:3000'
    }
  },
  production: {
    database: {
      url: process.env.DATABASE_URL!,
      logging: false
    },
    cors: {
      origin: process.env.FRONTEND_URL!
    }
  }
};
```

### Docker Configuration
```dockerfile
# ✅ GOOD - Multi-stage build
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

FROM node:18-alpine AS runtime
WORKDIR /app
COPY --from=builder /app/node_modules ./node_modules
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

## Quality Gates

### Pre-commit Checks
- ✅ ESLint passes without errors
- ✅ TypeScript compilation successful
- ✅ Unit tests pass với minimum 80% coverage
- ✅ Prettier formatting applied

### Pre-deployment Checks
- ✅ Integration tests pass
- ✅ Security scan completed
- ✅ Performance benchmarks met
- ✅ Database migrations tested

---

*Note: These rules are specific to the Base AI Project. For personal preferences that apply across all projects, see user_rules.md*