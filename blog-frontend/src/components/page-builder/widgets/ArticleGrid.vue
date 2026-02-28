<template>
  <div class="article-grid" :style="{ '--columns': props.columns }">
    <!-- 加载状态 -->
    <div v-if="loading && articles.length === 0" class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <!-- 文章列表 -->
    <div 
      v-for="article in displayArticles" 
      :key="article.id"
      class="article-card"
      :class="[`style-${props.cardStyle}`, `hover-${props.hoverEffect}`]"
      @click="handleArticleClick(article)"
    >
      <div v-if="props.showCover && article.cover" class="article-cover">
        <img :src="article.cover" :alt="article.title">
      </div>
      
      <div class="article-body">
        <h3 class="article-title">{{ article.title }}</h3>
        
        <div v-if="props.showTags && article.tags?.length" class="article-tags">
          <span v-for="tag in article.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
        
        <p v-if="props.showExcerpt" class="article-excerpt">
          {{ article.excerpt }}
        </p>
        
        <div class="article-meta">
          <span v-if="props.showDate" class="meta-item">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(article.date) }}
          </span>
          <span v-if="props.showReadCount" class="meta-item">
            <el-icon><View /></el-icon>
            {{ article.readCount }}
          </span>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="props.loadType === 'pagination' && !loading" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="props.itemsPerPage"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 无限滚动加载更多 -->
    <div v-if="props.loadType === 'infinite' && hasMore && !loading" class="load-more">
      <el-button @click="loadMore" :loading="loading">加载更多</el-button>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && articles.length === 0" class="empty-state">
      <el-icon class="empty-icon"><Document /></el-icon>
      <p>暂无文章</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Calendar, View, Loading, Document } from '@element-plus/icons-vue';
import { articleApi } from '@/api/article';
import type { ArticleGridProps } from '@/types/page-builder';

const props = withDefaults(defineProps<ArticleGridProps>(), {
  columns: 3,
  cardStyle: 'default',
  showCover: true,
  showExcerpt: true,
  showTags: true,
  showDate: true,
  showReadCount: true,
  loadType: 'pagination',
  itemsPerPage: 9,
  hoverEffect: 'lift',
});

const currentPage = ref(1);
const loadedCount = ref(props.itemsPerPage);
const articles = ref<any[]>([]);
const loading = ref(false);
const total = ref(0);

// 加载文章数据
const loadArticles = async () => {
  loading.value = true;
  try {
    const response = await articleApi.getArticles({
      page: currentPage.value,
      size: props.itemsPerPage,
      status: 1, // 只显示已发布的文章
    });
    
    // 转换数据格式
    articles.value = response.content.map((article: any) => ({
      id: article.id,
      title: article.title,
      excerpt: article.summary || article.contentMd?.substring(0, 150) + '...',
      cover: article.coverUrl || `https://picsum.photos/400/250?random=${article.id}`,
      tags: article.tags || [],
      date: article.publishedAt || article.createdAt,
      readCount: article.viewCount || 0,
    }));
    
    total.value = response.totalElements;
  } catch (error) {
    console.error('Failed to load articles:', error);
    // 降级到模拟数据
    articles.value = getMockArticles();
  } finally {
    loading.value = false;
  }
};

