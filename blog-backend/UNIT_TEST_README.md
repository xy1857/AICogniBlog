# ArticleController 单元测试说明

## 修复的问题

### 1. 导入错误
- **问题**: 使用了不存在的 `AuthorVO` 和 `CategoryVO` 类
- **修复**: 改用 `ArticleVO.AuthorInfo` 和 `ArticleVO.CategoryInfo` 内部类

### 2. PageResult 字段名称
- **问题**: 使用了错误的字段名 `page` 和 `size`
- **修复**: 改用正确的字段名 `current` 和 `pages`

## 运行测试

### 方式 1: 使用批处理文件（推荐）
```bash
cd blog-backend
run-unit-test.bat
```

### 方式 2: 使用 Maven 命令
```bash
cd blog-backend
mvn test -Dtest=ArticleControllerTest
```

### 方式 3: 在 IDE 中运行
- 打开 `ArticleControllerTest.java`
- 右键点击类名或方法名
- 选择 "Run" 或 "Debug"

## 测试覆盖范围

### ✅ 基础功能测试
- 列出文章（分页、筛选、搜索）
- 获取文章详情（已登录/未登录）
- 创建文章
- 更新文章
- 删除文章

### ✅ 权限测试
- 作者本人可以编辑/删除
- 管理员可以编辑/删除任何文章
- 其他用户无权限编辑/删除

### ✅ 交互功能测试
- 点赞文章
- 取消点赞
- 获取编辑数据

### ✅ 辅助功能测试
- 列出草稿
- 列出分类
- 列出标签
- 空结果处理

## 单元测试 vs 集成测试对比

| 特性 | 单元测试 (ArticleControllerTest) | 集成测试 (ArticleControllerIntegrationTest) |
|------|----------------------------------|---------------------------------------------|
| 启动容器 | ❌ 否 | ✅ 是 |
| 数据库 | ❌ Mock | ✅ H2 真实数据库 |
| 执行速度 | ⚡⚡⚡ 快 (~1-2秒) | ⚡ 慢 (~10-20秒) |
| 依赖注入 | @Mock + @InjectMocks | @Autowired |
| HTTP 层 | ❌ 直接调用方法 | ✅ MockMvc 模拟 HTTP |
| 适用场景 | 快速验证业务逻辑 | 完整的端到端测试 |

## 测试示例

### 单元测试写法
```java
@Test
void testGetArticle_WithAuth() {
    // Given - 准备 Mock 数据
    when(authentication.getPrincipal()).thenReturn(1L);
    when(articleService.getArticleById(1L, 1L)).thenReturn(testArticleVO);

    // When - 直接调用方法
    Result<ArticleVO> result = articleController.getArticle(1L, authentication);

    // Then - 验证结果
    assertThat(result.getCode()).isEqualTo(200);
    verify(articleService).getArticleById(1L, 1L);
}
```

### 集成测试写法
```java
@Test
void testGetArticle() throws Exception {
    // Given - 准备真实数据
    Long articleId = articleService.createArticle(request, testUserId);

    // When & Then - 模拟 HTTP 请求
    mockMvc.perform(get("/api/articles/{id}", articleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));
}
```

## 最佳实践

1. **开发阶段**: 优先使用单元测试，快速验证逻辑
2. **提交前**: 运行集成测试，确保端到端功能正常
3. **CI/CD**: 两种测试都运行，确保代码质量

## 依赖说明

单元测试只需要以下依赖：
- JUnit 5
- Mockito
- AssertJ (可选，用于更流畅的断言)

不需要：
- Spring Boot Test
- H2 Database
- MockMvc

