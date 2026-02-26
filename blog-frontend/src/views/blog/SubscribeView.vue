<template>
  <div class="subscribe-page">
    <h2 class="page-title">我的订阅</h2>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="订阅内容" name="feed">
        <div v-if="feedLoading" class="loading-wrap">
          <el-skeleton :rows="5" animated />
        </div>
        <template v-else>
          <el-empty v-if="!feedArticles.length" description="暂无订阅内容，请先添加分类或标签订阅" />
          <div v-else>
            <div
              v-for="article in feedArticles"
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
            </div>
            <el-pagination
              v-if="feedTotal > feedPageSize"
              background
              layout="prev, pager, next"
              :total="feedTotal"
              :page-size="feedPageSize"
              v-model:current-page="feedPage"
              @current-change="fetchFeed"
              class="pagination"
            />
          </div>
        </template>
      </el-tab-pane>
      <el-tab-pane label="管理订阅" name="manage">
        <div class="subscriptions-list">
          <div v-for="sub in subscriptions" :key="sub.id" class="sub-item">
            <el-tag :type="sub.targetType === 'category' ? 'primary' : 'success'">
              {{ sub.targetType === 'category' ? '分类' : '标签' }}：{{ sub.targetName }}
            </el-tag>
            <el-button type="danger" text size="small" @click="unsubscribe(sub)">取消订阅</el-button>
          </div>
          <el-empty v-if="!subscriptions.length && !subLoading" description="暂无订阅" />
        </div>
        <el-divider>添加订阅</el-divider>
        <p class="hint">在首页通过分类或标签筛选时，可点击订阅；也可在此选择分类/标签添加订阅。</p>
        <div class="add-sub">
          <el-select v-model="addTargetType" placeholder="类型" style="width:100px">
            <el-option label="分类" value="category" />
            <el-option label="标签" value="tag" />
          </el-select>
          <el-select
            v-model="addTargetId"
            :placeholder="addTargetType === 'category' ? '选择分类' : '选择标签'"
            filterable
            style="width:200px; margin-left:8px"
          >
            <el-option
              v-for="c in (addTargetType === 'category' ? categories : tags)"
              :key="c.id"
              :label="c.name"
              :value="addTargetType === 'category' ? c.id : c.id"
            />
          </el-select>
          <el-button type="primary" style="margin-left:8px" @click="subscribe">添加订阅</el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { subscriptionApi } from '@/api/subscription'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'

const activeTab = ref('feed')
const subscriptions = ref<any[]>([])
const feedArticles = ref<any[]>([])
const feedTotal = ref(0)
const feedPage = ref(1)
const feedPageSize = 10
const feedLoading = ref(false)
const subLoading = ref(false)
const categories = ref<any[]>([])
const tags = ref<any[]>([])
const addTargetType = ref<'category' | 'tag'>('category')
const addTargetId = ref<number | null>(null)

async function fetchSubscriptions() {
  subLoading.value = true
  try {
    const res = await subscriptionApi.list() as any
    subscriptions.value = res.data ?? []
  } finally {
    subLoading.value = false
  }
}

async function fetchFeed() {
  feedLoading.value = true
  try {
    const res = await subscriptionApi.articles({
      page: feedPage.value,
      size: feedPageSize,
    }) as any
    feedArticles.value = res.data.records ?? []
    feedTotal.value = res.data.total ?? 0
  } finally {
    feedLoading.value = false
  }
}

async function subscribe() {
  if (addTargetId.value == null) return ElMessage.warning('请选择分类或标签')
  try {
    await subscriptionApi.subscribe({
      targetType: addTargetType.value,
      targetId: addTargetId.value,
    })
    ElMessage.success('订阅成功')
    fetchSubscriptions()
    fetchFeed()
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message ?? '订阅失败')
  }
}

async function unsubscribe(sub: any) {
  try {
    await subscriptionApi.unsubscribe({
      targetType: sub.targetType,
      targetId: sub.targetId,
    })
    ElMessage.success('已取消订阅')
    fetchSubscriptions()
    fetchFeed()
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message ?? '操作失败')
  }
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleDateString('zh-CN') : ''
}

onMounted(async () => {
  const [catRes, tagRes] = await Promise.all([articleApi.categories(), articleApi.tags()]) as any[]
  categories.value = catRes.data ?? []
  tags.value = tagRes.data ?? []
  fetchSubscriptions()
  fetchFeed()
})
watch(activeTab, (t) => { if (t === 'feed') fetchFeed() })
</script>

<style scoped>
.subscribe-page {
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
  transition: all 0.3s;
}
.article-card:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-sm);
}
.article-card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  opacity: 0;
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
.meta-date { margin-left: auto; }
.article-title { margin: 0 0 8px; font-size: 18px; color: var(--text-primary); }
.article-summary { margin: 0 0 12px; font-size: 14px; color: var(--text-secondary); }
.article-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-item { flex-shrink: 0; }
.pagination { margin-top: 24px; }
.subscriptions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.sub-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
}
.hint {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 16px;
}
.add-sub {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
