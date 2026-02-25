<template>
  <div class="home">
    <el-row :gutter="24">
      <!-- 文章列表 -->
      <el-col :span="17">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索文章..." clearable @keyup.enter="search" style="width:280px">
            <template #suffix>
              <el-icon style="cursor:pointer" @click="search"><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <div v-if="loading" class="loading-wrap">
          <el-skeleton :rows="5" animated />
        </div>

        <template v-else>
          <el-empty v-if="!articles.length" description="暂无文章" />
          <div v-for="article in articles" :key="article.id" class="article-card" @click="$router.push(`/article/${article.id}`)">
            <div class="article-meta">
              <el-tag v-if="article.category" size="small" type="info">{{ article.category.name }}</el-tag>
              <span class="meta-item"><el-icon><View /></el-icon> {{ article.viewCount }}</span>
              <span class="meta-item"><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
              <span class="meta-date">{{ formatDate(article.publishedAt) }}</span>
            </div>
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
            <div class="article-tags">
              <el-tag v-for="tag in article.tags" :key="tag.id" size="small" style="margin-right:6px">{{ tag.name }}</el-tag>
            </div>
            <div class="article-author">
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
            style="margin-top:20px;justify-content:center;display:flex"
          />
        </template>
      </el-col>

      <!-- 侧边栏 -->
      <el-col :span="7">
        <el-card class="side-card">
          <template #header><b>文章分类</b></template>
          <el-tag
            v-for="cat in categories" :key="cat.id"
            :type="selectedCategory === cat.id ? 'primary' : 'info'"
            style="margin:4px;cursor:pointer"
            @click="filterByCategory(cat.id)"
          >{{ cat.name }}（{{ cat.article_count }}）</el-tag>
          <el-tag type="warning" style="margin:4px;cursor:pointer" v-if="selectedCategory" @click="filterByCategory(null)">清除筛选</el-tag>
        </el-card>

        <el-card class="side-card" style="margin-top:16px">
          <template #header><b>标签云</b></template>
          <el-tag
            v-for="tag in tags" :key="tag.id"
            style="margin:4px;cursor:pointer"
            @click="filterByTag(tag.id)"
          >{{ tag.name }}</el-tag>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'

const articles = ref<any[]>([])
const categories = ref<any[]>([])
const tags = ref<any[]>([])
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
  const [catRes, tagRes] = await Promise.all([articleApi.categories(), articleApi.tags()]) as any[]
  categories.value = catRes.data
  tags.value = tagRes.data
})
</script>

<style scoped>
.search-bar { margin-bottom: 16px; }
.article-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  border: 1px solid #f0f0f0;
  transition: box-shadow .2s;
}
.article-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }
.article-meta { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; font-size: 13px; color: #999; }
.meta-item { display: flex; align-items: center; gap: 3px; }
.meta-date { margin-left: auto; }
.article-title { font-size: 18px; font-weight: 600; margin: 0 0 8px; color: #222; }
.article-summary { color: #666; font-size: 14px; line-height: 1.7; margin-bottom: 10px; }
.article-tags { margin-bottom: 8px; }
.article-author { font-size: 13px; color: #999; }
.side-card { border-radius: 8px; }
.loading-wrap { padding: 20px; }
</style>
