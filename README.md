# AICogniBlog

一个基于 Spring Boot + Vue 3 构建的现代化博客系统，集成 AI 智能审核功能。

## 📖 项目介绍

AICogniBlog 是一个功能完善的全栈博客平台，采用前后端分离架构开发。系统提供了文章发布、评论互动、用户关注、内容订阅等核心功能，并集成了 AI 智能内容审核能力，为用户提供安全、流畅的博客体验。

### 核心功能

- **用户系统**
  - 用户注册/登录（RSA 加密传输密码）
  - 个人资料管理（头像、昵称、简介）
  - 密码修改
  - 用户关注/取消关注
  - 内容订阅

- **文章管理**
  - Markdown 编辑器支持
  - 文章发布/编辑/删除
  - 草稿保存
  - 分类和标签管理
  - 文章点赞
  - 浏览历史记录

- **评论系统**
  - 文章评论
  - 评论回复（支持多级回复）
  - 评论点赞
  - 评论管理

- **管理后台**
  - 用户管理（启用/禁用账号）
  - 文章管理
  - 评论审核
  - AI 智能内容审核

- **AI 功能**
  - 集成 DeepSeek API
  - 智能内容审核
  - 违规内容检测

### 技术特点

- 前后端分离架构
- RESTful API 设计
- JWT 身份认证
- RSA 非对称加密
- MyBatis-Plus ORM
- 响应式 UI 设计
- Markdown 实时预览
- 代码高亮显示

## 🏗️ 项目结构

```
AICogniBlog/
├── blog-backend/              # 后端项目（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aicogniblog/
│   │   │   │   ├── ai/                    # AI 服务模块
│   │   │   │   │   ├── controller/        # AI 审核控制器
│   │   │   │   │   ├── dto/               # AI 数据传输对象
│   │   │   │   │   └── service/           # AI 服务实现
│   │   │   │   ├── article/               # 文章模块
│   │   │   │   │   ├── controller/        # 文章控制器
│   │   │   │   │   ├── dto/               # 文章 DTO
│   │   │   │   │   ├── entity/            # 文章实体类
│   │   │   │   │   ├── mapper/            # MyBatis Mapper
│   │   │   │   │   └── service/           # 文章服务
│   │   │   │   ├── auth/                  # 认证授权模块
│   │   │   │   │   ├── controller/        # 认证控制器
│   │   │   │   │   ├── dto/               # 登录/注册 DTO
│   │   │   │   │   ├── entity/            # 用户实体
│   │   │   │   │   ├── mapper/            # 用户 Mapper
│   │   │   │   │   ├── security/          # Spring Security 配置
│   │   │   │   │   └── service/           # 认证服务
│   │   │   │   ├── comment/               # 评论模块
│   │   │   │   │   ├── controller/        # 评论控制器
│   │   │   │   │   ├── dto/               # 评论 DTO
│   │   │   │   │   ├── entity/            # 评论实体
│   │   │   │   │   ├── mapper/            # 评论 Mapper
│   │   │   │   │   └── service/           # 评论服务
│   │   │   │   ├── user/                  # 用户模块
│   │   │   │   │   ├── controller/        # 用户控制器
│   │   │   │   │   ├── dto/               # 用户 DTO
│   │   │   │   │   ├── entity/            # 关注/订阅实体
│   │   │   │   │   ├── mapper/            # 用户 Mapper
│   │   │   │   │   └── service/           # 用户服务
│   │   │   │   ├── common/                # 公共模块
│   │   │   │   │   ├── config/            # 配置类
│   │   │   │   │   ├── exception/         # 异常处理
│   │   │   │   │   ├── result/            # 统一响应结果
│   │   │   │   │   └── util/              # 工具类
│   │   │   │   └── AICogniBlogApplication.java  # 启动类
│   │   │   └── resources/
│   │   │       ├── db/                    # 数据库脚本
│   │   │       │   ├── schema-full.sql    # 完整建表脚本
│   │   │       │   ├── schema-incremental.sql  # 增量更新脚本
│   │   │       │   └── init.sql           # 初始化数据
│   │   │       └── application.yml        # 应用配置
│   │   └── test/                          # 测试代码
│   ├── pom.xml                            # Maven 配置
│   ├── run-tests.bat                      # Windows 测试脚本
│   └── run-tests.sh                       # Linux/Mac 测试脚本
│
├── blog-frontend/             # 前端项目（Vue 3 + TypeScript）
│   ├── src/
│   │   ├── api/                           # API 接口封装
│   │   │   ├── article.ts                 # 文章 API
│   │   │   ├── auth.ts                    # 认证 API
│   │   │   ├── comment.ts                 # 评论 API
│   │   │   ├── follow.ts                  # 关注 API
│   │   │   ├── subscription.ts            # 订阅 API
│   │   │   ├── user.ts                    # 用户 API
│   │   │   └── http.ts                    # HTTP 客户端
│   │   ├── assets/                        # 静态资源
│   │   │   └── styles/                    # 样式文件
│   │   ├── components/                    # 公共组件
│   │   │   └── layout/                    # 布局组件
│   │   │       └── BlogLayout.vue         # 博客主布局
│   │   ├── router/                        # 路由配置
│   │   │   └── index.ts                   # 路由定义
│   │   ├── stores/                        # Pinia 状态管理
│   │   │   ├── auth.ts                    # 认证状态
│   │   │   └── theme.ts                   # 主题状态
│   │   ├── utils/                         # 工具函数
│   │   │   └── crypto.ts                  # 加密工具
│   │   ├── views/                         # 页面组件
│   │   │   ├── admin/                     # 管理后台页面
│   │   │   ├── auth/                      # 认证页面
│   │   │   └── blog/                      # 博客页面
│   │   ├── App.vue                        # 根组件
│   │   └── main.ts                        # 入口文件
│   ├── e2e/                               # E2E 测试
│   ├── package.json                       # NPM 配置
│   ├── vite.config.ts                     # Vite 配置
│   ├── tsconfig.json                      # TypeScript 配置
│   └── playwright.config.ts               # Playwright 配置
│
├── README.md                  # 项目说明文档
├── TESTING.md                 # 测试文档
├── PASSWORD_ENCRYPTION.md     # 密码加密说明
└── CLAUDE.md                  # AI 开发记录

```

