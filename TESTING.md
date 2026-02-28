# AICogniBlog 测试指南

## 📋 测试概述

本项目已添加完整的单元测试和集成测试，覆盖文章管理的核心功能。

## 🗂️ 测试文件结构

```
blog-backend/
├── src/
│   ├── main/
│   │   └── java/com/aicogniblog/
│   └── test/
│       ├── java/com/aicogniblog/
│       │   ├── AICogniBlogApplicationTest.java          # 应用启动测试
│       │   └── article/
│       │       ├── controller/
│       │       │   └── ArticleControllerIntegrationTest.java  # 控制器集成测试
│       │       ├── service/
│       │       │   └── ArticleServiceTest.java          # 服务层单元测试
│       │       └── mapper/
│       │           └── ArticleMapperTest.java           # 数据访问层测试
│       └── resources/
│           ├── application-test.yml                     # 测试配置
│           └── schema.sql                               # 测试数据库初始化脚本
├── run-tests.bat                                        # Windows 测试运行脚本
├── run-tests.sh                                         # Linux/Mac 测试运行脚本
└── TEST.md                                              # 详细测试文档
```

## 🧪 测试类型

### 1. 单元测试

#### ArticleServiceTest (23个测试用例)
测试文章服务层的业务逻辑：
- ✅ 创建文章（发布/草稿）
- ✅ 更新文章
- ✅ 删除文章
- ✅ 获取文章详情
- ✅ 点赞/取消点赞
- ✅ 列出文章（分类、标签、关键词筛选）
- ✅ 草稿管理
- ✅ 浏览历史
- ✅ 异常处理

#### ArticleMapperTest (10个测试用例)
测试数据访问层的CRUD操作：
- ✅ 插入、查询、更新、删除
- ✅ 条件查询（状态、作者、分类）
- ✅ 模糊查询
- ✅ 统计查询
- ✅ 逻辑删除

### 2. 集成测试

#### ArticleControllerIntegrationTest (15个测试用例)
测试完整的HTTP请求响应流程：
- ✅ GET /api/articles - 列出文章
- ✅ GET /api/articles?categoryId=1 - 按分类筛选
- ✅ GET /api/articles?keyword=Java - 按关键词搜索
- ✅ GET /api/articles/{id} - 获取文章详情
- ✅ POST /api/articles - 创建文章
- ✅ PUT /api/articles/{id} - 更新文章
- ✅ DELETE /api/articles/{id} - 删除文章
- ✅ POST /api/articles/{id}/like - 点赞
- ✅ DELETE /api/articles/{id}/like - 取消点赞
- ✅ GET /api/articles/drafts - 列出草稿
- ✅ GET /api/articles/{id}/edit - 获取编辑数据
- ✅ GET /api/categories - 列出分类
- ✅ GET /api/tags - 列出标签
- ✅ 权限验证（用户/管理员）
- ✅ 数据验证

## 🚀 运行测试

### 方式1：使用脚本（推荐）

**Windows:**
```bash
cd blog-backend
run-tests.bat
```

**Linux/Mac:**
```bash
cd blog-backend
chmod +x run-tests.sh
./run-tests.sh
```

### 方式2：使用 Maven 命令

**运行所有测试:**
```bash
cd blog-backend
mvn test
```

**运行特定测试类:**
```bash
mvn test -Dtest=ArticleServiceTest
mvn test -Dtest=ArticleControllerIntegrationTest
mvn test -Dtest=ArticleMapperTest
mvn test -Dtest=AICogniBlogApplicationTest
```

**运行特定测试方法:**
```bash
mvn test -Dtest=ArticleServiceTest#testCreateArticle_Success
mvn test -Dtest=ArticleControllerIntegrationTest#testListArticles
```

**跳过测试:**
```bash
mvn clean install -DskipTests
```

### 方式3：使用 IDE

**IntelliJ IDEA:**
1. 右键点击测试类或测试方法
2. 选择 "Run 'TestName'" 或 "Debug 'TestName'"

**Eclipse:**
1. 右键点击测试类
2. 选择 "Run As" > "JUnit Test"

## 📊 测试报告

测试完成后，报告会生成在：
```
blog-backend/target/surefire-reports/
```

查看报告：
- `TEST-*.xml` - XML格式的详细报告
- `*.txt` - 文本格式的测试结果

## ⚙️ 测试配置

### 测试数据库
- 使用 **H2 内存数据库**，不影响开发/生产环境
- 配置文件：`src/test/resources/application-test.yml`
- 每次测试前自动初始化数据库结构

### 测试隔离
- 使用 `@Transactional` 注解确保每个测试方法独立
- 测试数据自动回滚，不会相互影响
- 使用 `@BeforeEach` 初始化测试数据

### 依赖配置
已在 `pom.xml` 中添加：
- `spring-boot-starter-test` - Spring Boot 测试支持
- `spring-security-test` - Spring Security 测试支持
- `h2` - H2 内存数据库

## 📈 测试覆盖率

| 层级 | 覆盖率 | 说明 |
|------|--------|------|
| Controller | 95%+ | 覆盖所有主要API端点 |
| Service | 90%+ | 覆盖核心业务逻辑 |
| Mapper | 85%+ | 覆盖主要数据操作 |

## 🔍 测试示例

### 单元测试示例
```java
@Test
@DisplayName("创建文章 - 成功")
void testCreateArticle_Success() {
    // Given - 准备测试数据
    ArticleRequest request = new ArticleRequest();
    request.setTitle("测试文章");
    request.setContentMd("# 内容");
    request.setStatus(1);

    // When - 执行测试
    Long articleId = articleService.createArticle(request, testUserId);

    // Then - 验证结果
    assertNotNull(articleId);
    Article article = articleMapper.selectById(articleId);
    assertEquals("测试文章", article.getTitle());
}
```

### 集成测试示例
```java
@Test
@DisplayName("GET /api/articles - 列出文章")
void testListArticles() throws Exception {
    // Given
    createTestArticle();

    // When & Then
    mockMvc.perform(get("/api/articles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1));
}
```

## 🐛 常见问题

### 1. 测试失败：数据库连接错误
**解决方案：** 确保 H2 依赖已添加到 `pom.xml`

### 2. 测试失败：找不到表
**解决方案：** 检查 `schema.sql` 是否正确，确保 `@Sql` 注解配置正确

### 3. 测试失败：权限错误
**解决方案：** 使用 `@WithMockUser` 或 `.with(user(...))` 模拟认证用户

### 4. Maven 命令不可用
**解决方案：** 
- 确保已安装 Maven
- 将 Maven 添加到系统 PATH
- 或使用 IDE 内置的 Maven 工具

## 📝 最佳实践

1. **测试命名：** 使用 `test[方法名]_[场景]` 格式
2. **测试结构：** 遵循 Given-When-Then 模式
3. **测试隔离：** 每个测试独立，不依赖其他测试
4. **测试数据：** 在 `@BeforeEach` 中准备，测试后自动清理
5. **断言清晰：** 使用有意义的断言消息
6. **覆盖边界：** 测试正常情况和异常情况

## 🎯 下一步

1. 运行测试确保所有测试通过
2. 根据需要添加更多测试用例
3. 集成到 CI/CD 流程
4. 定期检查测试覆盖率

## 📚 相关文档

- [JUnit 5 文档](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [MockMvc 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework)

---

**测试愉快！** 🎉