// 模拟数据（作为降级方案）
const getMockArticles = () => [
  {
    id: 1,
    title: 'Vue 3 组合式 API 最佳实践',
    excerpt: '深入探讨 Vue 3 Composition API 的使用技巧和最佳实践，帮助你写出更优雅的代码...',
    cover: 'https://picsum.photos/400/250?random=1',
    tags: ['Vue', 'JavaScript'],
    date: '2024-01-15',
    readCount: 1234,
  },
  {
    id: 2,
    title: 'TypeScript 高级类型系统详解',
    excerpt: '从基础到进阶，全面解析 TypeScript 的类型系统，让你的代码更加类型安全...',
    cover: 'https://picsum.photos/400/250?random=2',
    tags: ['TypeScript', '前端'],
    date: '2024-01-10',
    readCount: 856,
  },
  {
    id: 3,
    title: '构建高性能的 Web 应用',
    excerpt: '性能优化是前端开发的重要课题，本文分享一些实用的性能优化技巧...',
    cover: 'https://picsum.photos/400/250?random=3',
    tags: ['性能优化', 'Web'],
    date: '2024-01-05',
    readCount: 2341,
  },
  {
    id: 4,
    title: 'CSS Grid 布局完全指南',
    excerpt: 'CSS Grid 是现代网页布局的强大工具，掌握它可以让你的布局更加灵活...',
    cover: 'https://picsum.photos/400/250?random=4',
    tags: ['CSS', '布局'],
    date: '2024-01-01',
    readCount: 678,
  },
  {
    id: 5,
    title: 'React Hooks 深度解析',
    excerpt: '理解 React Hooks 的工作原理，避免常见的陷阱和错误...',
    cover: 'https://picsum.photos/400/250?random=5',
    tags: ['React', 'Hooks'],
    date: '2023-12-28',
    readCount: 1567,
  },
  {
    id: 6,
    title: '前端工程化实践指南',
    excerpt: '从构建工具到 CI/CD，全面介绍前端工程化的最佳实践...',
    cover: 'https://picsum.photos/400/250?random=6',
    tags: ['工程化', '前端'],
    date: '2023-12-20',
    readCount: 934,
  },
];

const displayArticles = computed(() => {
  if (props.loadType === 'pagination') {
    return articles.value;
  } else {
    return articles.value.slice(0, loadedCount.value);
  }
});

const hasMore = computed(() => {
  return loadedCount.value < total.value;
});

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN');
};

const handleArticleClick = (article: any) => {
  console.log('Article clicked:', article);
  // 这里可以跳转到文章详情页
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadArticles();
};

const loadMore = async () => {
  if (props.loadType === 'infinite' && hasMore.value) {
    currentPage.value++;
    loading.value = true;
    try {
      const response = await articleApi.getArticles({
        page: currentPage.value,
        size: props.itemsPerPage,
        status: 1,
      });
      
      const newArticles = response.content.map((article: any) => ({
        id: article.id,
        title: article.title,
        excerpt: article.summary || article.contentMd?.substring(0, 150) + '...',
        cover: article.coverUrl || `https://picsum.photos/400/250?random=${article.id}`,
        tags: article.tags || [],
        date: article.publishedAt || article.createdAt,
        readCount: article.viewCount || 0,
      }));
      
      articles.value.push(...newArticles);
      loadedCount.value = articles.value.length;
    } catch (error) {
      console.error('Failed to load more articles:', error);
    } finally {
      loading.value = false;
    }
  }
};

onMounted(() => {
  loadArticles();
});
</script>

<style scoped>
.article-grid {
  display: grid;
  grid-template-columns: repeat(var(--columns), 1fr);
  gap: 2rem;
  padding: 2rem 0;
}

.article-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.article-card.hover-lift:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.article-card.hover-zoom:hover .article-cover img {
  transform: scale(1.1);
}

.article-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-body {
  padding: 1.5rem;
}

.article-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #303133;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.tag {
  padding: 0.25rem 0.75rem;
  background: #f0f2f5;
  color: #606266;
  border-radius: 4px;
  font-size: 0.875rem;
}

.article-excerpt {
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.6;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 1rem;
  color: #909399;
  font-size: 0.875rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

/* 卡片样式变体 */
.style-minimal {
  box-shadow: none;
  border: 1px solid #e4e7ed;
}

.style-magazine .article-cover {
  height: 250px;
}

.style-magazine .article-title {
  font-size: 1.5rem;
}

/* 分页 */
.pagination {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.load-more {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.loading-state,
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #909399;
}

.loading-state .el-icon,
.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.loading-state .el-icon {
  color: #409EFF;
}

@media (max-width: 1024px) {
  .article-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .article-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}
</style>

