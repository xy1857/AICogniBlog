# 个人博客主页低代码装修功能

基于 Vue 3 + VueDraggable + Element Plus 的低代码页面装修系统。

## 功能特性

### 核心功能
- ✅ 拖拽式组件布局
- ✅ 实时可视化编辑
- ✅ 组件属性配置面板
- ✅ 主题颜色自定义
- ✅ 撤销/重做操作
- ✅ 页面预览
- ✅ 本地存储/云端保存

### 5个可配置组件

#### 1. Hero横幅 (HeroBanner)
- 背景类型：图片/视频/渐变
- 标题、副标题、CTA按钮
- 内容位置：左/中/右
- 特效：视差滚动、打字机效果

#### 2. 文章网格 (ArticleGrid)
- 列数配置（1-4列）
- 卡片样式：默认/简约/杂志
- 显示项：封面/摘要/标签/日期/阅读量
- 加载方式：分页/无限滚动
- 悬停效果：上浮/缩放

#### 3. 分类导航 (CategoryNav)
- 样式：标签页/侧边栏/下拉菜单/药丸
- 显示图标和文章数量
- 自定义分类颜色

#### 4. 友情链接 (FriendLinks)
- 展示模式：卡片/列表/轮播
- 列数配置
- 显示Logo和描述
- 悬停效果：缩放/发光

#### 5. 互动组件 (InteractiveWidget)
- 类型：留言板/评论墙/访客地图
- 主题：自动/浅色/深色
- 表情支持
- 头像显示

## 技术架构

```
src/
├── components/page-builder/
│   ├── widgets/              # 5个可配置组件
│   │   ├── HeroBanner.vue
│   │   ├── ArticleGrid.vue
│   │   ├── CategoryNav.vue
│   │   ├── FriendLinks.vue
│   │   └── InteractiveWidget.vue
│   ├── ComponentLibrary.vue  # 组件库面板
│   ├── ConfigPanel.vue       # 配置面板
│   └── componentRegistry.ts  # 组件注册表
├── stores/
│   └── pageBuilder.ts        # 状态管理
├── types/
│   └── page-builder.ts       # 类型定义
├── api/
│   └── page.ts              # API接口
└── views/
    ├── PageBuilder.vue       # 装修器主界面
    └── PagePreview.vue       # 预览页面
```

## 使用方法

### 1. 访问装修器
```
http://localhost:5173/page-builder
```

### 2. 添加组件
- 从左侧组件库拖拽组件到画布
- 或点击组件直接添加

### 3. 配置组件
- 点击画布中的组件选中
- 在右侧配置面板修改属性
- 支持实时预览

### 4. 调整布局
- 拖动组件上的拖拽手柄调整顺序
- 复制或删除组件

### 5. 主题设置
- 切换到"主题设置"标签
- 配置全局颜色、字体等

### 6. 保存和预览
- 点击"保存"按钮保存配置
- 点击"预览"查看最终效果

## 配置示例

### 组件配置
```typescript
{
  id: 'HeroBanner-1234567890',
  type: 'HeroBanner',
  position: 0,
  visible: true,
  props: {
    backgroundType: 'gradient',
    gradientColors: ['#667eea', '#764ba2'],
    title: '欢迎来到我的博客',
    subtitle: '分享技术，记录生活',
    height: '500px',
    enableTypingEffect: true
  },
  style: {
    backgroundColor: 'transparent',
    padding: '2rem',
    margin: '0'
  }
}
```

### 主题配置
```typescript
{
  primaryColor: '#409EFF',
  backgroundColor: '#f5f7fa',
  textColor: '#303133',
  fontFamily: 'Inter, system-ui, sans-serif',
  borderRadius: '8px'
}
```

## 快捷键

- `Ctrl + Z` - 撤销
- `Ctrl + Y` - 重做
- `Delete` - 删除选中组件

## 数据存储

### 本地存储
配置自动保存到 `localStorage`，刷新页面不丢失。

### 云端存储（待实现）
```typescript
// 保存到后端
await updatePage(pageId, pageConfig);

// 加载配置
const config = await getPageConfig(pageId);
```

## 扩展开发

### 添加新组件

1. 创建组件文件
```vue
<!-- src/components/page-builder/widgets/MyWidget.vue -->
<template>
  <div class="my-widget">
    {{ props.title }}
  </div>
</template>

<script setup lang="ts">
const props = defineProps<MyWidgetProps>();
</script>
```

2. 定义类型
```typescript
// src/types/page-builder.ts
export interface MyWidgetProps {
  title: string;
  // ...
}
```

3. 注册组件
```typescript
// src/components/page-builder/componentRegistry.ts
export const componentRegistry = {
  // ...
  MyWidget: {
    type: 'MyWidget',
    name: '我的组件',
    icon: 'Star',
    description: '组件描述',
    defaultProps: { title: '默认标题' },
    schema: { /* ... */ }
  }
}
```

4. 在装修器中引入
```typescript
// src/views/PageBuilder.vue
import MyWidget from './widgets/MyWidget.vue';

const getComponentByType = (type: ComponentType) => {
  return {
    // ...
    MyWidget
  }[type];
};
```

## 后端API接口（待实现）

```java
// PageController.java
@RestController
@RequestMapping("/api/pages")
public class PageController {
    
    @GetMapping("/{id}")
    public PageConfig getPage(@PathVariable String id) { }
    
    @GetMapping("/my")
    public List<PageConfig> getMyPages() { }
    
    @PostMapping
    public PageConfig createPage(@RequestBody PageConfig config) { }
    
    @PutMapping("/{id}")
    public PageConfig updatePage(@PathVariable String id, @RequestBody PageConfig config) { }
    
    @DeleteMapping("/{id}")
    public void deletePage(@PathVariable String id) { }
    
    @PostMapping("/{id}/publish")
    public void publishPage(@PathVariable String id) { }
}
```

## 浏览器兼容性

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 依赖包

```json
{
  "vuedraggable": "^4.1.0",
  "@vueuse/core": "^10.0.0",
  "element-plus": "^2.13.2",
  "pinia": "^3.0.4"
}
```

## 开发计划

- [ ] 后端API集成
- [ ] 组件市场
- [ ] 模板库
- [ ] 响应式预览
- [ ] 导出为静态HTML
- [ ] 版本历史
- [ ] 协作编辑

## 截图

装修器界面包含：
- 左侧：组件库（可拖拽）
- 中间：画布区（实时编辑）
- 右侧：配置面板（属性编辑）
- 顶部：工具栏（撤销/重做/预览/保存）