## 🛠️ 技术栈

### 后端技术

- **框架**: Spring Boot 3.4.3
- **Java 版本**: JDK 17
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.9
- **安全**: Spring Security + JWT
- **加密**: BCrypt (密码) + RSA (传输)
- **工具库**: Hutool 5.8.35
- **Markdown**: CommonMark 0.24.0
- **构建工具**: Maven

### 前端技术

- **框架**: Vue 3.5.25
- **语言**: TypeScript 5.9.3
- **UI 组件**: Element Plus 2.13.2
- **状态管理**: Pinia 3.0.4
- **路由**: Vue Router 4.6.4
- **HTTP 客户端**: Axios 1.13.5
- **Markdown 编辑器**: ByteMD 1.22.0
- **加密**: JSEncrypt 3.5.4
- **构建工具**: Vite 7.3.1
- **测试**: Playwright 1.49.0

## 🚀 运行部署

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 后端部署

#### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE aicogniblog DEFAULT CHARACTER SET utf8mb4;

# 导入数据库脚本
mysql -u root -p aicogniblog < blog-backend/src/main/resources/db/schema-full.sql
mysql -u root -p aicogniblog < blog-backend/src/main/resources/db/init.sql
```

#### 2. 配置文件修改

编辑 `blog-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aicogniblog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root          # 修改为你的数据库用户名
    password: root          # 修改为你的数据库密码

app:
  jwt:
    secret: your-jwt-secret-key-here  # 修改为你的 JWT 密钥（至少32字符）
  ai:
    api-key: your-deepseek-api-key-here  # 配置 DeepSeek API Key（可选）
