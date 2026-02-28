<template>
  <div class="category-nav" :class="`style-${props.style}`">
    <div v-if="props.style === 'tabs'" class="nav-tabs">
      <div
        v-for="category in displayCategories"
        :key="category.id"
        class="tab-item"
        :class="{ active: activeCategory === category.id }"
        @click="handleCategoryClick(category.id)"
      >
        <el-icon v-if="props.showIcon && category.icon">
          <component :is="category.icon" />
        </el-icon>
        <span>{{ category.name }}</span>
        <span v-if="props.showCount" class="count">({{ getCategoryCount(category.id) }})</span>
      </div>
    </div>

    <div v-else-if="props.style === 'sidebar'" class="nav-sidebar">
      <div
        v-for="category in displayCategories"
        :key="category.id"
        class="sidebar-item"
        :class="{ active: activeCategory === category.id }"
        :style="{ borderLeftColor: category.color }"
        @click="handleCategoryClick(category.id)"
      >
        <div class="sidebar-content">
          <el-icon v-if="props.showIcon && category.icon" :style="{ color: category.color }">
            <component :is="category.icon" />
          </el-icon>
          <span class="name">{{ category.name }}</span>
        </div>
        <span v-if="props.showCount" class="count">{{ getCategoryCount(category.id) }}</span>
      </div>
    </div>

    <el-dropdown v-else-if="props.style === 'dropdown'" trigger="click" @command="handleCategoryClick">
      <el-button type="primary">
        分类导航
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="category in displayCategories"
            :key="category.id"
            :command="category.id"
          >
            <el-icon v-if="props.showIcon && category.icon">
              <component :is="category.icon" />
            </el-icon>
            {{ category.name }}
            <span v-if="props.showCount" class="count">({{ getCategoryCount(category.id) }})</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <div v-else-if="props.style === 'pills'" class="nav-pills">
      <div
        v-for="category in displayCategories"
        :key="category.id"
        class="pill-item"
        :class="{ active: activeCategory === category.id }"
        :style="activeCategory === category.id ? { backgroundColor: category.color } : {}"
        @click="handleCategoryClick(category.id)"
      >
        <el-icon v-if="props.showIcon && category.icon">
          <component :is="category.icon" />
        </el-icon>
        <span>{{ category.name }}</span>
        <span v-if="props.showCount" class="count">{{ getCategoryCount(category.id) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ArrowDown } from '@element-plus/icons-vue';
import { articleApi } from '@/api/article';
import type { CategoryNavProps } from '@/types/page-builder';

const props = withDefaults(defineProps<CategoryNavProps>(), {
  style: 'tabs',
  showIcon: true,
  showCount: true,
  categories: () => [],
});

const emit = defineEmits<{
  categoryChange: [categoryId: string]
}>();

const activeCategory = ref('all');
const realCategories = ref<any[]>([]);
const loading = ref(false);

// 加载真实分类数据
const loadCategories = async () => {
  loading.value = true;
  try {
    const response = await articleApi.categories();
    
    // 转换数据格式
    const categories = response.data.map((cat: any) => ({
      id: cat.id?.toString() || cat.slug,
      name: cat.name,
      icon: 'Folder', // 默认图标
      color: getRandomColor(),
      count: cat.articleCount || cat.count || 0,
    }));
    
    // 添加"全部"选项
    realCategories.value = [
      { id: 'all', name: '全部', icon: 'Grid', color: '#409EFF', count: categories.reduce((sum: number, c: any) => sum + c.count, 0) },
      ...categories,
    ];
  } catch (error) {
    console.error('Failed to load categories:', error);
    // 降级到默认分类
    realCategories.value = [
      { id: 'all', name: '全部', icon: 'Grid', color: '#409EFF', count: 0 },
      { id: 'tech', name: '技术', icon: 'Monitor', color: '#67C23A', count: 0 },
      { id: 'life', name: '生活', icon: 'Coffee', color: '#E6A23C', count: 0 },
      { id: 'travel', name: '旅行', icon: 'Location', color: '#F56C6C', count: 0 },
    ];
  } finally {
    loading.value = false;
  }
};

// 生成随机颜色
const getRandomColor = () => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#00D7FF'];
  return colors[Math.floor(Math.random() * colors.length)];
};

// 使用真实数据或配置的分类
const displayCategories = computed(() => {
  return props.categories.length > 0 ? props.categories : realCategories.value;
});

// 获取分类文章数量
const getCategoryCount = (categoryId: string) => {
  const category = displayCategories.value.find(c => c.id === categoryId);
  return category?.count || 0;
};

const handleCategoryClick = (categoryId: string) => {
  activeCategory.value = categoryId;
  emit('categoryChange', categoryId);
};

onMounted(() => {
  // 如果没有配置分类，则加载真实数据
  if (props.categories.length === 0) {
    loadCategories();
  }
});
</script>

<style scoped>
.category-nav {
  width: 100%;
}

/* Tabs 样式 */
.nav-tabs {
  display: flex;
  gap: 0.5rem;
  border-bottom: 2px solid #e4e7ed;
  overflow-x: auto;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  cursor: pointer;
  color: #606266;
  font-weight: 500;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.tab-item:hover {
  color: #409EFF;
}

.tab-item.active {
  color: #409EFF;
  border-bottom-color: #409EFF;
}

.tab-item .count {
  font-size: 0.875rem;
  color: #909399;
}

/* Sidebar 样式 */
.nav-sidebar {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.sidebar-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  background: white;
  border-left: 4px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sidebar-item:hover {
  background: #f5f7fa;
  transform: translateX(4px);
}

.sidebar-item.active {
  background: #ecf5ff;
}

.sidebar-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.sidebar-item .name {
  font-weight: 500;
  color: #303133;
}

.sidebar-item .count {
  color: #909399;
  font-size: 0.875rem;
}

/* Pills 样式 */
.nav-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.pill-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: #f5f7fa;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  color: #606266;
}

.pill-item:hover {
  background: #e4e7ed;
  transform: translateY(-2px);
}

.pill-item.active {
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.pill-item .count {
  font-size: 0.875rem;
  opacity: 0.8;
}

/* Dropdown 样式 */
.el-dropdown {
  width: 100%;
}

.el-dropdown .el-button {
  width: 100%;
}

@media (max-width: 768px) {
  .nav-tabs {
    gap: 0.25rem;
  }
  
  .tab-item {
    padding: 0.75rem 1rem;
    font-size: 0.875rem;
  }
}
</style>

