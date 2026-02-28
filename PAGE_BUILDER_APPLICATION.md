# 页面装修应用方案

## 方案概述

装修完成后，有以下几种应用方式：

### 方案一：替换现有首页（推荐）

**适用场景**：用户想用装修的页面完全替换默认首页

**实现步骤**：

1. **在装修器中标记为"首页"**
   - 添加一个"设为首页"按钮
   - 保存配置时标记 `isHomePage: true`

2. **修改路由逻辑**
   ```typescript
   // router/index.ts
   {
     path: '/',
     component: () => import('@/components/layout/BlogLayout.vue'),
     children: [
       { 
         path: '', 
         name: 'Home', 
         component: () => {
           // 检查是否有自定义首页
           const customHome = localStorage.getItem('customHomePage');
           if (customHome) {
             return import('@/views/PagePreview.vue');
           }
           return import('@/views/blog/HomeView.vue');
         }
       },
     ]
   }
   ```

3. **优点**：
   - 用户体验最直接
   - 完全自定义首页
   - 无需额外路由

4. **缺点**：
   - 需要保留原首页的访问方式
   - 可能需要回退机制

---

### 方案二：独立个人主页（推荐）

**适用场景**：保留原首页，装修页面作为个人主页展示

**实现步骤**：

1. **添加独立路由**
   ```typescript
   // router/index.ts
   {
     path: '/home/:username',
     name: 'UserHome',
     component: () => import('@/views/PagePreview.vue')
   }
   ```

2. **在用户菜单添加入口**
   ```vue
   <el-dropdown-item @click="$router.push(`/home/${auth.username}`)">
     <el-icon><House /></el-icon>
     我的主页
   </el-dropdown-item>
   ```

3. **分享链接**
   - 用户可以分享 `https://yourblog.com/home/username`
   - 其他人访问看到装修后的个人主页

4. **优点**：
   - 不影响原有首页
   - 每个用户有独立主页
   - 便于分享和展示

5. **缺点**：
   - 需要额外路由
   - 用户需要知道如何访问

---

### 方案三：主题切换模式

**适用场景**：提供多套主页样式供用户切换

**实现步骤**：

1. **预设多个模板**
   - 默认主页
   - 简约模式
   - 杂志模式
   - 自定义装修

2. **添加切换器**
   ```vue
   <el-select v-model="homePageMode" @change="switchHomePage">
     <el-option label="默认首页" value="default" />
     <el-option label="我的装修" value="custom" />
   </el-select>
   ```

3. **动态加载组件**
   ```typescript
   const HomeComponent = computed(() => {
     if (homePageMode.value === 'custom') {
       return PagePreview;
     }
     return HomeView;
   });
   ```

4. **优点**：
   - 灵活切换
   - 保留多种选择
   - 用户体验好

5. **缺点**：
   - 实现较复杂
   - 需要管理多个状态

---

### 方案四：发布/预览模式（最完整）

**适用场景**：专业博客系统，支持草稿和发布

**实现步骤**：

1. **添加发布状态**
   ```typescript
   interface PageConfig {
     id: string;
     name: string;
     status: 'draft' | 'published';
     publishedAt?: string;
     // ...
   }
   ```

2. **装修器添加发布按钮**
   ```vue
   <el-button type="success" @click="publishPage">
     发布到首页
   </el-button>
   ```

3. **后端API**
   ```java
   @PostMapping("/{id}/publish")
   public void publishPage(@PathVariable String id) {
     // 1. 更新页面状态为 published
     // 2. 将其他页面状态改为 draft
     // 3. 设置为当前用户的首页
   }
   ```

4. **前端加载逻辑**
   ```typescript
   async function loadHomePage() {
     try {
       // 尝试加载已发布的自定义首页
       const page = await getPublishedPage(userId);
       if (page) {
         return page;
       }
     } catch {
       // 降级到默认首页
       return null;
     }
   }
   ```

5. **优点**：
   - 最专业的方案
   - 支持版本管理
   - 可以回滚
   - 支持预览

6. **缺点**：
   - 需要完整的后端支持
   - 开发成本高

---

## 推荐实现方案

### 阶段一：快速实现（方案二）

```typescript
// 1. 修改 PagePreview.vue
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const pageConfig = ref<PageConfig | null>(null);

const loadPageConfig = async () => {
  const username = route.params.username;
  
  if (username) {
    // 从后端加载用户的发布页面
    // const config = await getPublicPage(username);
    
    // 临时方案：从 localStorage 加载
    const saved = localStorage.getItem(`pageConfig_${username}`);
    if (saved) {
      pageConfig.value = JSON.parse(saved);
    }
  } else {
    // 加载当前用户的配置
    const saved = localStorage.getItem('pageConfig');
    if (saved) {
      pageConfig.value = JSON.parse(saved);
    }
  }
};

onMounted(() => {
  loadPageConfig();
});
</script>
```

```typescript
// 2. 添加路由
{
  path: '/home/:username',
  name: 'UserHome',
  component: () => import('@/views/PagePreview.vue')
}
```

```vue
// 3. 在 BlogLayout.vue 添加菜单项
<el-dropdown-item @click="$router.push(`/home/${auth.username}`)">
  <el-icon><House /></el-icon>
  我的主页
</el-dropdown-item>
```

```typescript
// 4. 在 PageBuilder.vue 添加预览链接
const handleViewMyHome = () => {
  const username = auth.username;
  window.open(`/home/${username}`, '_blank');
};
```

### 阶段二：完整实现（方案四）

需要后端支持：

```java
// PageController.java
@RestController
@RequestMapping("/api/pages")
public class PageController {
    
    // 获取用户的已发布页面
    @GetMapping("/public/{username}")
    public PageConfig getPublicPage(@PathVariable String username) {
        return pageService.getPublishedPage(username);
    }
    
    // 发布页面
    @PostMapping("/{id}/publish")
    public void publishPage(@PathVariable String id) {
        pageService.publishPage(id);
    }
    
    // 取消发布
    @PostMapping("/{id}/unpublish")
    public void unpublishPage(@PathVariable String id) {
        pageService.unpublishPage(id);
    }
}
```

```sql
-- 数据库表结构
CREATE TABLE page_configs (
    id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    config JSON NOT NULL,
    status VARCHAR(20) DEFAULT 'draft',
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_status (user_id, status)
);
```

---

## 快速开始

### 最简单的方式（5分钟实现）

1. **添加"查看我的主页"按钮**

在 `PageBuilder.vue` 的工具栏添加：

```vue
<el-button :icon="View" @click="handleViewHome">
  查看我的主页
</el-button>
```

```typescript
const handleViewHome = () => {
  // 保存当前配置
  pageStore.saveToLocal();
  // 打开预览页面
  window.open('/page-preview', '_blank');
};
```

2. **用户访问流程**
   - 在装修器中设计页面
   - 点击"保存"
   - 点击"查看我的主页"
   - 在新标签页看到装修效果
   - 可以分享 `/page-preview` 链接

3. **优点**
   - 无需后端
   - 立即可用
   - 适合演示

---

## 总结

| 方案 | 难度 | 功能完整度 | 推荐度 |
|------|------|-----------|--------|
| 方案一：替换首页 | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| 方案二：独立主页 | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 方案三：主题切换 | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| 方案四：发布模式 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

**建议**：
- 快速原型：使用方案二（独立主页）
- 生产环境：使用方案四（发布模式）
- 个人博客：使用方案一（替换首页）

