<template>
  <div class="home">
    <el-row :gutter="24">
      <!-- 文章列表 -->
      <el-col :span="17">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索文章..."
            clearable
            @keyup.enter="search"
            class="search-input"
            size="large"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
            <template #suffix>
              <el-icon class="search-btn" @click="search"><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <div v-if="loading" class="loading-wrap">
          <el-skeleton :rows="5" animated />
        </div>

        <template v-else>
          <el-empty v-if="!articles.length" description="暂无文章" />
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card"
            @click="$router.push(`/article/${article.id}`)"
          >
            <div class="article-card-glow" />
            <div class="article-meta">
              <el-tag v-if="article.category" size="small" type="info">{{ article.category.name }}</el-tag>
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
      </el-col>

      <!-- 侧边栏 -->
      <el-col :span="7">
        <el-card class="side-card side-card--categories">
          <template #header>
            <span class="side-card-title"><el-icon><Document /></el-icon> 文章分类</span>
          </template>
          <div class="tag-cloud">
            <el-tag
              v-for="cat in categories"
              :key="cat.id"
              :type="selectedCategory === cat.id ? 'primary' : 'info'"
              class="cloud-tag"
              @click="filterByCategory(cat.id)"
            >{{ cat.name }}（{{ cat.article_count }}）</el-tag>
            <el-tag type="warning" class="cloud-tag" v-if="selectedCategory" @click="filterByCategory(null)">清除筛选</el-tag>
          </div>
        </el-card>

        <el-card class="side-card side-card--tags">
          <template #header>
            <span class="side-card-title"><el-icon><PriceTag /></el-icon> 标签云</span>
          </template>
          <div class="tag-cloud">
            <el-tag
              v-for="tag in tags"
              :key="tag.id"
              class="cloud-tag"
              @click="filterByTag(tag.id)"
            >{{ tag.name }}</el-tag>
          </div>
        </el-card>

        <el-card class="side-card side-card--latest-comments">
          <template #header>
            <router-link to="/comments/latest" class="side-card-title link">
              <el-icon><ChatDotRound /></el-icon> 最新评论
            </router-link>
          </template>
          <div v-if="latestComments.length" class="latest-comments">
            <div
              v-for="c in latestComments"
              :key="c.id"
              class="latest-comment-item"
              @click="$router.push(`/article/${c.article?.id}`)"
            >
              <span class="latest-comment-text">{{ (c.content || '').slice(0, 24) }}{{ (c.content || '').length > 24 ? '…' : '' }}</span>
              <span class="latest-comment-meta">{{ c.user?.nickname }} · {{ c.article?.title }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无评论" :image-size="48" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'

const articles = ref<any[]>([])
const categories = ref<any[]>([])
const tags = ref<any[]>([])
const latestComments = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)
const keyword = ref('')
const selectedCategory = ref<number | null>(null)
const selectedTag = ref<number | null>(null)

async function fetchArticles() {
  loading.value = true
  try {
    const res = await articleApi.list({
      page: currentPage.value,
      size: pageSize,
      categoryId: selectedCategory.value ?? undefined,
      tagId: selectedTag.value ?? undefined,
      keyword: keyword.value || undefined,
    }) as any
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function filterByCategory(id: number | null) {
  selectedCategory.value = id
  selectedTag.value = null
  currentPage.value = 1
  fetchArticles()
}

function filterByTag(id: number) {
  selectedTag.value = id
  selectedCategory.value = null
  currentPage.value = 1
  fetchArticles()
}

function search() {
  currentPage.value = 1
  fetchArticles()
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleDateString('zh-CN') : ''
}

onMounted(async () => {
  fetchArticles()
  const [catRes, tagRes, latestRes] = await Promise.all([
    articleApi.categories(),
    articleApi.tags(),
    commentApi.latest({ limit: 8 }).catch(() => ({ data: [] })),
  ]) as any[]
  categories.value = catRes.data
  tags.value = tagRes.data
  latestComments.value = latestRes.data ?? []
})
</script>

<style scoped>
.home {
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.search-bar {
  margin-bottom: 24px;
}

.search-input {
  max-width: 360px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 12px 20px;
}

.search-icon {
  color: var(--text-muted);
  margin-right: 8px;
}

.search-btn {
  cursor: pointer;
  color: var(--primary);
  transition: all 0.2s;
}

.search-btn:hover {
  transform: scale(1.1);
  color: var(--primary-hover);
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

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-date {
  margin-left: auto;
  color: var(--text-muted);
}

.article-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--text-primary);
  font-family: var(--font-display);
  letter-spacing: 0.5px;
  transition: color 0.2s;
}

.article-card:hover .article-title {
  color: var(--primary);
}

.article-summary {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 16px;
}

.article-tags {
  margin-bottom: 12px;
}

.tag-item {
  margin-right: 8px !important;
  margin-bottom: 4px !important;
}

.article-author {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-muted);
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
}

.side-card {
  border-radius: 16px;
  margin-bottom: 20px;
}

.side-card:last-child {
  margin-bottom: 0;
}

.side-card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cloud-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.cloud-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 12px rgba(14, 165, 233, 0.25);
}

.side-card-title.link {
  text-decoration: none;
  color: inherit;
}
.side-card-title.link:hover {
  color: var(--primary);
}
.latest-comments {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.latest-comment-item {
  font-size: 13px;
  cursor: pointer;
  padding: 6px 0;
  border-bottom: 1px solid var(--border-light);
  transition: color 0.2s;
}
.latest-comment-item:last-child {
  border-bottom: none;
}
.latest-comment-item:hover {
  color: var(--primary);
}
.latest-comment-text {
  display: block;
  color: var(--text-secondary);
  margin-bottom: 4px;
}
.latest-comment-meta {
  font-size: 11px;
  color: var(--text-muted);
}

.pagination {
  margin-top: 32px;
  justify-content: center;
  display: flex;
}

.loading-wrap {
  padding: 24px;
}
</style>