```

#### 3. 编译运行

```bash
# 进入后端目录
cd blog-backend

# 使用 Maven 编译
mvn clean package -DskipTests

# 运行应用
java -jar target/blog-backend-1.0.0.jar

# 或者直接使用 Maven 运行
mvn spring-boot:run
```

后端服务将在 `http://localhost:8089` 启动。

#### 4. 运行测试（可选）

```bash
# Windows
run-tests.bat

# Linux/Mac
chmod +x run-tests.sh
./run-tests.sh
```

### 前端部署

#### 1. 安装依赖

```bash
# 进入前端目录
cd blog-frontend

# 安装依赖
npm install
```

#### 2. 配置后端地址

编辑 `blog-frontend/src/api/http.ts`，确认后端 API 地址：

```typescript
const apiClient = axios.create({
  baseURL: 'http://localhost:8089',  // 后端服务地址
  timeout: 10000,
});
```

#### 3. 开发模式运行

```bash
# 启动开发服务器
npm run dev
```

前端开发服务器将在 `http://localhost:5173` 启动。

#### 4. 生产环境构建

```bash
# 构建生产版本
npm run build

# 构建产物在 dist 目录
# 可以使用 Nginx 或其他 Web 服务器部署
```

#### 5. 运行 E2E 测试（可选）

```bash
# 运行端到端测试
npm run test:e2e

# 使用 UI 模式运行测试
npm run test:e2e:ui
```

### Docker 部署（推荐）

#### 后端 Dockerfile

在 `blog-backend` 目录创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/blog-backend-1.0.0.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 前端 Dockerfile

在 `blog-frontend` 目录创建 `Dockerfile`：

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Docker Compose 部署

在项目根目录创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: aicogniblog-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: aicogniblog
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./blog-backend/src/main/resources/db:/docker-entrypoint-initdb.d
    command: --default-authentication-plugin=mysql_native_password

  backend:
    build: ./blog-backend
    container_name: aicogniblog-backend
    ports:
      - "8089:8089"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/aicogniblog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root

  frontend:
    build: ./blog-frontend
    container_name: aicogniblog-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql-data:
```

运行命令：

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 生产环境配置建议

1. **数据库优化**
   - 配置数据库连接池
   - 启用慢查询日志
   - 定期备份数据

2. **安全配置**
   - 修改默认密码和密钥
   - 启用 HTTPS
   - 配置防火墙规则
   - 限制 API 访问频率

3. **性能优化**
   - 启用 Redis 缓存
   - 配置 CDN 加速静态资源
   - 启用 Gzip 压缩
   - 数据库索引优化

4. **监控告警**
   - 配置应用监控（如 Spring Boot Actuator）
   - 设置日志收集
   - 配置错误告警

## 📝 默认账号

系统初始化后会创建以下测试账号：

- **管理员账号**
  - 用户名: `admin`
  - 密码: `Admin123!`

- **普通用户账号**
  - 用户名: `testuser`
  - 密码: `Test123!`

**⚠️ 生产环境请务必修改默认密码！**

## 📚 API 文档

后端 API 遵循 RESTful 设计规范，主要接口包括：

- **认证接口**: `/api/auth/*`
- **用户接口**: `/api/users/*`
- **文章接口**: `/api/articles/*`
- **评论接口**: `/api/comments/*`
- **管理接口**: `/api/admin/*`

详细 API 文档可以通过 Swagger UI 访问（需要配置 Swagger 依赖）。

## 🧪 测试

项目包含完整的测试套件：

- **后端单元测试**: JUnit 5 + Mockito
- **后端集成测试**: Spring Boot Test + H2
- **前端 E2E 测试**: Playwright

运行测试命令参见上文"运行测试"部分。

## 📄 许可证

本项目仅供学习交流使用。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题或建议，请通过 Issue 反馈。

---

**AICogniBlog Team** - 使用 AI 辅助构建的现代化博客系统
