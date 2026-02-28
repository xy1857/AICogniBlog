# 测试文档

## 概述

本项目包含完整的单元测试和集成测试，覆盖了文章管理的核心功能。

## 测试结构

### 1. 单元测试

#### ArticleServiceTest
- 测试文章服务层的业务逻辑
- 覆盖功能：
  - 创建文章（发布/草稿）
  - 更新文章
  - 删除文章
  - 获取文章详情
  - 点赞/取消点赞
  - 列出文章（分类、标签、关键词筛选）
  - 草稿管理
  - 浏览历史

#### ArticleMapperTest
- 测试数据访问层的CRUD操作
- 覆盖功能：
  - 插入、查询、更新、删除
  - 条件查询（状态、作者、分类）
  - 模糊查询
  - 统计查询

### 2. 集成测试

#### ArticleControllerIntegrationTest
- 测试完整的HTTP请求响应流程
- 覆盖功能：
  - GET /api/articles - 列出文章
  - GET /api/articles/{id} - 获取文章详情
  - POST /api/articles - 创建文章
  - PUT /api/articles/{id} - 更新文章
  - DELETE /api/articles/{id} - 删除文章
  - POST /api/articles/{id}/like - 点赞
  - DELETE /api/articles/{id}/like - 取消点赞
  - GET /api/articles/drafts - 列出草稿
  - GET /api/articles/{id}/edit - 获取编辑数据
  - GET /api/categories - 列出分类
  - GET /api/tags - 列出标签
  - 权限验证（用户/管理员）

## 运行测试

### 运行所有测试
```bash
cd blog-backend
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=ArticleServiceTest
mvn test -Dtest=ArticleControllerIntegrationTest
mvn test -Dtest=ArticleMapperTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=ArticleServiceTest#testCreateArticle_Success
```

### 生成测试报告
```bash
mvn test
# 报告位置: target/surefire-reports/
```

## 测试配置

- 使用 H2 内存数据库进行测试
- 配置文件：`src/test/resources/application-test.yml`
- 数据库初始化脚本：`src/test/resources/schema.sql`
- 每个测试方法执行前自动初始化数据库
- 使用 `@Transactional` 确保测试隔离

## 测试覆盖率

主要测试覆盖：
- ✅ 文章CRUD操作
- ✅ 文章状态管理（草稿/发布）
- ✅ 文章筛选（分类、标签、关键词）
- ✅ 点赞功能
- ✅ 浏览历史
- ✅ 权限验证
- ✅ 数据验证
- ✅ 异常处理

## 注意事项

1. 测试使用独立的测试配置文件 `application-test.yml`
2. 测试数据库使用 H2 内存数据库，不影响开发/生产环境
3. 每个测试方法都是独立的，使用 `@Transactional` 自动回滚
4. 测试中使用 `@BeforeEach` 初始化测试数据
5. 集成测试使用 MockMvc 模拟 HTTP 请求




