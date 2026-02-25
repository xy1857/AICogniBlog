# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Communication Rule

**Use Chinese (中文) for all responses and explanations** when working in this repository.

## Project Overview

AICogniBlog is a full-stack blog system with AI-powered content moderation. It uses Spring Boot 3.x backend with Vue 3 frontend, integrating DeepSeek AI for automated comment auditing.

## Development Commands

### Backend (blog-backend/)
```bash
# Build and run (requires Java 25+ and Maven)
mvn clean install
mvn spring-boot:run

# Run tests
mvn test
```

The backend runs on `http://localhost:8080`

### Frontend (blog-frontend/)
```bash
# Install dependencies and run dev server (requires Node.js 20+)
cd blog-frontend
npm install
npm run dev      # Runs on port 5173
npm run build    # Production build to dist/
npm run preview  # Preview production build
```

### Database Setup

Create database and run init script:
```sql
CREATE DATABASE aicogniblog DEFAULT CHARACTER SET utf8mb4;
-- Then execute blog-backend/src/main/resources/db/init.sql
```

Default admin: username `admin`, password `Admin@123`

## Architecture

### Module Structure

**Backend modules** (blog-backend/src/main/java/com/aicogniblog/):
- `auth/` - Authentication (JWT-based), user registration/login
- `article/` - Article CRUD, categories, tags with Markdown-to-HTML rendering
- `comment/` - Nested comments with AI moderation status tracking
- `ai/` - DeepSeek AI integration for async comment auditing
- `user/` - User profile and admin user management
- `common/` - Global exception handling, result wrappers, async config

**Frontend structure** (blog-frontend/src/):
- `api/` - Axios HTTP clients for backend communication
- `stores/` - Pinia stores (auth state management)
- `router/` - Vue Router with authentication guards
- `views/` - Page components (auth, blog, admin sections)
- `components/layout/` - Layout templates (BlogLayout, AdminLayout)

### Key Technologies

- **Backend**: Spring Boot 3.4.3, Spring Security + JWT, MyBatis-Plus 3.5.9, MySQL
- **Frontend**: Vue 3 Composition API, TypeScript, Vite, Element Plus, Pinia, ByteMD (markdown editor)
- **AI**: DeepSeek API for content moderation via Java 11+ HttpClient

### Authentication Flow

1. JWT tokens stored in localStorage (frontend)
2. JwtFilter intercepts requests (except `/api/auth/**` and public GET endpoints)
3. Role-based access: `USER` (0) vs `ADMIN` (1)
4. Admin-only endpoints prefixed with `/api/admin/**`

### AI Comment Moderation

When a comment is posted:
1. Comment saved with `status=0` (pending)
2. `AiService.auditCommentAsync()` called via `@Async` thread pool
3. DeepSeek API evaluates content safety
4. If safe: `status=1` (published), if unsafe: stays `status=0` for admin review
5. Results stored in `ai_audit_result` and `ai_reply_suggestion` fields

### Data Flow

**Article Publishing**: Markdown input → CommonMark library → HTML stored in `content_html`
**Comment Moderation**: User comment → Async AI audit → Status update → Admin approval if needed

## Configuration

**Backend** (blog-backend/src/main/resources/application.yml):
- Database: MySQL on localhost:3306
- JWT: 24-hour expiration
- AI API: Configure `app.ai.api-key` with DeepSeek API key

**Frontend** (blog-frontend/vite.config.ts):
- Dev server proxy: `/api` → `http://localhost:8080`
- Path alias: `@` → `src/`

## Code Conventions

- Backend uses Lombok for boilerplate reduction (`@Data`, `@RequiredArgsConstructor`)
- MyBatis-Plus with soft delete (`deleted` field)
- Controllers return `Result<T>` wrapper for consistent API responses
- Frontend uses Composition API with `<script setup lang="ts">`
- All DTOs in respective `dto/` packages
- Entities use snake_case database columns → camelCase Java properties