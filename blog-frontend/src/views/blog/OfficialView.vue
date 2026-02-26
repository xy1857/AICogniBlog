<template>
  <div class="official-page">
    <h2 class="page-title">官方博客</h2>
    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="5" animated />
    </div>
    <template v-else>
      <el-empty v-if="!articles.length" description="暂无官方公告" />
      <div
        v-else
        v-for="article in articles"
        :key="article.id"
        class="article-card"
        @click="$router.push(`/article/${article.id}`)"
      >
        <div class="article-card-glow" />
        <div class="article-meta">
          <el-tag v-if="article.category" size="small" type="warning">{{ article.category.name }}</el-tag>
          <span class="meta-item"><el-icon><View /></el-icon> {{ article.viewCount }}</span>
          <span class="meta-item"><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
          <span class="meta-date">{{ formatDate(article.publishedAt) }}</span>
        </div>
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
        <div class="article-tags">
          <el-tag v-for="tag in article.tags" :key="tag.id" size="small" class="tag-item">{{ tag.name }}</el-tag>
        </div>
        <div class="article-author">
          <span class="author-avatar">{{ article.author?.nickname?.charAt(0) }}</span>
          <span>作者：{{ article.author?.nickname }}</span>
        </div>
      </div>
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="fetchArticles"
        class="pagination"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'

const articles = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

async function fetchArticles() {
  loading.value = true
  try {
    const res = await articleApi.list({
      page: currentPage.value,
      size: pageSize,
      categorySlug: 'official',
    }) as any
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleDateString('zh-CN') : ''
}

onMounted(fetchArticles)
</script>

<style scoped>
.official-page {
  animation: fadeIn 0.4s ease;
}
.page-title {
  margin: 0 0 24px;
  font-size: 20px;
  color: var(--text-primary);
}
.article-card {
  position: relative;
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  cursor: pointer;
  border: 1px solid var(--border);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-sm);
}
.article-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary);
  box-shadow: var(--shadow-hover);
}
.article-card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity 0.3s;
}
.article-card:hover .article-card-glow {
  opacity: 1;
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 13px;
  color: var(--text-muted);
}
.meta-item { display: flex; align-items: center; gap: 4px; }
.meta-date { margin-left: auto; color: var(--text-muted); }
.article-title { margin: 0 0 8px; font-size: 18px; color: var(--text-primary); }
.article-summary { margin: 0 0 12px; font-size: 14px; color: var(--text-secondary); line-height: 1.6; }
.article-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 12px; }
.tag-item { flex-shrink: 0; }
.article-author { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--text-muted); }
.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
}
.pagination { margin-top: 24px; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
